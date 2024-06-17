package main.java.com.jcv8.framegallery.fileStorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Handles saving and retrieving files from the filesystem.
 * See <a href="https://spring.io/guides/gs/uploading-files">Spring Docs</a>
 */
public interface StorageServiceApi {

    void init();

    void store(Path path, MultipartFile file);

    Stream<Path> loadAll();

    Resource loadAsResource(Path path);

    void deleteAll();

    void delete(Path path) throws IOException;
}