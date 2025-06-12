package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.ui.BaseAdActivity;

import java.util.concurrent.TimeUnit;

/**
 * An {@link android.app.Activity} used to show AppLovin MAX interstitial ads.
 */
public class InterstitialAdActivity
        extends BaseAdActivity
        implements MaxAdListener, MaxAdRevenueListener {
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);
        setTitle(R.string.activity_interstitial);

        setupCallbacksRecyclerView();

        interstitialAd = new MaxInterstitialAd("4e3d8dc87fc3fb78", this);

        interstitialAd.setListener(this);
        interstitialAd.setRevenueListener(this);

        // Load the first ad.
        interstitialAd.loadAd();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        interstitialAd.setListener(null);
        interstitialAd.setRevenueListener(null);
    }

    public void onShowAdClicked(View view) {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        } else {
            System.out.println("还没准备好。");
        }
    }

    // region MAX Ad Listener

    @Override
    public void onAdLoaded(@NonNull final MaxAd ad) {
        Toast.makeText(this, "广告加载完成！", Toast.LENGTH_SHORT).show();
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'.
        logCallback();

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError maxError) {
        String errorMsg = "广告加载失败，错误码：" + maxError.getCode() + "，原因：" + maxError.getMessage();
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        System.out.println(errorMsg);

        logCallback();

        // Interstitial ad failed to load. We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds).
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                interstitialAd.loadAd();
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(@NonNull final MaxAd ad, @NonNull final MaxError maxError) {
        logCallback();

        // Interstitial ad failed to display. We recommend loading the next ad.
        interstitialAd.loadAd();
    }

    @Override
    public void onAdDisplayed(@NonNull final MaxAd ad) {
        logCallback();
    }

    @Override
    public void onAdClicked(@NonNull final MaxAd ad) {
        logCallback();
    }

    @Override
    public void onAdHidden(@NonNull final MaxAd ad) {
        logCallback();

        // Interstitial Ad is hidden. Pre-load the next ad
        interstitialAd.loadAd();
    }

    // endregion

    // region MAX Ad Revenue Listener

    @Override
    public void onAdRevenuePaid(final MaxAd maxAd) {
        logCallback();

        AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        adjustAdRevenue.setRevenue(maxAd.getRevenue(), "USD");
        adjustAdRevenue.setAdRevenueNetwork(maxAd.getNetworkName());
        adjustAdRevenue.setAdRevenueUnit(maxAd.getAdUnitId());
        adjustAdRevenue.setAdRevenuePlacement(maxAd.getPlacement());

        Adjust.trackAdRevenue(adjustAdRevenue);
    }

    // endregion
}
