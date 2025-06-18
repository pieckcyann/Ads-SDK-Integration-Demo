package com.xiaoyou.adsdkIntegration.demoapp.ads.topon;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;

public class TopOn_AppOpenAdLoader implements AdLoader {

    // private final String placementId = "n683fba67f0cb4";
    private final String placementId = "xxxxxxxxxxxxxxxxxx";
    private final Activity activity;
    ATSplashExListener listener = new ATSplashExListener() {

        @Override
        public void onDeeplinkCallback(ATAdInfo atAdInfo, boolean b) {

        }

        @Override
        public void onDownloadConfirm(Context context, ATAdInfo atAdInfo, ATNetworkConfirmInfo atNetworkConfirmInfo) {

        }

        @Override
        public void onAdLoaded(boolean isTimeout) {
            // //加载未超时时
            // if(!isTimeout){
            //     //当前Activity处于前台时进行广告展示
            //     if(inForeBackground){
            //         //container大小至少占屏幕75%
            //         splashAd.show(activity, container);
            //     }else{
            //         //等待应用回到前台后再进行展示
            //         needShowSplashAd = true;
            //     }
            // }
        }

        @Override
        public void onAdLoadTimeout() {
            // 加载超时后，直接进入主界面
            // jumpToMainActivity();
        }

        @Override
        public void onNoAdError(AdError adError) {
            // 加载失败直接进入主界面
            // jumpToMainActivity();
        }

        @Override
        public void onAdShow(ATAdInfo entity) {

        }

        @Override
        public void onAdClick(ATAdInfo atAdInfo) {

        }

        @Override
        public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {
            // 开屏广告展示关闭后进入主界面
            // 注意：部分平台跳转落地页后倒计时不暂停，即使在看落地页，倒计时结束后仍然会回调onAdDismiss
            // 因此在页面跳转时需要特殊处理，详情参考下方示例代码
            // jumpToMainActivity();
        }
    };
    private ATSplashAd appOpenAd;

    public TopOn_AppOpenAdLoader(Activity activity) {
        this.activity = activity;
        initAd();
    }

    private void initAd() {
        appOpenAd = new ATSplashAd(activity, placementId, listener, 5000);
        appOpenAd.loadAd();
    }

    @Override
    public void loadAd() {
        initAd();
    }

    @Override
    public void showAd() {
        if (appOpenAd != null) {
            // ATInterstitial.entryAdScenario(placementId, scenarioId);
            if (appOpenAd.isAdReady()) {
                // interstitialAd.show(activity, scenarioId);
                appOpenAd.show(activity, activity.findViewById(android.R.id.content));
            } else {
                Toast.makeText(activity, "TopOn 开屏广告还没准备好", Toast.LENGTH_SHORT).show();
                initAd();
            }
        }
    }
}
