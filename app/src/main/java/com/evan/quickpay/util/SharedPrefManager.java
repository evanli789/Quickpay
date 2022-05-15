package com.evan.quickpay.util;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPrefManager {

    private final SharedPreferences sharedPreferences;

    private static final String KEY_BTC_ADDRESS = "KEY_BTC_ADDRESS";
    private static final String KEY_ETH_ADDRESS = "KEY_ETH_ADDRESS";
    private static final String KEY_DOGE_ADDRESS = "KEY_DOGE_ADDRESS";
    private static final String KEY_USDT_ADDRESS = "KEY_USDT_ADDRESS";

    @Inject
    public SharedPrefManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getBtcAddress() {
        return sharedPreferences.getString(KEY_BTC_ADDRESS, null);
    }

    public String getEthAddress() {
        return sharedPreferences.getString(KEY_ETH_ADDRESS, null);
    }

    public String getDogeAddress() {
        return sharedPreferences.getString(KEY_DOGE_ADDRESS, null);
    }

    public String getUsdtAddress() {
        return sharedPreferences.getString(KEY_USDT_ADDRESS, null);
    }

    public void setBtcAddress(String btcAddress){
        sharedPreferences.edit().putString(KEY_BTC_ADDRESS, btcAddress).apply();
    }

    public void setEthAddress(String ethAddress){
        sharedPreferences.edit().putString(KEY_ETH_ADDRESS, ethAddress).apply();
    }

    public void setDogeAddress(String dogeAddress){
        sharedPreferences.edit().putString(KEY_DOGE_ADDRESS, dogeAddress).apply();
    }

    public void setUsdtAddress(String usdtAddress){
        sharedPreferences.edit().putString(KEY_USDT_ADDRESS, usdtAddress).apply();
    }
}
