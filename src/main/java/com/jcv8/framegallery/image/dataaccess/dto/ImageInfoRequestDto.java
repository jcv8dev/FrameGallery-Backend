package com.jcv8.framegallery.image.dataaccess.dto;

import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.entity.Keyword;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoRequestDto {
    private UUID id;
    private String title;
    private String description;
    private List<Keyword> keywordList;
    private Boolean published;
    private String filename;
}
