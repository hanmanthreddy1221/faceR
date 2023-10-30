package com.fc.facerec.service;

import com.fc.facerec.bpo.CaptureImage;
import com.fc.facerec.bpo.ImageClassifierBuilder;
import com.fc.facerec.bpo.ImageClassifierValidator;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FaceRecServiceImpl implements FaceRecService{

    @Override
    public String identify(String path) throws IOException {
        ImageClassifierValidator imageClassifierValidator=new ImageClassifierValidator(path);
        return "Hello Hanmanth";
    }

    @Override
    public String classify(String path, String name) throws IOException {
        ImageClassifierBuilder imageClassifierBuilder=new ImageClassifierBuilder(path+"/"+name,name);
        return "Classified "+path+"/"+name;
    }

    @Override
    public String capture(String path, String name,int count) throws InterruptedException, FrameGrabber.Exception {
        CaptureImage cpimg=new CaptureImage(path+"/"+name,count);
        return null;
    }
}
