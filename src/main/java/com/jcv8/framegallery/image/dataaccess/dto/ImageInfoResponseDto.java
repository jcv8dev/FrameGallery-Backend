package com.jcv8.framegallery.image.dataaccess.dto;

import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;
import com.jcv8.framegallery.image.dataaccess.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageInfoResponseDto {
    private UUID id;
    private String title;
    private String description;
    private List<Keyword> keywordList;
    private List<ImageProperty<?>> imagePropertyList;
    private Boolean published;
    private String filename;

    public ImageInfoResponseDto(Image image) {
        this.id = image.getId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.keywordList = image.getKeywords();
        this.imagePropertyList = image.getImageProperties();
        this.published = image.getPublished();
        this.filename = image.getPath().getFileName().toString();
    }

}
