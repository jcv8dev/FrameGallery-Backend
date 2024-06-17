package main.java.com.jcv8.framegallery.image.dataaccess.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Keyword {

    private String keyword;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    List<Image> imageList;
}
