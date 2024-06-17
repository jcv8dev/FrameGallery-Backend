package main.java.com.jcv8.framegallery.image.dataaccess.entity.helper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import main.java.com.jcv8.framegallery.fileStorage.StorageProperties;

import java.nio.file.Path;

@Converter(autoApply = true)
public class PathConverter implements AttributeConverter<Path, String> {

    private final StorageProperties properties = new StorageProperties();

    @Override
    public String convertToDatabaseColumn(Path path) {
        if(path == null){
            return "";
        }
        return properties.getLocation() + "/" + path.getFileName().toString();
    }

    @Override
    public Path convertToEntityAttribute(String s) {
        return Path.of(s);
    }
}
