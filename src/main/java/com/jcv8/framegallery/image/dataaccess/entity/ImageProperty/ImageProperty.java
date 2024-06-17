package main.java.com.jcv8.framegallery.image.dataaccess.entity.ImageProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public abstract class ImageProperty<T> {
    @Id
    @GeneratedValue
    private UUID id;

    abstract String getPropertyName();
    abstract String getPropertyUnit();
    abstract T getPropertyValue();
    abstract String getFormattedValue();

}
