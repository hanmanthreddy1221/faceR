package com.fc.facerec.bpo;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.IOException;

public class ImageClassifierValidator {
    public ImageClassifierValidator(String imagePath) throws IOException {
        // Model path (where your JSON model is saved)
        String modelPath = "/Users/hanmanthreddy/NerdsEdge/Backend/DataForImage/DataModels/Hanmanth.json";

        // Load the model
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(modelPath, true);

        // Image dimensions and channels (must match the model's input requirements)
        int height = 224;  // Adjust to match the model's input size
        int width = 224;   // Adjust to match the model's input size
        int channels = 3;  // Adjust to match the model's input requirements

        // Data preparation
        FileSplit fileSplit = new FileSplit(new File(imagePath));
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        InputSplit[] data = fileSplit.sample(null, 1.0, 0.0); // Use 100% for validation

        ImageRecordReader recordReader = new ImageRecordReader(height, width, channels, labelMaker);
        recordReader.initialize(data[0]);
        DataSetIterator testIter = new RecordReaderDataSetIterator(recordReader, 1);

        // Perform the validation
        Evaluation evaluation = model.evaluate(testIter);
        System.out.println("Evaluation Results:");
        System.out.println(evaluation.stats());
    }
}
