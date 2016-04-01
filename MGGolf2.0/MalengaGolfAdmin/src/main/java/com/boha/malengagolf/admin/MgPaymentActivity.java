package com.boha.malengagolf.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.android.vending.billing.IInAppBillingService;
import com.boha.malengagolf.admin.billing.IabException;
import com.boha.malengagolf.admin.billing.IabHelper;
import com.boha.malengagolf.admin.billing.IabResult;
import com.boha.malengagolf.admin.billing.Inventory;
import com.boha.malengagolf.admin.billing.Purchase;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/11.
 */
public class MgPaymentActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        ctx = getApplicationContext();
        //MpUtils.enablePaymentBroadcast(this, Manifest.permission.PAYMENT_BROADCAST_PERMISSION);
        setFields();
        startGooglePlay();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    IInAppBillingService mService;


    private void setFields() {

        btnCreditCardPayment = (Button) findViewById(R.id.PAY_btnCredit);
        btnMobilePayment = (Button) findViewById(R.id.PAY_btnMobile);
        radio100 = (RadioButton)findViewById(R.id.PAY_radio100);
        radio50 = (RadioButton)findViewById(R.id.PAY_radio50);
        radio200 = (RadioButton)findViewById(R.id.PAY_radio200);


        btnMobilePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PaymentRequest.PaymentRequestBuilder builder = new PaymentRequest.PaymentRequestBuilder();
//                builder.setService(FORTUMO_SERVICE_ID, FORTUMO_IN_APPLICATION_SECRET);
//                builder.setDisplayString(ctx.getResources().getString(R.string.subs));      // shown on user receipt
//                builder.setProductName(ctx.getResources().getString(R.string.subs));  // non-consumable purchases are restored using this value
//                builder.setConsumable(true);              // non-consumable items can be later restored
//                builder.setIcon(R.drawable.flag48);
//                PaymentRequest pr = builder.build();
//                makePayment(pr);
            }
        });
        btnCreditCardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseSubscription();
            }
        });
    }


    RadioButton radio50, radio100, radio200;
    private void purchaseSubscription() {

        if (!radio100.isChecked() && !radio50.isChecked() && !radio200.isChecked()) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_subs));
            return;
        }
        String sku = SKU_200;
        if (radio50.isChecked()) {
            sku = SKU_50;
        }
        if (radio100.isChecked()) {
            sku = SKU_100;
        }
        if (radio200.isChecked()) {
            sku = SKU_200;
        }

        Log.e(LOG, "purchaseSubscription ....");
        administrator = SharedUtil.getAdministrator(ctx);
        boolean subsSupported = mHelper.subscriptionsSupported();
        Log.e(LOG, "are subscriptionsSupported?: " + subsSupported);
        mHelper.launchSubscriptionPurchaseFlow(this, sku, REQUEST_PURCHASE, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Log.e(LOG, "onIabPurchaseFinished ....result: " + result);
                if (info != null) {
                    Log.e(LOG, "dev payload: " + info.getDeveloperPayload());
                    Log.e(LOG, "itemType: " + info.getItemType());
                    Log.e(LOG, "orderID: " + info.getOrderId());
                    Log.e(LOG, "getOriginalJson: " + info.getOriginalJson());
                    Log.e(LOG, "getPurchaseState: " + info.getPurchaseState());
                    Log.e(LOG, "getPurchaseTime: " + new Date(info.getPurchaseTime()));
                    Log.e(LOG, "sku: " + info.getSku());
                    Log.e(LOG, "token: " + info.getToken());
                }
            }
        }, "admin" + administrator.getAdministratorID());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(LOG, "onActivityResult ....resultCode: " + resultCode);
        mHelper.handleActivityResult(requestCode,resultCode, data);
    }

    private void startGooglePlay() {
        mHelper = new IabHelper(ctx, getLicenseKey());
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e(LOG, "Problem setting up In-App Billing: " + result);
                } else {
                    Log.i(LOG, "########## Looks like in-app billing helper is UP! " + result);
                    queryPurchase();
                }
            }
        });
    }

    private void queryPurchase() {
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add(SKU_50);
        skuList.add(SKU_100);
        skuList.add(SKU_200);
        try {
            Inventory inv = mHelper.queryInventory(true, skuList);
            Log.i(LOG, "########## query done in activity .....");
            if (inv != null) {
                Log.i(LOG, "inv is not null ... progress?" + inv.toString());
            }
        } catch (IabException e) {
            e.printStackTrace();
        }

    }

    private void queryInventory() {
        Log.e(LOG, "queryInventory ...");
        List<String> additionalSkuList = new ArrayList<String>();
        additionalSkuList.add(SKU_50);
        additionalSkuList.add(SKU_100);
        additionalSkuList.add(SKU_200);
        mHelper.queryInventoryAsync(true, additionalSkuList,
                new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        Log.d(LOG, "onQueryInventoryFinished, result: " + result);
                        if (result.isSuccess()) {
                            try {
                                //String price50 = inv.;
                                Log.e(LOG, "**** success! but no cigar??");
                            } catch (Exception e) {
                                Log.e(LOG, "### queryInventory failed", e);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;

    }

    AdministratorDTO administrator;
    IabHelper mHelper;

    Button btnMobilePayment, btnCreditCardPayment;
    Context ctx;
    boolean mIsPremium;
    static final int REQUEST_PURCHASE = 313;
    static final String FORTUMO_SERVICE_ID = "0bc3d36b5bf29df913eb3c6836e5000e";
    static final String FORTUMO_IN_APPLICATION_SECRET = "d8b68a20637f938ce329554c00af15c6";
    static final String FORTUMO_SECRET = "bef8ec81c92092e73e1b22ef8e36edd4";
    static final String LOG = "MgPaymentActivity", SKU_PREMIUM = "mgGolf",
            TEST_PRODUCT_PURCHASED = "android.unpack.purchased",
            TEST_PURCHASE_CANCELLED = "android.unpack.canceled", TEST_PURCHASE_REFUNDED = "android.unpack.refunded",
            TEST_PURCHASE_UNAVAILABLE = "android.unpack.refunded";
    static final String LICENSE_KEY1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr97MkWHcXt8AruRG8aCBxoJBoAmkuPkKNVzBvcd/";
    static final String LICENSE_KEY3 = "R23rP1Ccr53E8RaO2RaIX1L7zvOytEedDYMuNCd6YBlUT2C9Id4/zN4e6ZJCLZ47vcNwPogzSFsGyDN5qAZmQQvy+ctWX2Ni767b5BhxAjjs4Hp3Wt9qt2NlhMOiDf1/";
    static final String LICENSE_KEY5 = "kaN/rH0mEq8vyN9yrZfm+1J08o4VD4X5HK30z8NkCTgA+awxmPwxoBB6P6mAvGORBGUWtAVyICW8c2O4CpXVaiRPgsdJ7xf+PM0LZRbxMNmkUAlgXgoLTAFLoHs3mDFhL35";
    static final String LICENSE_KEY7 = "bUfZgarEDP6UwCEkJCgF2fTR/H474Y68eGF4m/gCMZQIDAQAB";
    private String getLicenseKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(LICENSE_KEY1.trim()).append(LICENSE_KEY3.trim()).append(LICENSE_KEY5.trim()).append(LICENSE_KEY7.trim());
        return sb.toString();
    }

    static final String SKU_50 = "mgadmin";
    static final String SKU_100 = "mgadmin100";
    static final String SKU_200 = "mgadmin303";

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.boha.malengagolf.library.R.menu.payment, menu);
        mMenu = menu;
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(com.boha.malengagolf.library.R.id.menu_help);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(com.boha.malengagolf.library.R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_help) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.under_cons));
        }

        if (item.getItemId() == R.id.menu_inventory) {
            queryInventory();
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onPause() {
        overridePendingTransition(com.boha.malengagolf.library.R.anim.slide_in_left, com.boha.malengagolf.library.R.anim.slide_out_right);
        super.onPause();
    }
    Menu mMenu;
}