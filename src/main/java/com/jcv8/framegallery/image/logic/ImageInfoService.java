package com.jcv8.framegallery.image.logic;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.jcv8.framegallery.fileStorage.StorageService;
import com.jcv8.framegallery.image.dataaccess.dto.ImageInfoRequestDto;
import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.entity.ImageProperty.*;
import com.jcv8.framegallery.image.dataaccess.entity.Keyword;
import com.jcv8.framegallery.image.dataaccess.repository.ImagePropertyRepository;
import com.jcv8.framegallery.image.dataaccess.repository.ImageRepository;
import com.jcv8.framegallery.image.dataaccess.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ImageInfoService {

    private Logger logger = Logger.getLogger(ImageInfoService.class.getName());

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
    public void setImageInfo(UUID id, ImageInfoRequestDto newImageInfo) throws FileNotFoundException {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isPresent()) {
            Image imageData = image.get();


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
            try{
                orphanImage.setImageProperties(readFileMetadata(storageService.loadAsResource(orphan)));
            } catch (IOException | ImageProcessingException | MetadataException e) {
                logger.warning(e.getMessage());
            }
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


    public List<ImageProperty<?>> readFileMetadata(Resource file) throws ImageProcessingException, MetadataException, IOException {
        List<ImageProperty<?>> properties = new ArrayList<>();

        Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());

        Directory ifdDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if(ifdDirectory != null){
            if(ifdDirectory.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)) {
                properties.add(new ExposureTime(ifdDirectory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)));
            }

            if(ifdDirectory.containsTag(ExifSubIFDDirectory.TAG_FNUMBER)) {
                properties.add(new Aperture(ifdDirectory.getString(ExifSubIFDDirectory.TAG_FNUMBER)));
            }

            if(ifdDirectory.containsTag(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)){
                properties.add(new FocalLength(ifdDirectory.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)));
            }

            if(ifdDirectory.containsTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)){
                properties.add(new Iso(ifdDirectory.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)));
            }

            if(ifdDirectory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)){
                properties.add(new ImageDatetime(ifdDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)));
            }

        }

        imagePropertyRepository.saveAll(properties);

        return properties;
    }

}
