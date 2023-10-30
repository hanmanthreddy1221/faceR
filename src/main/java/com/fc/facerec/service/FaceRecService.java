package com.fc.facerec.service;

import org.bytedeco.javacv.FrameGrabber;

import java.io.IOException;

public interface FaceRecService {

    public String identify(String path) throws IOException;

    String classify(String path, String name) throws IOException;

    String capture(String path, String name,int count) throws InterruptedException, FrameGrabber.Exception;
}
