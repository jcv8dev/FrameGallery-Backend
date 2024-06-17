package main.java.com.jcv8.framegallery.image.facade;

import main.java.com.jcv8.framegallery.image.dataaccess.entity.Image;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller(value = "/api/rest/v1/image")
public class ImageController {
    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllImages() {
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<Image>());
    }
}
