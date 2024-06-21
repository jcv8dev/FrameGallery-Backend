package com.jcv8.framegallery.image.facade;

import com.jcv8.framegallery.configuration.JwtService;
import com.jcv8.framegallery.image.dataaccess.entity.Image;
import com.jcv8.framegallery.image.logic.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping(path = "/api/rest/v1/image")
@CrossOrigin(origins = "*")
public class ImageController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ImageController.class);
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint for retrieving filenames of images
     * @param token optional, allows to see unpublished images
     * @param showAll optional, selects whether to see unpublished images when authenticated
     * @return a list of Strings representing filenames
     */
    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllImageFilename( @RequestHeader(name="Authorization", required = false) String token, @RequestParam(required = false) Boolean showAll) {
        if(token != null){
            String username = jwtService.extractUsername(token.split(" ")[1]);
            if(showAll != null && showAll && userDetailsService.loadUserByUsername(username) != null){
                logger.info("Retrieving all image paths");
                return ResponseEntity.status(HttpStatus.OK).body(imageService.getAllImageFilename());
            }
        }

        logger.info("Retrieving all published paths");
        return ResponseEntity.status(HttpStatus.OK).body(imageService.getAllPublishedImageFilename());

    }

    @PutMapping(value = "/add")
    public ResponseEntity<?> addImage(@RequestParam("image") MultipartFile image) {
        try{
            Image savedImage = imageService.saveImage(image);
            logger.log(Level.INFO, "Adding image " + image.getOriginalFilename() + " as " + savedImage.getPath());
            return ResponseEntity.status(HttpStatus.OK).body(savedImage);
        } catch (InvalidPathException e ){
            logger.log(Level.WARNING, "Add Image Request with invalid Path");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Add Image Request with empty file");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/{id:[0-9a-zA-Z-]{36}}")
    public ResponseEntity<?> getImageById(@PathVariable("id") UUID id) {
        try{
            Resource image = imageService.getImageById(id);
            logger.log(Level.INFO, "Retrieving image " + image.getFilename());
            return ResponseEntity.status(HttpStatus.OK).body(image);
        } catch (NoSuchFileException e) {
            logger.log(Level.WARNING, "Request for non-existing image with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(value = "/{filename:[0-9a-zA-Z-]{36}\\.[a-zA-Z]{3,4}}")
    public ResponseEntity<?> getImageById(@PathVariable("filename") String filename, HttpServletRequest request) {
        try{
            Resource image = imageService.getImageByFilename(filename);

            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(image.getFile().getAbsolutePath());
            } catch (IOException ex) {
                logger.info("Could not determine file type.");
            }

            // Fallback to the default content type if type could not be determined
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            logger.info("Request for " + filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image);

        } catch (NoSuchFileException e) {
            logger.log(Level.WARNING, "Request for non-existing image " + filename);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteImageById(@PathVariable("id") UUID id) {
        try{
            imageService.deleteImageById(id);
            logger.log(Level.INFO, "Deleting image with id " + id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted image " + id);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Delete request for non-existing image with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(value = "/orphans")
    public ResponseEntity<?> getOrphanImages() {
        List<String> fileNames = imageService.getOrphans().stream().map(path -> path.getFileName().toString()).toList();
        return ResponseEntity.status(HttpStatus.OK).body(fileNames);
    }

    @PostMapping(value = "/orphans/index")
    public ResponseEntity<?> reIndexOrphans() {
        imageService.indexOrphans();
        return ResponseEntity.status(HttpStatus.OK).body(imageService.getOrphans());
    }

    @DeleteMapping(value = "/orphans")
    public ResponseEntity<?> deleteOrphanImages() {
        try{
            List<String> fileNames = imageService.getOrphans().stream().map(path -> path.getFileName().toString()).toList();
            imageService.deleteOrphans();
            return ResponseEntity.status(HttpStatus.OK).body(fileNames);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Delete request for non-existing orphan images");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not delete all orphans");
        }
    }
}
