package com.evan.quickpay.data;

import com.evan.quickpay.util.MainActivityStatus;

public class MainResponse {
    private MainActivityStatus status;
    private String msg;

    public MainResponse(MainActivityStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public MainActivityStatus getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
