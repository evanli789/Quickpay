package com.evan.quickpay.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.evan.quickpay.R;
import com.evan.quickpay.adapters.MenuAdapter;
import com.evan.quickpay.util.CollectionItemDecoration;
import com.evan.quickpay.util.Constants;
import com.evan.quickpay.util.SpacingUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements MenuAdapter.MenuAdapterCallback {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.tv_static_no_menu)
    TextView tvEmptyMenu;

    @BindView(R.id.et_order)
    EditText etOrder;

    @BindView(R.id.btn_add)
    FloatingActionButton btnAdd;

    @BindView(R.id.layout_order)
    ViewGroup layoutUserOrders;

    @BindView(R.id.spinner_currency)
    Spinner spinnerCurrency;

    @BindView(R.id.parentLayout)
    ViewGroup parentLayout;

    private MenuAdapter menuAdapter;
    private MainViewModel viewModel;

    private static final int GRID_VIEW_SPACING = 8;
    private static final int GRID_VIEW_COLUMN_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getLiveData().observe(this, mainResponse -> {
            switch (mainResponse.getStatus()) {
                case SUCCESS_GET_MENU:
                    tvEmptyMenu.setVisibility(View.GONE);
                    menuAdapter.notifyDataSetChanged();
                    break;

                case NO_MENU_ITEMS:
                    tvEmptyMenu.setVisibility(View.VISIBLE);
                    break;

                case MENU_ITEM_ADDED:
                    menuAdapter.notifyItemInserted(viewModel.getListItems().size() - 1);
                    tvEmptyMenu.setVisibility(View.GONE);
                    break;

                case ON_ITEM_ADDED_TO_ORDER:
                    etOrder.setText(mainResponse.getMsg());
                    break;

                case ERROR:
                    Toast.makeText(this, mainResponse.getMsg(), Toast.LENGTH_LONG).show();
                    break;
            }
        });

        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getMenuAddLiveData().observe(this, menuItem -> {
            viewModel.onMenuItemAdded(menuItem);
        });

        int spacing = SpacingUtils.convertIntToDP(this, GRID_VIEW_SPACING);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this, GRID_VIEW_COLUMN_COUNT
        );

        recyclerView.addItemDecoration(
                new CollectionItemDecoration(GRID_VIEW_COLUMN_COUNT, spacing, true)
        );
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(null);
        menuAdapter = new MenuAdapter(viewModel.getListItems(), this);
        recyclerView.setAdapter(menuAdapter);

        initCurrencySpinner();
    }

    @OnClick(R.id.btn_add)
    void btnAdd() {
        DialogFragment addItemFragment = new AddItemFragment();
        addItemFragment.show(getSupportFragmentManager(), Constants.FRAGMENT_ADD_ITEM);
    }



    @OnClick(R.id.btn_cancel)
    void btnCancel() {
        etOrder.getText().clear();
        layoutUserOrders.setVisibility(View.GONE);
        btnAdd.setVisibility(View.VISIBLE);
        viewModel.clearOrder();
    }

    @Override
    public void onMenuItemClick(int position) {
        btnAdd.setVisibility(View.GONE);
        layoutUserOrders.setVisibility(View.VISIBLE);
        viewModel.onMenuItemClick(position);
    }

    @Override
    public void onMenuItemLongClick(int position) {

    }

    @OnClick(R.id.btn_wallet)
    void btnWallet(){
        DialogFragment walletFragment = new WalletFragment();
        walletFragment.show(getSupportFragmentManager(), Constants.FRAGMENT_WALLET);
    }

    private void initCurrencySpinner() {
        List<String> spinnerItems = new ArrayList<>(Arrays.asList(viewModel.getCryptoTypes()));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_network_text_layout, spinnerItems
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_network_drop_down);
        spinnerCurrency.setAdapter(spinnerAdapter);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedType(position);
                spinnerCurrency.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.btn_print)
    void btnPrint() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.layout_print, parentLayout, false);
        TextView tvOrder = view.findViewById(R.id.tv_order);
        TextView tvSendCurrencyType = view.findViewById(R.id.tv_send_to);
        ImageView ivQrCode = view.findViewById(R.id.iv_qr_code);
        tvOrder.setText(viewModel.getFormattedOrder());
        view.setVisibility(View.INVISIBLE);
        tvSendCurrencyType.setText(viewModel.getFormattedSendTo());

        parentLayout.addView(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    ivQrCode.setImageBitmap(createQrCode(viewModel.getWalletAddress(), view.getHeight()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                btnAdd.setVisibility(View.VISIBLE);
                layoutUserOrders.setVisibility(View.GONE);
                viewModel.clearOrder();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        printMessage(loadBitmapFromView(view));
                    }
                }).start();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });



    }

    private Bitmap createQrCode(String walletPublicKey, int qrSize) throws Exception {
        int QR_WIDTH_HEIGHT = SpacingUtils.convertIntToDP(this, qrSize);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.encodeBitmap(
                walletPublicKey, BarcodeFormat.QR_CODE, QR_WIDTH_HEIGHT, QR_WIDTH_HEIGHT
        );
    }

    public static Bitmap loadBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        view.draw(canvas);
        return returnedBitmap;
    }

    private void printMessage(Bitmap bitmap) {
        try {
            Printer myPrinter = new Printer();
            PrinterInfo myPrinterInfo = myPrinter.getPrinterInfo();
            myPrinterInfo.printerModel = PrinterInfo.Model.QL_820NWB;


            myPrinterInfo.port = PrinterInfo.Port.NET;
            myPrinterInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;
            myPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
            myPrinterInfo.numberOfCopies = 1;
            myPrinterInfo.ipAddress = "192.168.118.1";

            myPrinterInfo.workPath = this.getFilesDir().getAbsolutePath() + "/";

            myPrinterInfo.labelNameIndex = LabelInfo.QL700.W54.ordinal();
            myPrinterInfo.isAutoCut = true;
            myPrinterInfo.isCutAtEnd = false;
            myPrinterInfo.isHalfCut = false;
            myPrinterInfo.isSpecialTape = false;

            myPrinter.setPrinterInfo(myPrinterInfo);
            myPrinter.startCommunication();
            PrinterStatus status = myPrinter.printImage(bitmap);
            Timber.d("Status: " + status);
            Timber.d("Error code: " + status.errorCode);
            myPrinter.endCommunication();
        } catch (Exception e) {
            //Hide progressbar
            Timber.e("Error: " + e);
        }

    }





}