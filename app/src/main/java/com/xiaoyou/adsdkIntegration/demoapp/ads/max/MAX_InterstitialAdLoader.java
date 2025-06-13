package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

import java.util.concurrent.TimeUnit;

public class MAX_InterstitialAdLoader implements AdLoader, MaxAdListener, MaxAdRevenueListener {

    private final Context context;
    private final MaxInterstitialAd interstitialAd;
    private int retryAttempt = 0;

    public MAX_InterstitialAdLoader(Context context) {
        this.context = context.getApplicationContext(); // 避免内存泄漏
        interstitialAd = new MaxInterstitialAd("4e3d8dc87fc3fb78", context);
        interstitialAd.setListener(this);
        interstitialAd.setRevenueListener(this);
    }

    @Override
    public void loadAd() {
        interstitialAd.loadAd();
    }

    @Override
    public void showAd() {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        } else {
            Toast.makeText(context, "插屏广告还没准备好", Toast.LENGTH_SHORT).show();
        }
    }

    public void destroy() {
        interstitialAd.setListener(null);
        interstitialAd.setRevenueListener(null);
    }

    // region MaxAdListener

    @Override
    public void onAdLoaded(@NonNull MaxAd ad) {
        Toast.makeText(context, "插屏广告加载完成", Toast.LENGTH_SHORT).show();
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError maxError) {
        Toast.makeText(context, "插屏广告加载失败：" + maxError.getMessage(), Toast.LENGTH_SHORT).show();

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
        new Handler().postDelayed(interstitialAd::loadAd, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) {
        interstitialAd.loadAd();
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdClicked(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdHidden(@NonNull MaxAd ad) {
        interstitialAd.loadAd();
    }

    // endregion

    // region Revenue

    @Override
    public void onAdRevenuePaid(MaxAd ad) {
        AdjustAdRevenue revenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        revenue.setRevenue(ad.getRevenue(), "USD");
        revenue.setAdRevenueNetwork(ad.getNetworkName());
        revenue.setAdRevenueUnit(ad.getAdUnitId());
        revenue.setAdRevenuePlacement(ad.getPlacement());

        Adjust.trackAdRevenue(revenue);
    }

    // endregion
}
