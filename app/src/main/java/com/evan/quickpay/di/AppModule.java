package com.evan.quickpay.di;

import android.app.Application;

import com.evan.quickpay.BaseApplication;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    BaseApplication provideBaseApplication(Application application){
        return (BaseApplication) application;
    }
}
