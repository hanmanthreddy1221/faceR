package com.fc.facerec.bpo;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;
public class ImageClassifierBuilder {

    private static final Logger log = LoggerFactory.getLogger(ImageClassifierBuilder.class);

    public ImageClassifierBuilder(String dataPath, String name) throws IOException {
        // Define the larger image dimensions
        int height = 224;  // Change to the desired height (e.g., 224 for many face classification models)
        int width = 224;   // Change to the desired width

        int channels = 3; // Use 3 channels for RGB images
        int outputNum = 2; // Number of classes (e.g., 2 for binary classification)

        // Hyperparameters
        int batchSize = 64;
        int nEpochs = 10;

        // Data preparation
        FileSplit fileSplit = new FileSplit(new File(dataPath), NativeImageLoader.ALLOWED_FORMATS, null);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        InputSplit[] inputSplit = fileSplit.sample(null, 0.8, 0.2); // Split data into training and test sets

        ImageRecordReader trainRR = new ImageRecordReader(height, width, channels, labelMaker);
        trainRR.initialize(inputSplit[0]);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(trainRR, batchSize, 1, outputNum);

        // Model configuration
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(1e-3))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        .nIn(channels)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutional(height, width, channels))
                .backpropType(BackpropType.Standard)
                .build();

        // Create and train the model
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10)); // Print score every 10 iterations

        log.info("Training the model...");
        for (int i = 0; i < nEpochs; i++) {
            model.fit(trainIter);
        }

        log.info("Training complete.");

        // Evaluate the model
        ImageRecordReader testRR = new ImageRecordReader(height, width, channels, labelMaker);
        testRR.initialize(inputSplit[1]);
        DataSetIterator testIter = new RecordReaderDataSetIterator(testRR, batchSize, 1, outputNum);

        Evaluation eval = model.evaluate(testIter);
        log.info(eval.stats());

        log.info("Model evaluation complete.");

        String modelPath = "/Users/hanmanthreddy/NerdsEdge/Backend/DataForImage/DataModels/" + name + ".json";
        saveModelToJson(model, modelPath);
    }

    private static void saveModelToJson(MultiLayerNetwork model, String filePath) throws IOException {
        // Create a file object with the specified file path
        File file = new File(filePath);

        // Serialize the model's configuration to JSON and save it to the file
        ModelSerializer.writeModel(model, file, true);

        System.out.println("Model saved in JSON format to: " + filePath);
    }
}
