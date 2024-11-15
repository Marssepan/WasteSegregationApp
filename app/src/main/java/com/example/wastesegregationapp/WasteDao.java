package com.example.wastesegregationapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WasteDao {

    @Insert
    void insert(WasteItem wasteItem);

    // Query method to find a WasteItem by name
    @Query("SELECT * FROM waste_table WHERE itemName LIKE :query LIMIT 1")
    WasteItem getWasteItemByName(String query);

    // Query to get all waste items
    @Query("SELECT * FROM waste_table")
    List<WasteItem> getAllWasteItems();
}
