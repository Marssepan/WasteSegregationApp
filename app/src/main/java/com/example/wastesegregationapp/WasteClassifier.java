package com.example.wastesegregationapp;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class WasteClassifier {

    private final Interpreter tflite;
    private final List<String> labels;

    public WasteClassifier(Context context, String modelPath) throws IOException {
        // Load the TFLite model
        tflite = new Interpreter(FileUtil.loadMappedFile(context, modelPath));

        // Load labels from a file, e.g., waste_labels.txt in assets
        labels = FileUtil.loadLabels(context, "waste_labels.txt");
    }

    public Result classifyImage(Bitmap bitmap) {
        // Resize bitmap to the model input size (e.g., 224x224)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        // Convert the bitmap to ByteBuffer
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);

        // Define output tensor shape (1x6, since we have 6 classes)
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, org.tensorflow.lite.DataType.FLOAT32);

        // Run inference
        tflite.run(inputBuffer, outputBuffer.getBuffer().rewind());

        // Process the output
        return getBestResult(outputBuffer.getFloatArray());
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[224 * 224];
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224);

        int pixelIndex = 0;
        for (int i = 0; i < 224; i++) {
            for (int j = 0; j < 224; j++) {
                int pixel = intValues[pixelIndex++];
                byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((pixel & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }

    private Result getBestResult(float[] output) {
        // Identify the label with the highest confidence
        int maxIndex = 0;
        float maxConfidence = 0;
        for (int i = 0; i < output.length; i++) {
            if (output[i] > maxConfidence) {
                maxConfidence = output[i];
                maxIndex = i;
            }
        }
        return new Result(labels.get(maxIndex), maxConfidence);
    }

    public static class Result {
        public final String label;
        public final float confidence;

        public Result(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }
    }
}
