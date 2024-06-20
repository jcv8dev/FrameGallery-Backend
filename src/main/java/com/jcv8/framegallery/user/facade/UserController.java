package com.jcv8.framegallery.user.facade;

import com.jcv8.framegallery.user.dataaccess.repository.UserInfoRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.jcv8.framegallery.configuration.JwtService;
import com.jcv8.framegallery.user.dataaccess.dto.AuthRequestDto;
import com.jcv8.framegallery.user.dataaccess.entity.UserInfo;
import com.jcv8.framegallery.user.logic.UserInfoService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rest/v1/auth")
public class UserController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UserController.class);
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private UserInfoService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody UserInfo userInfo) {
        logger.info("Request to register new User " + userInfo);
        UserInfo newUser = service.addUser(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDto authRequest) {
        logger.info("Login request from " + authRequest.getUsername());
        try{

            Authentication authentication = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            authenticationManager.authenticate(authentication);
            String jwt = jwtService.generateToken(authRequest.getUsername());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", jwt);
            logger.info("Authentication Success");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            logger.info("Authentication Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Lets the frontend check whether to allow the onboarding page to load
     * @return true, if there is no registered user
     */
    @GetMapping("/onboarding")
    public ResponseEntity<?> onboarding() {
        if(userInfoService.hasOnboarded()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("");
        }
    }
}
