package main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Aperture extends ImageProperty<Double>{

    private String name = "Aperture";
    private Double propertyValue;
    private String unit = "f";

    public Aperture(double propertyValue){
        this.propertyValue = propertyValue;
    }

    @Override
    public String getPropertyName() {
        return name;
    }

    @Override
    public String getPropertyUnit() {
        return unit;
    }

    @Override
    public Double getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getFormattedValue() {
        return String.format("%s%s", unit, propertyValue);
    }
}