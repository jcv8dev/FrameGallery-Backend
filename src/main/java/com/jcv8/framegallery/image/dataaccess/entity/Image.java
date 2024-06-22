package com.jcv8.framegallery.image.dataaccess.entity;

import jakarta.persistence.*;
import lombok.*;
import com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;
import com.jcv8.framegallery.image.dataaccess.entity.helper.PathConverter;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class Image {

    @Convert(converter = PathConverter.class)
    private Path path;
    private String title;
    private String description;

    @ManyToMany
    private List<Keyword> keywords;

    @OneToMany
    private List<ImageProperty<?>> imageProperties;

    @Id
    private UUID id;

    private Boolean published;

    public Image(Path path) {
        this.path = path;

        // if images already have an uuid file path, they get reassigned that id
        if(filenameIsUUID(path.getFileName())){
            id = UUID.fromString(UuidFromFilename(path.getFileName()));
        } else {
            id = UUID.randomUUID();
        }
    }

    public Image() {
        this.id = UUID.randomUUID();
    }

    private String UuidFromFilename(Path path){
        return path.getFileName().toString().split("\\.")[0];
    }

    private boolean filenameIsUUID(Path path) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z-]{36}");
        Matcher matcher = pattern.matcher(UuidFromFilename(path));
        return matcher.matches();
    }


}
