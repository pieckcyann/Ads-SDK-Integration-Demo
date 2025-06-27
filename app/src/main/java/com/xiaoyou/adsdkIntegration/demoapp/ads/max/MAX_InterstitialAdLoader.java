package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.LogUtil;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;
import com.xiaoyou.adsdkIntegration.demoapp.utils.analyzer.AdContentAnalyzer;

import java.util.concurrent.TimeUnit;

// 说说我的这个类的结构
public class MAX_InterstitialAdLoader implements AdLoader, MaxAdListener, MaxAdRevenueListener {
    private static final String TAG = "max inter";
    private static Context context;
    // private final String MAX_INTER_ID = "891d085f2e930102";
    private final String MAX_INTER_ID = "8c4f6bf7e93ee56a"; // com.chenfine.flowing.chagerater
    private final MaxInterstitialAd interstitialAd;
    private int retryAttempt = 0;

    public MAX_InterstitialAdLoader(Context context) {
        MAX_InterstitialAdLoader.context = context.getApplicationContext(); // 避免内存泄漏
        interstitialAd = new MaxInterstitialAd(MAX_INTER_ID);

        interstitialAd.setListener(this);
        interstitialAd.setRevenueListener(this);
        interstitialAd.loadAd();
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, context, text);
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
            MAX_InterstitialAdLoader.notify("MAX 插屏广告还没准备好");
        }
    }

    public void destroy() {
        interstitialAd.setListener(null);
        interstitialAd.setRevenueListener(null);
    }

    // region MaxAdListener

    @Override
    public void onAdLoaded(@NonNull MaxAd ad) {
        MAX_InterstitialAdLoader.notify("MAX 插屏广告加载完成");
        retryAttempt = 0;

        String networkName = ad.getNetworkName();
        LogUtil.i("Showing ad from: " + networkName);
        // 允许 Fyber 展示
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError maxError) {
        Toast.makeText(context, "MAX 插屏广告加载失败，错误码：" + maxError.getCode() + "\n" + "报错详情：" + maxError.getMessage(), Toast.LENGTH_SHORT).show();

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
        new Handler().postDelayed(interstitialAd::loadAd, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) {
        // interstitialAd.loadAd();
        Toast.makeText(context, "MAX 插屏广告曝光失败，错误码：" + error.getCode() + "\n" + "报错详情：" + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd ad) {
        AdContentAnalyzer.getAdContent(ad);
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
        // AdjustAdRevenue revenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        // revenue.setRevenue(ad.getRevenue(), "USD");
        // revenue.setAdRevenueNetwork(ad.getNetworkName());
        // revenue.setAdRevenueUnit(ad.getAdUnitId());
        // revenue.setAdRevenuePlacement(ad.getPlacement());
        //
        // Adjust.trackAdRevenue(revenue);
    }

    // endregion
}
