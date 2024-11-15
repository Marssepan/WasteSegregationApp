package com.example.wastesegregationapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "waste_table")
public class WasteItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String itemName;
    private String wasteCategory;
    private String disposalInstructions;
    private String cleanupInstructions;
    private String disposalLocation;

    // Constructor
    public WasteItem(String itemName, String wasteCategory, String disposalInstructions,
                     String cleanupInstructions, String disposalLocation) {
        this.itemName = itemName;
        this.wasteCategory = wasteCategory;
        this.disposalInstructions = disposalInstructions;
        this.cleanupInstructions = cleanupInstructions;
        this.disposalLocation = disposalLocation;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getItemName() { return itemName; }
    public String getWasteCategory() { return wasteCategory; }
    public String getDisposalInstructions() { return disposalInstructions; }
    public String getCleanupInstructions() { return cleanupInstructions; }
    public String getDisposalLocation() { return disposalLocation; }
}
