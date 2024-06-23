package com.jcv8.framegallery.image.logic;

import com.jcv8.framegallery.fileStorage.StorageService;
import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageDownloadService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StorageService storageService;

    /**
     * Retrieves the actual file through the storageService from a given id
     * @param id the files id
     * @return the actual file
     * @throws NoSuchFileException when there is no entry with the given id in the imageRepository
     */
    public Resource getImageById(UUID id) throws NoSuchFileException {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isEmpty()) {throw new NoSuchFileException("File does not exist");}
        return storageService.loadAsResource(image.get().getPath());
    }


    public Resource getImageFileByFilename(String filename) throws NoSuchFileException {
        UUID uuid = Image.getUUIDFromPath(Path.of(filename));
        return getImageById(uuid);
    }

    /**
     * Deletes an image, specified through its id
     * @param id of the image to be deleted
     * @throws IOException when the file does not exist or the storageService has problems due to permissions etc.
     */
    public void deleteImageById(UUID id) throws IOException {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isEmpty()){throw new NoSuchElementException("File does not exist");}
        storageService.delete(image.get().getPath());
    }



    /**
     * @return a list of filesystem paths of all images in the storageServices directory.
     */
    private List<Path> getAllImagePaths() {
        return imageRepository.findAll()
                .stream()
                .map(Image::getPath)
                .collect(Collectors.toList());
    }


    public List<Image> getAllImage() {
        return imageRepository.findAll();
    }
}
