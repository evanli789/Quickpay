package com.evan.quickpay.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.quickpay.data.AddItemResponse;
import com.evan.quickpay.data.LocalRepository;
import com.evan.quickpay.data.MenuItem;
import com.evan.quickpay.util.AddItemStatus;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class AddItemViewModel extends ViewModel {

    private final LocalRepository localRepository;
    private final MutableLiveData<AddItemResponse> liveData = new MutableLiveData<>();
    private String imagePath;

    @Inject
    public AddItemViewModel(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public MutableLiveData<AddItemResponse> getLiveData() {
        return liveData;
    }

    public void onSaveItemClick(String name, String description, String price) {
        Timber.d(name + description + price);

        if (name.length() < 1) {
            liveData.setValue(new AddItemResponse(AddItemStatus.ERROR, "Name must not be empty"));
            return;
        }

        if (description.length() < 1) {
            liveData.setValue(new AddItemResponse(AddItemStatus.ERROR, "Description must not be empty"));
            return;
        }

        float priceValue;
        try {
            priceValue = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            Timber.e("Not a number");
            liveData.setValue(new AddItemResponse(AddItemStatus.ERROR, "Invalid price"));
            return;
        }

        MenuItem menuItem = new MenuItem(imagePath, name, description, priceValue);
        localRepository.addMenuItem(menuItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    Timber.d("New item id: " + id);
                    liveData.setValue(AddItemResponse.successAdd(
                            AddItemStatus.SUCCESS_INSERT, menuItem, "Success creating menu item"
                    ));
                }, err -> {
                    Timber.e("err fetching txs: " + err);
                    liveData.setValue(new AddItemResponse(AddItemStatus.ERROR, err.getMessage()));
                });
    }
}
