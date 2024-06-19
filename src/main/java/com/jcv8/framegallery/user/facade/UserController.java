package com.jcv8.framegallery.user.facade;

import com.jcv8.framegallery.user.dataaccess.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rest/v1/auth")
public class UserController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private UserInfoService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public UserInfo addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequestDto authRequest) {
        logger.info(authRequest.toString());
        logger.info(userInfoRepository.findAll().toString());
        try{
            Authentication authentication = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            authenticationManager.authenticate(authentication);
            return jwtService.generateToken(authRequest.getUsername());

        } catch (Exception e) {
            return "Invalid username or password";
        }

    }
}
