package com.evan.quickpay.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.evan.quickpay.R;
import com.evan.quickpay.data.WalletResponse;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WalletFragment extends DialogFragment {

    @BindView(R.id.et_btc)
    TextInputEditText etBtc;

    @BindView(R.id.et_eth)
    TextInputEditText etEth;

    @BindView(R.id.et_doge)
    TextInputEditText etDoge;

    @BindView(R.id.et_usdt)
    TextInputEditText etUsdt;

    private WalletViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_QuickPay);
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.FragmentDialogAnim;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        viewModel.getLiveData().observe(this, walletResponse -> {
            etBtc.setText(walletResponse.getBtcAddress());
            etEth.setText(walletResponse.getEthAddress());
            etDoge.setText(walletResponse.getDogeAddress());
            etUsdt.setText(walletResponse.getUsdtAddress());
        });
    }

    @OnClick(R.id.btn_back)
    void onBackBtn() {
        this.dismiss();
    }

    @OnClick(R.id.btn_save)
    void onBtnSave() {
        String btcAddress = etBtc.getText().toString().trim();
        String ethAddress = etEth.getText().toString().trim();
        String dogeAddress = etDoge.getText().toString().trim();
        String usdtAddress = etUsdt.getText().toString().trim();

        viewModel.onSaveClick(btcAddress, ethAddress, dogeAddress, usdtAddress);
        this.dismiss();
    }
}
