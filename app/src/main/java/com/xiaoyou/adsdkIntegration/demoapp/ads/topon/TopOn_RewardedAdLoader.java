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
    private ATRewardVideoAd mRewardVideoAd;

    public TopOn_RewardedAdLoader(Activity activity) {
        this.activity = activity;
        mRewardVideoAd = new ATRewardVideoAd(activity, placementId);
        initAd();
        mRewardVideoAd.load();
    }

    private void initAd() {

        mRewardVideoAd.setAdListener(new ATRewardVideoListener() {

            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {
                // 注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                // AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror

            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
                // ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                // 请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info

                // 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                // mRewardVideoAd.load();
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
        if (mRewardVideoAd == null) {
            mRewardVideoAd = new ATRewardVideoAd(activity, placementId);
        }
        mRewardVideoAd.load();
    }

    @Override
    public void showAd() {
        if (mRewardVideoAd != null) {
            // ATInterstitial.entryAdScenario(placementId, scenarioId);
            if (mRewardVideoAd.isAdReady()) {
                // interstitialAd.show(activity, scenarioId);
                mRewardVideoAd.show(activity);
            } else {
                Toast.makeText(activity, "TopOn 激励广告还没准备好", Toast.LENGTH_SHORT).show();
                mRewardVideoAd = new ATRewardVideoAd(activity, placementId);
            }
        }
    }

    public boolean isAdReady() {
        return mRewardVideoAd != null && mRewardVideoAd.isAdReady();
    }
}
