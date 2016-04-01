package com.boha.malengagolf.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by aubreyM on 2014/05/11.
 */
public class PaymentStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Set<String> set = extras.keySet();
        Iterator<String> yter = set.iterator();
        while (yter.hasNext()) {
            String s = yter.next();
            Log.e(TAG, "Parameter name: " + s);
        }
        Log.d(TAG, "- billing_status:  " + extras.getInt("billing_status"));
        Log.d(TAG, "- credit_amount:   " + extras.getString("credit_amount"));
        Log.d(TAG, "- credit_name:     " + extras.getString("credit_name"));
        Log.d(TAG, "- message_id:      " + extras.getString("message_id") );
        Log.d(TAG, "- payment_code:    " + extras.getString("payment_code"));
        Log.d(TAG, "- price_amount:    " + extras.getString("price_amount"));
        Log.d(TAG, "- price_currency:  " + extras.getString("price_currency"));
        Log.d(TAG, "- product_name:    " + extras.getString("product_name"));
        Log.d(TAG, "- service_id:      " + extras.getString("service_id"));
        Log.d(TAG, "- user_id:         " + extras.getString("user_id"));
        Log.d(TAG, "- country:         " + extras.getString("country"));
        Log.d(TAG, "- operator:         " + extras.getString("operator"));
        Log.d(TAG, "- billing_type:     " + extras.getString("billing_type"));
        Log.d(TAG, "- sender:         " + extras.getString("sender"));
        Log.d(TAG, "- unpack:         " + extras.getString("unpack"));
        Log.d(TAG, "- user_share:         " + extras.getString("user_share"));
        Log.d(TAG, "- status:         " + extras.getString("status"));
        Log.d(TAG, "- currency:         " + extras.getString("currency"));
        Log.d(TAG, "- price:         " + extras.getString("price"));

//        int billingStatus = extras.getInt("billing_status");
//        if(billingStatus == MpUtils.MESSAGE_STATUS_BILLED) {
//            int coins = Integer.parseInt(intent.getStringExtra("credit_amount"));
//            //Wallet.addCoins(context, coins);
//            Log.e(TAG, "This transaction has been billed OK");
//        } else {
//            Log.e(TAG, "This transaction has NOT billed, tell user...");
//        }
    }

    static final String TAG = "PaymentStatusReceiver";
}
