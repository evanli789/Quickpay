package com.evan.quickpay.data;

import com.evan.quickpay.util.AddItemStatus;

public class AddItemResponse {
    private final AddItemStatus status;
    private MenuItem menuItem;
    private final String msg;

    public AddItemResponse(AddItemStatus status, MenuItem menuItem, String msg) {
        this.status = status;
        this.menuItem = menuItem;
        this.msg = msg;
    }

    public AddItemResponse(AddItemStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static AddItemResponse successAdd(AddItemStatus status, MenuItem menuItem, String msg) {
        return new AddItemResponse(status, menuItem, msg);
    }

    public AddItemStatus getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
}
