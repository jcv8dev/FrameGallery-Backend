package com.jcv8.framegallery.image.dataaccess.repository;

import com.jcv8.framegallery.image.dataaccess.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    @Query("SELECT i from Image i where i.path = :queryPath")
    Optional<Image> findByPath(@Param("queryPath") Path path);
}
