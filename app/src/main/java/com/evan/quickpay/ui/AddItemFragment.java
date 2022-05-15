package com.evan.quickpay.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.evan.quickpay.R;
import com.evan.quickpay.util.Constants;
import com.evan.quickpay.util.GlideEngine;
import com.google.android.material.textfield.TextInputEditText;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@AndroidEntryPoint
public class AddItemFragment extends DialogFragment {

    @BindView(R.id.iv_item_pic)
    ImageView imageView;

    @BindView(R.id.et_description)
    TextInputEditText etDescription;

    @BindView(R.id.et_item_name)
    TextInputEditText etName;

    @BindView(R.id.et_price)
    TextInputEditText etPrice;

    @BindView(R.id.tv_static_select_image)
    TextView tvSelectImage;

    private AddItemViewModel viewModel;
    private Context context;

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
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(AddItemViewModel.class);
        viewModel.getLiveData().observe(this, addItemResponse -> {
            switch (addItemResponse.getStatus()) {
                case SUCCESS_INSERT:
                    sharedViewModel.menuItemAdded(addItemResponse.getMenuItem());
                    this.dismiss();
                    break;

                case ERROR:
                    Toast.makeText(getContext(), addItemResponse.getMsg(), Toast.LENGTH_LONG).show();
                    break;
            }
        });


    }

    @OnClick(R.id.iv_item_pic)
    void onSelectPhotoClick() {
        AddItemFragmentPermissionsDispatcher.selectPhotoWithPermissionCheck(this);
    }

    @OnClick(R.id.btn_back)
    void onBackBtn() {
        this.dismiss();
    }

    @OnClick(R.id.btn_print)
    void onBtnSave() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        viewModel.onSaveItemClick(name, description, price);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddItemFragmentPermissionsDispatcher.onRequestPermissionsResult(
                this, requestCode, grantResults
        );
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void selectPhoto() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .isDisplayCamera(false)
                .forResult(Constants.REQUEST_CODE_SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_SELECT_PHOTO) {
            ArrayList<LocalMedia> list = PictureSelector.obtainSelectorList(data);
            String path = list.get(0).getRealPath();

            viewModel.setImagePath(path);
            Glide.with(AddItemFragment.this)
                    .load(path)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imageView);

            tvSelectImage.setVisibility(View.GONE);

        }
    }
}
