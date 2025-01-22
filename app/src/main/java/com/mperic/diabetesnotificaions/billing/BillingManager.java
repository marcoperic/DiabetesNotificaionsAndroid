package com.mperic.diabetesnotificaions.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.mperic.diabetesnotificaions.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class BillingManager {
    private static final String TAG = "BillingManager";
    public static final String PREMIUM_PRODUCT_ID = "premium_upgrade";

    private final BillingClient billingClient;
    private final PreferenceManager preferenceManager;
    private final Context context;
    private ProductDetails premiumProductDetails;
    
    public final MutableLiveData<Boolean> isPremiumLiveData = new MutableLiveData<>();

    public BillingManager(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
        
        billingClient = BillingClient.newBuilder(context)
                .setListener(this::handlePurchase)
                .enablePendingPurchases()
                .build();
        
        connectToPlayBilling();
    }

    private void connectToPlayBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                    queryPurchases();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to reconnect
                connectToPlayBilling();
            }
        });
    }

    private void queryProductDetails() {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(List.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PREMIUM_PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()))
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (!productDetailsList.isEmpty()) {
                    premiumProductDetails = productDetailsList.get(0);
                }
            }
        });
    }

    private void queryPurchases() {
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();

        billingClient.queryPurchasesAsync(params, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : purchases) {
                    if (purchase.getProducts().contains(PREMIUM_PRODUCT_ID)) {
                        handlePurchase(billingResult, purchases);
                    }
                }
            }
        });
    }

    private void handlePurchase(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    // Grant premium entitlement
                    preferenceManager.setPremium(true);
                    isPremiumLiveData.postValue(true);

                    // Acknowledge the purchase if it hasn't been acknowledged yet
                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                                .newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult1 -> {
                            if (billingResult1.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                                Log.e(TAG, "Failed to acknowledge purchase: " + billingResult1.getDebugMessage());
                            }
                        });
                    }
                }
            }
        }
    }

    public void launchBillingFlow(Activity activity) {
        if (premiumProductDetails == null) {
            Log.e(TAG, "Product details not loaded");
            return;
        }

        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = List.of(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(premiumProductDetails)
                .build()
        );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow(activity, billingFlowParams);
    }
} 