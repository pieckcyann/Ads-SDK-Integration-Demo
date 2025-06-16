package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

/**
 * An {@link android.app.Activity} used to show AppLovin MAX banner ads.
 * <p>
 * Created by santoshbagadi on 2019-09-10.
 */
public class MAX_BannerAdLoader implements AdLoader, MaxAdViewAdListener, MaxAdRevenueListener {
    private final Context context;
    private MaxAdView adView;

    public MAX_BannerAdLoader(Context context) {
        this.context = context;
        loadBannerAd();
    }

    @Override
    public void loadAd() {
        loadBannerAd();
    }

    private void loadBannerAd() {
        adView = new MaxAdView("YOUR_AD_UNIT_ID", context);

        adView.setListener(this);
        adView.setRevenueListener(this);

        // Set the height of the banner ad based on the device type.
        final boolean isTablet = AppLovinSdkUtils.isTablet(context);
        final int heightPx = AppLovinSdkUtils.dpToPx(context, isTablet ? 90 : 50);
        // Banner width must match the screen to be fully functional.
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));

        // Need to set the background or background color for banners to be fully functional.
        adView.setBackgroundColor(Color.BLACK);

        ViewGroup rootView = ((Activity) context).findViewById(android.R.id.content); // 视图根元素
        rootView.addView(adView);

        adView.loadAd();
    }

    @Override
    public void showAd() {
        loadBannerAd();
    }

    public void destroy() {
    }

    // 下面可选：监听回调
    @Override
    public void onAdLoaded(@NonNull MaxAd ad) {
        Toast.makeText(context, "已加载", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
        Toast.makeText(context, "onAdLoadFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
        Toast.makeText(context, "onAdDisplayFailed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdHidden(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdClicked(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdExpanded(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdCollapsed(@NonNull MaxAd ad) {
    }

    @Override
    public void onAdRevenuePaid(@NonNull MaxAd ad) {
        AdjustAdRevenue revenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        revenue.setRevenue(ad.getRevenue(), "USD");
        revenue.setAdRevenueNetwork(ad.getNetworkName());
        revenue.setAdRevenueUnit(ad.getAdUnitId());
        revenue.setAdRevenuePlacement(ad.getPlacement());

        Adjust.trackAdRevenue(revenue);
    }
}
