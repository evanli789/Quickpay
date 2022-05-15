package com.evan.quickpay.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.quickpay.data.LocalRepository;
import com.evan.quickpay.data.MainResponse;
import com.evan.quickpay.data.MenuItem;
import com.evan.quickpay.util.CryptoType;
import com.evan.quickpay.util.MainActivityStatus;
import com.evan.quickpay.util.PriceFormatter;
import com.evan.quickpay.util.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private static final String[] cryptoTypes = {"Bitcoin", "Ethereum", "Dogecoin", "USDT"};
    private final List<MenuItem> list = new ArrayList<>();
    private final MutableLiveData<MainResponse> liveData = new MutableLiveData<>();
    private final LocalRepository localRepository;
    private final Map<MenuItem, AtomicInteger> mapOrders = new LinkedHashMap<>();
    private final SharedPrefManager sharedPrefManager;
    private CryptoType selectedType = CryptoType.BTC;

    @Inject
    public MainViewModel(LocalRepository localRepository, SharedPrefManager sharedPrefManager) {
        this.localRepository = localRepository;
        this.sharedPrefManager = sharedPrefManager;

        localRepository.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(menuItems -> {
                    if (menuItems.size() == 0) {
                        liveData.setValue(new MainResponse(
                                MainActivityStatus.NO_MENU_ITEMS, "No items"
                        ));
                    } else {
                        for (MenuItem menuItem : menuItems) {
                            PriceFormatter.formatMenuItemPrice(menuItem);
                        }
                        list.addAll(menuItems);
                        liveData.setValue(new MainResponse(
                                MainActivityStatus.SUCCESS_GET_MENU, "Success get menu"
                        ));
                    }

                }, err -> {
                    Timber.e("err fetching menu: " + err);
                    liveData.setValue(new MainResponse(MainActivityStatus.ERROR, err.getMessage()));
                });
    }

    public MutableLiveData<MainResponse> getLiveData() {
        return liveData;
    }

    public List<MenuItem> getListItems() {
        return list;
    }

    public void onMenuItemAdded(MenuItem menuItem) {
        PriceFormatter.formatMenuItemPrice(menuItem);

        list.add(menuItem);
        liveData.setValue(new MainResponse(MainActivityStatus.MENU_ITEM_ADDED, "item added"));
    }

    public void onMenuItemClick(int position) {
        MenuItem clickedItem = list.get(position);
        if (mapOrders.containsKey(clickedItem)) {
            mapOrders.get(clickedItem).getAndIncrement();
        } else {
            mapOrders.put(clickedItem, new AtomicInteger(1));
        }

        liveData.setValue(new MainResponse(
                MainActivityStatus.ON_ITEM_ADDED_TO_ORDER, formatOrder(mapOrders)
        ));
    }

    private static String formatOrder(Map<MenuItem, AtomicInteger> mapOrders) {
        StringBuilder order = new StringBuilder();
        float totalPrice = 0;
        int i = 0;
        for (Map.Entry<MenuItem, AtomicInteger> entry : mapOrders.entrySet()) {
            MenuItem menuItem = entry.getKey();
            AtomicInteger amountOfItem = mapOrders.get(menuItem);
            order.append("( ")
                    .append(amountOfItem)
                    .append(" ) ")
                    .append(menuItem.getName());
            //Add comma only if not last menu item
            if (i++ != mapOrders.size() - 1) {
                //order.append(",");
                order.append("\n");
            }

            totalPrice += (menuItem.getPrice() * amountOfItem.floatValue());
        }
        DecimalFormat df = new DecimalFormat("#.##");
        order.append("\n\n").append("Total: ").append("$").append(df.format(totalPrice));
        return order.toString();
    }

    public String getFormattedOrder() {
        return formatOrder(mapOrders);
    }

    public void clearOrder() {
        mapOrders.clear();
    }

    public void setSelectedType(int position) {
        switch (position) {
            case 0:
                selectedType = CryptoType.BTC;
                break;

            case 1:
                selectedType = CryptoType.ETH;
                break;

            case 2:
                selectedType = CryptoType.DOGE;
                break;

            case 3:
                selectedType = CryptoType.USDT;
                break;
        }
    }

    public String[] getCryptoTypes() {
        return cryptoTypes;
    }

    public String getFormattedSendTo() {
        switch (selectedType){
            case BTC:
                return "Send BTC to:";
            case ETH:
                return "Send ETH to:";
            case DOGE:
                return "Send Doge to:";
            case USDT:
                return "Send USDT to:";
        }
        throw new IllegalStateException();
    }

    public String getWalletAddress() {
        switch (selectedType){
            case BTC:
                return sharedPrefManager.getBtcAddress();
            case ETH:
                return sharedPrefManager.getEthAddress();
            case DOGE:
                return sharedPrefManager.getDogeAddress();
            case USDT:
                return sharedPrefManager.getUsdtAddress();
        }
        throw new IllegalStateException();
    }
}
