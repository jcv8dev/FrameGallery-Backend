package main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public abstract class ImageProperty<T> {
    @Id
    private Long id;

    abstract String getName();
    abstract String getUnit();
    abstract T getValue();
    abstract String getFormattedValue();

}
