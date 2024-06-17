package com.jcv8.framegallery;


import main.java.com.jcv8.framegallery.FrameGalleryApplication;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = FrameGalleryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class FrameGalleryApplicationTests {

    @Test
    void contextLoads() {
    }

}
