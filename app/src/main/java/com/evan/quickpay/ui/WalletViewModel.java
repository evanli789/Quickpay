package com.evan.quickpay.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.quickpay.data.WalletResponse;
import com.evan.quickpay.util.SharedPrefManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WalletViewModel extends ViewModel {

    private final SharedPrefManager sharedPrefManager;
    private final MutableLiveData<WalletResponse> liveData = new MutableLiveData<>();

    @Inject
    public WalletViewModel(SharedPrefManager sharedPrefManager) {
        this.sharedPrefManager = sharedPrefManager;
        liveData.setValue(new WalletResponse(
                sharedPrefManager.getBtcAddress(),
                sharedPrefManager.getEthAddress(),
                sharedPrefManager.getDogeAddress(),
                sharedPrefManager.getUsdtAddress()
        ));
    }

    public void onSaveClick(String btcAddress, String ethAddress, String dogeAddress, String usdtAddress){
        sharedPrefManager.setBtcAddress(btcAddress);
        sharedPrefManager.setEthAddress(ethAddress);
        sharedPrefManager.setDogeAddress(dogeAddress);
        sharedPrefManager.setUsdtAddress(usdtAddress);
    }

    public MutableLiveData<WalletResponse> getLiveData() {
        return liveData;
    }
}
