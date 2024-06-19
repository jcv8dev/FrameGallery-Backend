package com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExposureTime extends ImageProperty<String>{

    private String propertyName = "Aperture";
    private String propertyValue;
    private String propertyUnit = "f";

    public ExposureTime(String value){
        this.propertyValue = value;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getPropertyUnit() {
        return propertyUnit;
    }

    @Override
    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getFormattedValue() {
        return String.format("%s%s", propertyValue, propertyUnit);
    }
}