package com.example.wastesegregationapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView imageView;
    private TextView resultTextView;
    private WasteClassifier wasteClassifier;
    private WasteDao wasteDao;

    private EditText searchEditText;
    private Button searchButton;
    private Button displayAllButton;

    // Declare the ActivityResultLauncher for camera result
    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    displayAndClassifyImage(photo);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        resultTextView = findViewById(R.id.result_text_view);
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        displayAllButton = findViewById(R.id.display_all_button);

        // Initialize WasteDao and WasteClassifier
        WasteDatabase db = WasteDatabase.getDatabase(getApplicationContext());
        wasteDao = db.wasteDao();
        try {
            wasteClassifier = new WasteClassifier(this, "waste_classification_model.tflite");
        } catch (IOException e) {
            Toast.makeText(this, "Error loading model", Toast.LENGTH_SHORT).show();
            resultTextView.setText("Error loading model" + e);
            e.printStackTrace();
        }

        // Set up the search button listener
        searchButton.setOnClickListener(v -> performSearch());

        // Set up the Display All button listener
        displayAllButton.setOnClickListener(v -> displayAllWasteItems());

        // Set up the Take Picture button
        Button takePictureButton = findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            searchWasteItem(query);
        }
    }

    @SuppressLint("SetTextI18n")
    private void searchWasteItem(final String query) {
        // Run the database query on a background thread using ExecutorService
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            WasteItem wasteItem = wasteDao.getWasteItemByName("%" + query + "%");

            // Switch to the main thread to update the UI
            runOnUiThread(() -> {
                if (wasteItem != null) {
                    resultTextView.setText("Item: " + wasteItem.getItemName() + "\n" +
                            "Category: " + wasteItem.getWasteCategory() + "\n" +
                            "Disposal Instructions: " + wasteItem.getDisposalInstructions() + "\n" +
                            "Cleanup Instructions: " + wasteItem.getCleanupInstructions() + "\n" +
                            "Disposal Location: " + wasteItem.getDisposalLocation());
                } else {
                    resultTextView.setText("No results found for " + query);
                }
            });
        });
    }

    private void displayAllWasteItems() {
        // Fetch all waste items and display in the resultTextView
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<WasteItem> wasteItems = wasteDao.getAllWasteItems();
            StringBuilder wasteList = new StringBuilder();
            for (WasteItem item : wasteItems) {
                wasteList.append(item.getItemName()).append("\n");
            }

            runOnUiThread(() -> resultTextView.setText(wasteList.toString()));
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Launch the camera using ActivityResultLauncher
            takePictureLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "Camera app not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAndClassifyImage(Bitmap bitmap) {
        if (bitmap == null) return;

        // Display the captured image in ImageView
        imageView.setImageBitmap(bitmap);

        // Run the classifier and get results
        WasteClassifier.Result result = wasteClassifier.classifyImage(bitmap);

        // Display result as text
        resultTextView.setText(String.format("Predicted: %s (%.2f)", result.label, result.confidence));

        // Search for the classified item in the database
        searchWasteItem(result.label);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required to capture image.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
