package main.java.com.jcv8.framegallery.fileStorage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties("storage")
@Component
public class StorageProperties {
    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";

}
