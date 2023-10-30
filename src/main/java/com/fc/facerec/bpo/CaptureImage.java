package com.fc.facerec.bpo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

public class CaptureImage {
    public CaptureImage(String path, int count) throws FrameGrabber.Exception, InterruptedException {

            FrameGrabber grabber = new OpenCVFrameGrabber(0); // 0 represents the default camera


            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            // Create a File object representing the directory
            File folder = new File(path);

            // Check if the directory already exists
            if (!folder.exists()) {
                // If it doesn't exist, create the directory
                folder.mkdirs();
            }
            grabber.start();
             for (int i = 0; i < count; i++) {
                    Frame frame = grabber.grab();

                    IplImage img = converter.convert(frame);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    String timestamp = dateFormat.format(new Date());
                    String filename = path + "/selfie" + timestamp + i + ".jpg";

                    cvSaveImage(filename, img);
                    System.out.println(filename);

                    Thread.sleep(100);
             }
            grabber.stop();


    }
}
