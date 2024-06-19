package com.jcv8.framegallery.user.dataaccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jcv8.framegallery.user.dataaccess.entity.UserInfo;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByUsername(String username);
}
