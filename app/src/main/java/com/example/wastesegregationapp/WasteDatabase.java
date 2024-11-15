package com.example.wastesegregationapp;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {WasteItem.class}, version = 1)
public abstract class WasteDatabase extends RoomDatabase {
    private static volatile WasteDatabase INSTANCE;
    public abstract WasteDao wasteDao();

    public static WasteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WasteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WasteDatabase.class, "waste_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
