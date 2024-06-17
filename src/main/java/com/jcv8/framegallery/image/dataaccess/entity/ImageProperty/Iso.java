package main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Iso extends ImageProperty<Integer>{

    private String propertyName = "ISO";
    private Integer propertyValue;
    private String propertyUnit = "";

    public Iso(int value){
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
    public Integer getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getFormattedValue() {
        return String.format("%s", propertyValue);
    }
}