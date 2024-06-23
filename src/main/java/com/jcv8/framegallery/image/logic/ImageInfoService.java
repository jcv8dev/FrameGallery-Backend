package com.jcv8.framegallery.image.logic;

import com.jcv8.framegallery.fileStorage.StorageService;
import com.jcv8.framegallery.image.dataaccess.dto.ImageInfoDto;
import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.ImageProperty;
import com.jcv8.framegallery.image.dataaccess.entity.Keyword;
import com.jcv8.framegallery.image.dataaccess.repository.ImagePropertyRepository;
import com.jcv8.framegallery.image.dataaccess.repository.ImageRepository;
import com.jcv8.framegallery.image.dataaccess.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageInfoService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImagePropertyRepository imagePropertyRepository;

    @Autowired
    private StorageService storageService;
    @Autowired
    private KeywordRepository keywordRepository;

    /**
     * Sets the published value of an image
     * @param id the images id
     * @param newImageInfo a DTO containing metadata and settings for the image
     */
    public void setImageInfo(UUID id, ImageInfoDto newImageInfo) throws FileNotFoundException {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isPresent()) {
            Image imageData = image.get();

            // add properties to database before adding to image
            newImageInfo.getImagePropertyList().forEach(this::saveImagePropertyIfAbsent);
            imageData.setImageProperties(newImageInfo.getImagePropertyList());

            // add keywords to database before adding to image
            newImageInfo.getKeywordList().forEach(this::saveKeywordsIfAbsent);
            imageData.setKeywords(newImageInfo.getKeywordList());

            if(newImageInfo.getPublished() != null){
                imageData.setPublished(newImageInfo.getPublished());
            }

            if(newImageInfo.getDescription() != null){
                imageData.setDescription(newImageInfo.getDescription());
            }

            if(newImageInfo.getTitle() != null){
                imageData.setTitle(newImageInfo.getTitle());
            }

            imageRepository.save(image.get());
        } else {
            throw new FileNotFoundException("");
        }
    }


    /**
     * Deletes all files without representation in the database
     * @throws IOException when there is an error in the storageService. Only throws once all delete calls have been made
     */
    public void deleteOrphans() throws IOException {
        boolean exceptionFlag = false; // try every file before throwing the exception
        String exceptionMessage = null;
        List<Path> orphans = getOrphans();
        for (Path orphan : orphans) {
            try{
                storageService.delete(orphan);
            } catch (IOException e){
                exceptionFlag = true;
                exceptionMessage = e.getMessage();
            }
        }
        if(exceptionFlag){
            throw new IOException(exceptionMessage);
        }
    }

    /**
     * @return a list of Paths corresponding to all files without an entry in the database
     */
    public List<Path> getOrphans() {
        List<Path> imagePaths = storageService.loadAll().toList();
        List<Path> orphans = new ArrayList<>();
        for (Path imagePath : imagePaths) {
            UUID uuid = Image.getUUIDFromPath(imagePath);
            if(imageRepository.findById(uuid).isEmpty()){
                orphans.add(imagePath);
            }
        }
        return orphans;
    }

    /**
     * Re-indexes all files without any representation in the database.
     */
    public void indexOrphans() {
        List<Path> orphans = getOrphans();
        for (Path orphan : orphans) {
            Image orphanImage = new Image(orphan);
            orphanImage.setPublished(false);
            imageRepository.save(orphanImage);
        }
    }

    /**
     * @return a list of filenames of all images in the database
     */
    public List<String> getAllImageFilename() {
        List<Image> images = imageRepository.findAll();
        return images.stream().map(image -> image.getPath().getFileName().toString()).collect(Collectors.toList());
    }

    /**
     * @return a list of filenames of all images in the database where published is set to true
     */
    public List<String> getAllPublishedImageFilename() {
        List<Image> images = imageRepository.findAll();
        return images.stream().filter(Image::getPublished).map(image -> image.getPath().getFileName().toString()).collect(Collectors.toList());
    }


    public Image getImageInfoById(UUID id) throws NoSuchFileException {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isEmpty()) {throw new NoSuchFileException("File does not exist");}
        return image.get();
    }

    private void saveImagePropertyIfAbsent(ImageProperty<?> imageProperty){
        if(imagePropertyRepository.findById(imageProperty.getId()).isEmpty()){
            imagePropertyRepository.save(imageProperty);
        }
    }

    private void saveKeywordsIfAbsent(Keyword keyword){
        if(keywordRepository.findById(keyword.getId()).isEmpty()){
            keywordRepository.save(keyword);
        }
    }


}
