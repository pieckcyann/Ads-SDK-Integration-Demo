package com.xiaoyou.adsdkIntegration.demoapp.ads.max;

import android.content.Context;
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
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.AdContentAnalysis;

import java.util.concurrent.TimeUnit;

/**
 * An {@link android.app.Activity} used to show AppLovin MAX rewarded ads.
 */
public class MAX_RewardedAdLoader implements AdLoader, MaxRewardedAdListener, MaxAdRevenueListener {

    private static Context context;
    private final String MAX_REWARD_ID = "ab9912ba38d64230";
    private final MaxRewardedAd rewardedAd;
    private int retryAttempt = 0;

    public MAX_RewardedAdLoader(Context context) {
        this.context = context.getApplicationContext(); // 避免内存泄露
        // rewardedAd = MaxRewardedAd.getInstance("29e66ea95642a2e1", this.context);
        rewardedAd = MaxRewardedAd.getInstance(MAX_REWARD_ID);
        rewardedAd.setListener(this);
        rewardedAd.setRevenueListener(this);
        rewardedAd.loadAd();
    }

    @Override
    public void loadAd() {
        rewardedAd.loadAd();
    }

    @Override
    public void showAd() {
        if (rewardedAd.isReady()) {
            rewardedAd.showAd();
        } else {
            Toast.makeText(context, "MAX 激励广告还没准备好", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy() {
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
        Toast.makeText(context, "MAX 激励广告加载完成", Toast.LENGTH_SHORT).show();

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError maxError) {
        // Rewarded ad failed to load. We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds).
        Toast.makeText(context, "MAX 激励广告加载失败：" + maxError.getMessage(), Toast.LENGTH_SHORT).show();

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
        // Rewarded ad failed to display. We recommend loading the next ad.
        rewardedAd.loadAd();
    }

    @Override
    public void onAdDisplayed(@NonNull final MaxAd ad) {
        AdContentAnalysis.getAdContent(ad);
    }

    @Override
    public void onAdClicked(@NonNull final MaxAd ad) {

    }

    @Override
    public void onAdHidden(@NonNull final MaxAd ad) {
        // Rewarded ad is hidden. Pre-load the the next ad
        // rewardedAd.loadAd();
    }

    @Override
    public void onUserRewarded(@NonNull final MaxAd ad, @NonNull final MaxReward reward) {
        // Rewarded ad was displayed and user should receive the reward.
        Toast.makeText(context, "用户已获得奖励！", Toast.LENGTH_SHORT).show();

    }

    // endregion

    // region MAX Ad Revenue Listener

    @Override
    public void onAdRevenuePaid(@NonNull final MaxAd maxAd) {

        AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX);
        adjustAdRevenue.setRevenue(maxAd.getRevenue(), "USD");
        adjustAdRevenue.setAdRevenueNetwork(maxAd.getNetworkName());
        adjustAdRevenue.setAdRevenueUnit(maxAd.getAdUnitId());
        adjustAdRevenue.setAdRevenuePlacement(maxAd.getPlacement());

        Adjust.trackAdRevenue(adjustAdRevenue);
    }

    // endregion
}
