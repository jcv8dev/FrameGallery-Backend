package com.jcv8.framegallery.image.logic;

import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.dataaccess.repository.ImageRepository;
import com.jcv8.framegallery.fileStorage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Handles saving and retrieving Images from the filesystem as well as their properties in the database
 * */
@Service
public class ImageService {

    private final StorageService storageService;

    @Autowired
    public ImageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Saves a given MultipartFile to the file system.
     * The location is determined by the storageService, the file name is generated from the files uuid and the original extension.
     * @param file The file to be saved
     * @return the file's database entry, containing the path and other properties
     * @throws InvalidPathException when the original path of the given file does not contain an extension
     * @throws NullPointerException when the given file is null (thrown by the getOriginalFilename() call)
     */
    public Image saveImage(MultipartFile file) throws InvalidPathException, NullPointerException {
        Image image = new Image();
        imageRepository.save(image);
        Path imagePath = generateUUIDPath(image.getId(), file.getOriginalFilename());
        storageService.store(imagePath, file);
        image.setPath(imagePath);
        image.setPublished(false); // default: image not publicly visible
        return imageRepository.save(image);
    }

    /**
     * Generates a unique file name from an uuid and a given filename
     * @param id the uuid of the file, which has been generated by the repository beforehand
     * @param filename the original filename of the file. This is used to extract the extension
     * @return a unique file name
     * @throws InvalidPathException when the file name does not contain a file extension
     */
    private Path generateUUIDPath(UUID id, String filename) throws InvalidPathException {
        Pattern pattern = Pattern.compile("\\.([^.]+)$");
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            String extension = matcher.group(1);
            return Path.of(id + "." + extension);
        } else {
            throw new InvalidPathException(filename, "File does not have a extension");
        }
    }

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

    public Resource getImageByFilename(String filename) throws NoSuchFileException {
        UUID uuid = getUUIDFromPath(Path.of(filename));
        return getImageById(uuid);
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

    /**
     * @return a list of filenames of all images in the database
     */
    public List<String> getAllImageFilename() {
        List<Image> images = imageRepository.findAll();
        for (Image image : images) {
            System.out.println(image.getPath().getFileName());
        }
        return images.stream().map(image -> image.getPath().getFileName().toString()).collect(Collectors.toList());
    }

    /**
     * @return a list of filenames of all images in the database where published is set to true
     */
    public List<String> getAllPublishedImageFilename() {
        List<Path> paths = getAllImagePaths();
        List<String> publishedImageFilenames = new ArrayList<>();
        for (Path path : paths) {
            if(imageRepository.findByPath(path).isPresent()){
                if(imageRepository.findByPath(path).get().getPublished()) {
                    publishedImageFilenames.add(path.getFileName().toString());
                }
            }
        }
        return publishedImageFilenames;
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
     * @return a list of Paths corresponding to all files without an entry in the database
     */
    public List<Path> getOrphans() {
        List<Path> imagePaths = storageService.loadAll().toList();
        List<Path> orphans = new ArrayList<>();
        for (Path imagePath : imagePaths) {
            UUID uuid = getUUIDFromPath(imagePath);
            if(imageRepository.findById(uuid).isEmpty()){
                orphans.add(imagePath);
            }
        }
        return orphans;
    }

    /**
     * Extracts the UUID from a filename (e.g. removes the file extension)
     * @return the UUID as String
     */
    private UUID getUUIDFromPath(Path path) {
        Pattern pattern = Pattern.compile("^[^.]+");
        Matcher matcher = pattern.matcher(path.toString());
        System.out.println(path);
        if (matcher.find()) {
            return UUID.fromString(matcher.group());
        }
        throw new InvalidPathException(path.toString(), "File does not have an extension");
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


    public List<Image> getAllImage() {
        return imageRepository.findAll();
    }
}
