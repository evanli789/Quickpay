package com.evan.quickpay.data;

public class WalletResponse {
    private String btcAddress;
    private String ethAddress;
    private String dogeAddress;
    private String usdtAddress;

    public WalletResponse(String btcAddress, String ethAddress, String dogeAddress, String usdtAddress) {
        this.btcAddress = btcAddress;
        this.ethAddress = ethAddress;
        this.dogeAddress = dogeAddress;
        this.usdtAddress = usdtAddress;
    }

    public String getBtcAddress() {
        return btcAddress;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public String getDogeAddress() {
        return dogeAddress;
    }

    public String getUsdtAddress() {
        return usdtAddress;
    }
}
