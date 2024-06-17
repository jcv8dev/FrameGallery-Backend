package main.java.com.jcv8.framegallery.image.dataaccess.entity;

import jakarta.persistence.*;
import lombok.*;
import main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;
import main.java.com.jcv8.framegallery.image.dataaccess.entity.helper.PathConverter;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Convert(converter = PathConverter.class)
    private Path path;
    private String title;

    @ManyToMany
    private List<Keyword> keywords;

    @OneToMany
    private List<ImageProperty<?>> imageProperties;

    @Id
    @GeneratedValue
    private UUID id;

    public Image(Path path) {
        this.path = path;
    }
}
