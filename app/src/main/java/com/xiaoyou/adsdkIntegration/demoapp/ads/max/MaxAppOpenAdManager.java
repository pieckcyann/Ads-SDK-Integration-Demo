package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;

/**
 * 管理 App Open 广告的工具类，可复用于 SplashActivity 等页面。
 */
public class MaxAppOpenAdManager implements MaxAdListener, MaxAdRevenueListener {
    private final Context context;
    private final MaxAppOpenAd appOpenAd;
    private final Runnable onAdFinishedCallback;

    public MaxAppOpenAdManager(Context context, String adUnitId, Runnable onAdFinishedCallback) {
        this.context = context;
        this.onAdFinishedCallback = onAdFinishedCallback;

        appOpenAd = new MaxAppOpenAd(adUnitId, context);
        appOpenAd.setListener(this);
        appOpenAd.setRevenueListener(this);
        appOpenAd.loadAd();
    }

    public void showIfReady() {
        if (appOpenAd.isReady()) {
            appOpenAd.showAd();
        } else {
            onAdFinishedCallback.run();
        }
    }

    // MaxAdListener 回调
    @Override
    public void onAdLoaded(@NonNull MaxAd ad) {
        showIfReady();
    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) {
        onAdFinishedCallback.run();
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
        onAdFinishedCallback.run();
    }

    @Override
    public void onAdHidden(@NonNull MaxAd ad) {
        onAdFinishedCallback.run();
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdClicked(@NonNull MaxAd ad) {
    }

    // MaxAdRevenueListener 回调
    @Override
    public void onAdRevenuePaid(@NonNull MaxAd ad) {
        AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        adjustAdRevenue.setRevenue(ad.getRevenue(), "USD");
        adjustAdRevenue.setAdRevenueNetwork(ad.getNetworkName());
        adjustAdRevenue.setAdRevenueUnit(ad.getAdUnitId());
        adjustAdRevenue.setAdRevenuePlacement(ad.getPlacement());

        Adjust.trackAdRevenue(adjustAdRevenue);
    }
}
