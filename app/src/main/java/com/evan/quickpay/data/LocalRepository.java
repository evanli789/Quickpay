package com.evan.quickpay.data;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class LocalRepository {

    private final AppDatabase appDatabase;

    @Inject
    public LocalRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Single<List<MenuItem>> getMenu(){
        return appDatabase.menuDao().getMenu();
    }

    public Single<MenuItem> getMenuItem(int itemId){
        return appDatabase.menuDao().getMenuItem(itemId);
    }

    public Single<Integer> updateMenuItem(MenuItem menuItem){
        return appDatabase.menuDao().addMenuItem(menuItem);
    }

    public Single<Integer> deleteMenuItem(int id){
        return appDatabase.menuDao().deleteMenuItem(id);
    }

    public Single<Long> addMenuItem(MenuItem menuItem){
        return appDatabase.menuDao().insertMenuItem(menuItem);
    }
}
