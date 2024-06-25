package com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ImageDatetime extends ImageProperty<String>{

    private String name_ = "Time";
    private String value_;
    private String unit_ = "";

    public ImageDatetime(String value){
        this.value_ = value;
    }

    @Override
    String getName_() {
        return name_;
    }

    @Override
    String getUnit_() {
        return "YYYY-MM-DD HH:MM:SS";
    }

    @Override
    String getValue_() {
        return value_;
    }

    @Override
    String getFormattedValue_() {
        return value_;
    }
}
