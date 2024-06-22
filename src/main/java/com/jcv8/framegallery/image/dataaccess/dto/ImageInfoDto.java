package com.jcv8.framegallery.image.dataaccess.dto;

import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;
import com.jcv8.framegallery.image.dataaccess.entity.Keyword;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
public class ImageInfoDto {
    private UUID id;
    private String title;
    private String description;
    private List<Keyword> keywordList;
    private List<ImageProperty<?>> imagePropertyList;
    private boolean published;
    private String filename;

    public ImageInfoDto(Image image) {
        this.id = image.getId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.keywordList = image.getKeywords();
        this.imagePropertyList = image.getImageProperties();
        this.published = image.getPublished();
        this.filename = image.getPath().getFileName().toString();
    }

}
