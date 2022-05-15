package com.evan.quickpay.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MenuItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MenuDao menuDao();
}
