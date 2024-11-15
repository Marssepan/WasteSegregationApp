package com.example.wastesegregationapp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseInitializer {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void populateDatabase(final WasteDatabase db) {
        executorService.execute(() -> {
            WasteDao wasteDao = db.wasteDao();

            // Add initial waste items with detailed instructions
            wasteDao.insert(new WasteItem("Plastic Bottle", "Recyclable: Plastic",
                    "Rinse before recycling.", "Remove the cap before disposal.", "Plastic Recycling Bin"));
            wasteDao.insert(new WasteItem("Glass Bottle", "Recyclable: Glass",
                    "Rinse and remove labels.", "Wash thoroughly before recycling.", "Glass Recycling Bin"));
            wasteDao.insert(new WasteItem("Paper Bag", "Recyclable: Paper",
                    "Flatten the bag before recycling.", "Ensure it is clean and dry.", "Paper Recycling Bin"));
            wasteDao.insert(new WasteItem("Apple Core", "Compostable: Organic",
                    "Place in compost bin.", "No need to clean.", "Compost Bin"));
        });
    }
}
