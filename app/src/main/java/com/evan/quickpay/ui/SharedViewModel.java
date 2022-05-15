package com.evan.quickpay.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.quickpay.data.MenuItem;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<MenuItem> menuAddLiveData = new MutableLiveData<>();

    public MutableLiveData<MenuItem> getMenuAddLiveData() {
        return menuAddLiveData;
    }

    public void menuItemAdded(MenuItem menuItem) {
        menuAddLiveData.setValue(menuItem);
    }
}
