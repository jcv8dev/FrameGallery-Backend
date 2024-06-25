package com.jcv8.framegallery.user.facade;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/rest/v1/artist")
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

    @PostMapping("/auth/register")
    public ResponseEntity<?> addNewUser(@RequestBody UserInfo userInfo) {
        logger.info("Request to register new User " + userInfo);
        try{
            UserInfo newUser = service.addUser(userInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
    }

    @PostMapping("/auth/login")
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
    @GetMapping("/auth/onboarding")
    public ResponseEntity<?> onboarding() {
        if(userInfoService.hasOnboarded()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> artistInfo(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(userInfoService.getArtistInfo());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }
}
