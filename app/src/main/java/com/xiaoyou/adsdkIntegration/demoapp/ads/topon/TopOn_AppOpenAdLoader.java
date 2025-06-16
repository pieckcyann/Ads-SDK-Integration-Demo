package com.xiaoyou.adsdkIntegration.demoapp.ads.topon;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.xiaoyou.adsdkIntegration.demoapp.MainActivity;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

public class TopOn_AppOpenAdLoader implements AdLoader {

    // private final String placementId = "n683fba67f0cb4";
    private final String placementId = "xxxxxxxxxxxxxxxxxx";
    private final Activity activity;
    private ATInterstitial appOpenAd;

    public TopOn_AppOpenAdLoader(Activity activity) {
        this.activity = activity;
        appOpenAd = new ATInterstitial(activity, placementId);
        appOpenAd.load();
        initAd();
    }

    private void initAd() {

        appOpenAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                Toast.makeText(activity, "TopOn 开屏广告加载完成", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Toast.makeText(activity, "TopOn 开屏广告加载失败" + adError.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
                System.out.println("TopOn 开屏广告加载失败" + adError.getFullErrorInfo());
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo adInfo) {
                // ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                // 请参考 https://docs.TopOnad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info
                // 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                appOpenAd.load(); // 自动预加
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo adInfo) {
                activity.startActivity(new Intent(activity, MainActivity.class));
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Toast.makeText(activity, "TopOn 开屏广告视频加载失败" + adError.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
                System.out.println("TopOn 开屏广告视频加载失败" + adError.getFullErrorInfo());
            }
        });

        // 也可以加 setAdDownloadListener、setAdSourceStatusListener 等
    }

    @Override
    public void loadAd() {
        // if (appOpenAd == null) {
        appOpenAd = new ATInterstitial(activity, placementId);
        // }
        appOpenAd.load();
    }

    @Override
    public void showAd() {
        if (appOpenAd != null) {
            // ATInterstitial.entryAdScenario(placementId, scenarioId);
            if (appOpenAd.isAdReady()) {
                // interstitialAd.show(activity, scenarioId);
                appOpenAd.show(activity);
            } else {
                Toast.makeText(activity, "TopOn 开屏广告还没准备好", Toast.LENGTH_SHORT).show();
                appOpenAd = new ATInterstitial(activity, placementId);
            }
        }
    }

    public boolean isAdReady() {
        return appOpenAd != null && appOpenAd.isAdReady();
    }
}
