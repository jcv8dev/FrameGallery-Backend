package com.jcv8.framegallery.user.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jcv8.framegallery.user.dataaccess.entity.UserInfo;
import com.jcv8.framegallery.user.dataaccess.entity.UserInfoDetails;
import com.jcv8.framegallery.user.dataaccess.repository.UserInfoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Service
public class UserInfoService implements UserDetailsService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = repository.findByUsername(username);

        // Converting userDetail to UserDetails
        return new UserInfoDetails(userDetail.orElse(null));
    }

    public UserInfo addUser(UserInfo userInfo) throws IllegalStateException{
        if(hasOnboarded()){
            throw new IllegalStateException("Only one user can be added");
        }
        logger.info("Adding user " + userInfo);
        if(repository.findByUsername(userInfo.getUsername()).isEmpty()) {
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            return repository.save(userInfo);
        }
        return null;
    }

    public Boolean hasOnboarded() {
        return !repository.findAll().isEmpty();
    }

    public Map<String, String> getArtistInfo() {
        List<UserInfo> artists = repository.findAll();
        Map<String, String> artistInfo = new HashMap<>();
        artistInfo.put("name", artists.get(0).getName());
        return artistInfo;
    }
}