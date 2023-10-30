package com.fc.facerec.controller;

import com.fc.facerec.service.FaceRecService;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class FaceRecController {

    @Autowired
    private FaceRecService faceRecService;

    @GetMapping("/identify")
    private String identifyFace(@RequestParam String path) throws IOException {
        return faceRecService.identify(path);
    }

    @GetMapping("/classify")
    private String classify(@RequestParam String path, @RequestParam String name) throws IOException {
        return faceRecService.classify(path, name);
    }

    @GetMapping("/capture")
    private String capture(@RequestParam String path, @RequestParam String name,@RequestParam int count) throws InterruptedException, FrameGrabber.Exception {
        return faceRecService.capture(path,name,count);
    }
}
