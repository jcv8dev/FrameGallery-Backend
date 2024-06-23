package com.jcv8.framegallery.image.dataaccess.repository;

import com.jcv8.framegallery.image.dataaccess.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
