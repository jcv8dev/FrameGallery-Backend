package main.java.com.jcv8.framegallery.image.dataaccess.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Image {
    private String path;
    private String title;

    @ManyToMany
    private List<Keyword> keywords;

    @OneToMany
    private List<ImageProperty<?>> imageProperties;

    @Id
    @GeneratedValue
    private UUID id;

}
