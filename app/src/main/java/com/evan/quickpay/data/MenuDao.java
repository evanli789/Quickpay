package com.evan.quickpay.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface MenuDao {
    //Room inserts can only return Long
    @Insert
    Single<Long> insertMenuItem(MenuItem menuItem);

    @Query("SELECT * FROM menu ORDER BY id ASC")
    Single<List<MenuItem>> getMenu();

    @Query("DELETE FROM menu WHERE id = :id")
    Single<Integer> deleteMenuItem(int id);

    @Query("SELECT * FROM menu WHERE id = :id")
    Single<MenuItem> getMenuItem(int id);

    @Update
    Single<Integer> addMenuItem(MenuItem menuItem);
}
