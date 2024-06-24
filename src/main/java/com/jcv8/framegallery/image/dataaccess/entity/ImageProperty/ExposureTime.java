package com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExposureTime extends ImageProperty<Double>{

    private String propertyName = "Exposure Time";
    private double propertyValue;
    private String propertyUnit = "s";

    public ExposureTime(double value){
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
    public Double getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getFormattedValue() {
        return String.format("%s%s", propertyValue, propertyUnit);
    }
}