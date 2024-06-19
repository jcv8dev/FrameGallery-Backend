package com.jcv8.framegallery.image.dataaccess.repository;

import com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImagePropertyRepository extends JpaRepository<ImageProperty<?>, UUID> {
}
