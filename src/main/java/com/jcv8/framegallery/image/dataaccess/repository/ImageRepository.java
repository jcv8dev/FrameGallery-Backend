package com.jcv8.framegallery.image.dataaccess.repository;

import com.jcv8.framegallery.image.dataaccess.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
