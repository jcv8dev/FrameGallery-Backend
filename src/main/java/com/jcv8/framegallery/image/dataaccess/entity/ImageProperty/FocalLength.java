package com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FocalLength extends ImageProperty<Double>{

    private String propertyName = "Focal Length";
    private Double propertyValue;
    private String propertyUnit = "mm";

    public FocalLength(double propertyValue){
        this.propertyValue = propertyValue;
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
    public Double getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getFormattedValue() {
        return String.format("%s%s", propertyUnit, propertyValue);
    }
}