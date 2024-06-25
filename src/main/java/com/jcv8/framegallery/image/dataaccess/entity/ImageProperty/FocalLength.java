package com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FocalLength extends ImageProperty<String>{

    private String name_ = "Focal Length";
    private String value_;
    private String unit_ = "mm";

    public FocalLength(String propertyValue){
        this.value_ = propertyValue;
    }

    @Override
    public String getName_() {
        return name_;
    }

    @Override
    public String getUnit_() {
        return unit_;
    }

    @Override
    public String getValue_() {
        return value_;
    }

    @Override
    public String getFormattedValue_() {
        return String.format("%s%s", value_, unit_);
    }
}