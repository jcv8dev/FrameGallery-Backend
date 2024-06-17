package main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExposureTime extends ImageProperty<String>{

    private String name = "Aperture";
    private String value;
    private String unit = "f";

    public ExposureTime(String value){
        this.value = value;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getFormattedValue() {
        return String.format("%s%s", value, unit);
    }
}