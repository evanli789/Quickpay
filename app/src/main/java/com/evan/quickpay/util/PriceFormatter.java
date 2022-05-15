package com.evan.quickpay.util;

import com.evan.quickpay.data.MenuItem;

public class PriceFormatter {
    public static void formatMenuItemPrice(MenuItem menuItem){
        float price = menuItem.getPrice();
        String formattedPrice = "$".concat(String.valueOf(price));
        menuItem.setFormattedPrice(formattedPrice);
    }
}
