package com.xiaoyou.adsdkIntegration.demoapp.ads.bigo;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.RewardAdInteractionListener;
import sg.bigo.ads.api.RewardVideoAd;
import sg.bigo.ads.api.RewardVideoAdLoader;
import sg.bigo.ads.api.RewardVideoAdRequest;

public class BIGO_RewardedAdLoader implements AdLoader {

    private static final String TAG = "bigo_reward";
    private static Context context;
    private RewardVideoAd rewardVideoAd;

    private RewardVideoAdRequest rewardVideoAdAdRequest;
    private RewardVideoAdLoader rewardVideoAdLoader;

    public BIGO_RewardedAdLoader(Activity activity) {
        context = activity.getApplicationContext();
        loadSplashAd();
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, context, text);
    }

    // Start to load splash ads
    @Override
    public void loadAd() {
        loadSplashAd();
    }

    @Override
    public void showAd() {
        if (rewardVideoAd != null) {
            rewardVideoAd.show();
        } else {
            notify("BIGO 激励广告还未加载好");
        }
    }

    private void loadSplashAd() {
        rewardVideoAdAdRequest = new RewardVideoAdRequest.Builder()
                .withSlotId("10182906-10001431")
                .build();

        rewardVideoAdLoader = new RewardVideoAdLoader.Builder().
                withAdLoadListener(new AdLoadListener<RewardVideoAd>() {
                    @Override
                    public void onError(@NonNull AdError adError) {
                        // There's something wrong during ad loading
                        // jumpToMainPage();
                        BIGO_RewardedAdLoader.notify("BIGO 激励广告加载失败，错误代码：" + adError.getCode() + "\n" + " 报错信息：" + adError.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardVideoAd ad) {
                        BIGO_RewardedAdLoader.notify("BIGO 激励广告加载成功");
                        rewardVideoAd = ad;
                        onRewardVideoAdLoaded(ad);
                    }
                }).build();
        rewardVideoAdLoader.loadAd(rewardVideoAdAdRequest);
    }

    private void onRewardVideoAdLoaded(RewardVideoAd ad) {
        ad.setAdInteractionListener(new RewardAdInteractionListener() {
            // 表示广告使用过程中出现了一些异常。
            @Override
            public void onAdError(@NonNull AdError adError) {
                BIGO_RewardedAdLoader.notify("BIGO 激励广告展示失败失败，错误代码：" + adError.getCode() + "\n" + " 报错信息：" + adError.getMessage());
            }

            // 表示广告已成功展示在设备屏幕上。
            @Override
            public void onAdImpression() {
            }

            // 	表示广告被用户点击。
            @Override
            public void onAdClicked() {
                // When the user clicks on the ad.
            }

            //  表示全屏的广告页面已打开。
            @Override
            public void onAdOpened() {
            }

            //	表示全屏的广告页面已关闭。
            @Override
            public void onAdClosed() {
            }

            //	表示激励视频已播放完成，可下发奖励。
            @Override
            public void onAdRewarded() {
            }
        });
    }
}

