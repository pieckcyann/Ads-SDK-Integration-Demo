package com.xiaoyou.adsdkIntegration.demoapp.ads.topon;

import android.app.Activity;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

public class TopOn_RewardedAdLoader implements AdLoader {

    private final String placementId = "n683fba683e000";
    private final Activity activity;
    private ATRewardVideoAd rewardedAd;

    public TopOn_RewardedAdLoader(Activity activity) {
        this.activity = activity;
        rewardedAd = new ATRewardVideoAd(activity, placementId);
        initAd();
        rewardedAd.load();
    }

    private void initAd() {

        rewardedAd.setAdListener(new ATRewardVideoListener() {

            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {

            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {

            }

            @Override
            public void onReward(ATAdInfo atAdInfo) {

            }
        });

        // 也可以加 setAdDownloadListener、setAdSourceStatusListener 等
    }

    @Override
    public void loadAd() {
        if (rewardedAd == null) {
            rewardedAd = new ATRewardVideoAd(activity, placementId);
        }
        rewardedAd.load();
    }

    @Override
    public void showAd() {
        if (rewardedAd != null) {
            // ATInterstitial.entryAdScenario(placementId, scenarioId);
            if (rewardedAd.isAdReady()) {
                // interstitialAd.show(activity, scenarioId);
                rewardedAd.show(activity);
            } else {
                Toast.makeText(activity, "TopOn 激励广告还没准备好", Toast.LENGTH_SHORT).show();
                rewardedAd = new ATRewardVideoAd(activity, placementId);
            }
        }
    }

    public boolean isAdReady() {
        return rewardedAd != null && rewardedAd.isAdReady();
    }
}
