package com.xiaoyou.adsdkIntegration.demoapp.ads.topon;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

public class TopOn_InterstitialLoader implements AdLoader {

    private final String placementId = "n683fba67f0cb4";

    private final Activity activity;
    private ATInterstitial interstitialAd;

    public TopOn_InterstitialLoader(Activity activity) {
        this.activity = activity;
        interstitialAd = new ATInterstitial(activity, placementId);
        initAd();
        interstitialAd.load(); // 自动预加载
    }

    private void initAd() {

        interstitialAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                Toast.makeText(activity, "TopOn 插页广告加载完成", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Toast.makeText(activity, "TopOn 插页广告加载失败" + adError.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
                System.out.println("TopOn 插页广告加载失败" + adError.getFullErrorInfo());
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo adInfo) {
                // ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                // 请参考 https://docs.TopOnad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info
                // 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                interstitialAd.load(); // 自动预加
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Toast.makeText(activity, "TopOn 插页广告视频加载失败" + adError.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
                Log.e("TopOn", "TopOn 插页广告视频加载失败" + adError.getFullErrorInfo());
            }
        });

        // 也可以加 setAdDownloadListener、setAdSourceStatusListener 等
    }

    @Override
    public void loadAd() {
        if (interstitialAd == null) {
            interstitialAd = new ATInterstitial(activity, placementId);
        }
        interstitialAd.load();
    }

    @Override
    public void showAd() {
        if (interstitialAd != null) {
            // ATInterstitial.entryAdScenario(placementId, scenarioId);
            if (interstitialAd.isAdReady()) {
                // interstitialAd.show(activity, scenarioId);
                interstitialAd.show(activity);
            } else {
                Toast.makeText(activity, "TopOn 插屏广告还没准备好", Toast.LENGTH_SHORT).show();
                interstitialAd = new ATInterstitial(activity, placementId);
            }
        }
    }

    public boolean isAdReady() {
        return interstitialAd != null && interstitialAd.isAdReady();
    }
}
