package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

/**
 * An {@link android.app.Activity} used to show AppLovin MAX App Open ads.
 */
public class MAX_AppOpenAdLoader
        implements AdLoader, MaxAdListener, MaxAdRevenueListener {

    private final Context context;
    private MaxAppOpenAd appOpenAd;

    public MAX_AppOpenAdLoader(Context context) {
        this.context = context.getApplicationContext(); // 避免内存泄露
        appOpenAd = new MaxAppOpenAd("YOUR_AD_UNIT_ID", this.context);
        appOpenAd.setListener(this);
        appOpenAd.setRevenueListener(this);
        appOpenAd.loadAd();

    }

    @Override
    public void loadAd() {
        appOpenAd.loadAd();
    }

    @Override
    public void showAd() {
        if (appOpenAd.isReady()) {
            appOpenAd.showAd();
        } else {
            Toast.makeText(context, "MAX 开屏广告还没准备好", Toast.LENGTH_SHORT).show();
        }
    }

    // region MAX Ad Listener

    @Override
    public void onAdLoaded(@NonNull final MaxAd ad) {
        // App Open ad is ready to be shown. appOpenAd.isReady() will now return 'true'.
        Toast.makeText(context, "MAX 开屏广告加载完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError error) {
    }

    @Override
    public void onAdDisplayFailed(@NonNull final MaxAd ad, @NonNull final MaxError error) {
        // App Open ad failed to display. We recommend loading the next ad.
        // appOpenAd.loadAd();
    }

    @Override
    public void onAdDisplayed(@NonNull final MaxAd ad) {

    }

    @Override
    public void onAdClicked(@NonNull final MaxAd ad) {

    }

    @Override
    public void onAdHidden(@NonNull final MaxAd ad) {

        // App Open ad is hidden. Pre-load the next ad
        appOpenAd.loadAd();

        // // 2 秒后跳转回主页
        // new android.os.Handler().postDelayed(() -> {
        //     this.context.startActivity(new Intent(this.context, MainActivity.class));
        // }, 2000);
    }

    // endregion

    // region MAX Ad Revenue Listener

    @Override
    public void onAdRevenuePaid(@NonNull final MaxAd ad) {


        // AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        // adjustAdRevenue.setRevenue(ad.getRevenue(), "USD");
        // adjustAdRevenue.setAdRevenueNetwork(ad.getNetworkName());
        // adjustAdRevenue.setAdRevenueUnit(ad.getAdUnitId());
        // adjustAdRevenue.setAdRevenuePlacement(ad.getPlacement());
        //
        // Adjust.trackAdRevenue(adjustAdRevenue);
    }

    // endregion
}
