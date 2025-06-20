package com.xiaoyou.adsdkIntegration.demoapp.ads.topon;

import android.app.Activity;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.AdContentAnalyzer;
import com.xiaoyou.adsdkIntegration.demoapp.utils.LogUtil;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

public class TopOn_InterstitialLoader implements AdLoader {

    private static final String TAG = "topon inter";
    private static Activity activity;
    private final String placementId = "n683fba67f0cb4";
    private ATInterstitial interstitialAd;

    public TopOn_InterstitialLoader(Activity activity) {
        this.activity = activity;
        initAd(); // 自动预加载
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, activity, text);
    }

    private void initAd() {
        interstitialAd = new ATInterstitial(activity, placementId);

        interstitialAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                TopOn_InterstitialLoader.notify("TopOn 插屏广告加载完成");
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                TopOn_InterstitialLoader.notify("TopOn 插屏广告加载失败，错误码：" + adError.getCode() + "\n" + "报错详情：" + adError.printStackTrace() + "\n");
                // TopOn_InterstitialLoader.notify("平台报错码：" + adError.getPlatformCode() + "\n" + "报错信息：" + adError.getPlatformMSG());
                // TopOn_InterstitialLoader.notify(adError.printStackTrace());
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo adInfo) {
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo adInfo) {
                // ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                // 请参考 https://docs.TopOnad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info
                // 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                interstitialAd.load(); // 自动预加载

                LogUtil.i("Showing ad from: " + adInfo.getNetworkName());
                AdContentAnalyzer.getAdContent(adInfo);
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
            }
        });

        interstitialAd.load();

        // 也可以加 setAdDownloadListener、setAdSourceStatusListener 等
    }

    @Override
    public void loadAd() {
        initAd();
    }

    @Override
    public void showAd() {
        if (interstitialAd != null) {
            // ATInterstitial.entryAdScenario(placementId, scenarioId);
            if (interstitialAd.isAdReady()) {
                // interstitialAd.show(activity, scenarioId);
                interstitialAd.show(activity);
            } else {
                TopOn_InterstitialLoader.notify("TopOn 插屏广告还没准备好");

                interstitialAd = new ATInterstitial(activity, placementId);
            }
        }
    }
}
