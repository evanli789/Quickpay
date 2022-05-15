package com.evan.quickpay.di;

import android.content.Context;

import androidx.room.Room;

import com.evan.quickpay.data.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@ApplicationContext Context context){
        return Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "quickpay-database")
                .fallbackToDestructiveMigration()
                .build();
    }
}
