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
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.ui.BaseAdActivity;

import java.util.concurrent.TimeUnit;

/**
 * An {@link android.app.Activity} used to show AppLovin MAX rewarded ads.
 */
public class RewardedAdActivity
        extends BaseAdActivity
        implements MaxRewardedAdListener, MaxAdRevenueListener {
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_ad);
        setTitle(R.string.activity_rewarded);

        setupCallbacksRecyclerView();

        rewardedAd = MaxRewardedAd.getInstance("29e66ea95642a2e1", this);

        rewardedAd.setListener(this);
        rewardedAd.setRevenueListener(this);

        // Load the first ad.
        rewardedAd.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        rewardedAd.setListener(null);
        rewardedAd.setRevenueListener(null);
    }

    public void onShowAdClicked(View view) {
        if (rewardedAd.isReady()) {
            rewardedAd.showAd();
        }
    }

    // region MAX Ad Listener

    @Override
    public void onAdLoaded(@NonNull final MaxAd ad) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
        logCallback();

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError maxError) {
        logCallback();

        // Rewarded ad failed to load. We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds).

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rewardedAd.loadAd();
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(@NonNull final MaxAd ad, @NonNull final MaxError maxError) {
        logCallback();

        // Rewarded ad failed to display. We recommend loading the next ad.
        rewardedAd.loadAd();
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

        // Rewarded ad is hidden. Pre-load the the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onUserRewarded(@NonNull final MaxAd ad, @NonNull final MaxReward reward) {
        Toast.makeText(this, "用户已获得奖励！", Toast.LENGTH_SHORT).show();
        // Rewarded ad was displayed and user should receive the reward.
        logCallback();
    }

    // endregion

    // region MAX Ad Revenue Listener

    @Override
    public void onAdRevenuePaid(@NonNull final MaxAd maxAd) {
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
