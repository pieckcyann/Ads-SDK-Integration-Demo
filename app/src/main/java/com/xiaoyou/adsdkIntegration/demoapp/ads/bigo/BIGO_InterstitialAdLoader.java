package com.xiaoyou.adsdkIntegration.demoapp.ads.bigo;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.InterstitialAd;
import sg.bigo.ads.api.InterstitialAdLoader;
import sg.bigo.ads.api.InterstitialAdRequest;

public class BIGO_InterstitialAdLoader implements AdLoader {

    private static final String TAG = "bigo interstitial";
    private static Context context;
    private InterstitialAd minterstitialAd;

    public BIGO_InterstitialAdLoader(Activity activity) {
        context = activity.getApplicationContext();
        loadInterAd(); // 预加载
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, context, text);
    }

    @Override
    public void loadAd() {
        loadInterAd();
    }

    @Override
    public void showAd() {
        if (minterstitialAd != null) {
            minterstitialAd.show();
        } else {
            notify("BIGO 插屏广告还未加载好");
        }
    }

    private void loadInterAd() {
        String InterstitialSlotId01 = "10182906-10158798"; // 竖
        String interstitialSlotId02 = "10247107-10379643"; // 横

        /* 1. 构建一个 InterstitialAdRequest 请求体实例，传入广告的 Slot Id */
        InterstitialAdRequest interAdAdRequest = new InterstitialAdRequest.Builder()
                .withSlotId(InterstitialSlotId01)
                .build();

        /* 2. 构建一个 InterstitialAdLoader 加载其实例 */
        InterstitialAdLoader interAdLoader = new InterstitialAdLoader.Builder()
                /* 2.1 传入 AdLoadListener 监听器实例，以接收广告请求结果回调 */
                .withAdLoadListener(new AdLoadListener<InterstitialAd>() {
                    @Override
                    public void onError(@NonNull AdError adError) {
                        // 广告加载过程中出现问题 | There's something wrong during ad loading
                        // jumpToMainPage();
                        BIGO_InterstitialAdLoader.notify("BIGO 插屏广告加载失败，错误代码：" + adError.getCode() + "\n" + " 报错信息：" + adError.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interAd) {
                        BIGO_InterstitialAdLoader.notify("BIGO 插屏广告加载成功");
                        interAd.destroy();
                        minterstitialAd = interAd;
                        onInterstitialAdLoaded(minterstitialAd);
                    }
                }).build();

        // 3. 调用加载器的 loadAd 方法传入请求体，以此发起广告请求
        interAdLoader.loadAd(interAdAdRequest);
    }

    private void onInterstitialAdLoaded(InterstitialAd interstitialAd) {
        interstitialAd.setAdInteractionListener(new AdInteractionListener() {
            // 表示广告使用过程中出现了一些异常。
            @Override
            public void onAdError(@NonNull AdError error) {
                BIGO_InterstitialAdLoader.notify("BIGO 插屏广告曝光失败，错误代码：" + error.getCode() + "\n" + " 报错信息：" + error.getMessage());
            }

            // 表示广告已成功展示在设备屏幕上。
            @Override
            public void onAdImpression() {
            }

            // 表示广告被用户点击。
            @Override
            public void onAdClicked() {
            }

            // 表示全屏的广告页面已打开。
            @Override
            public void onAdOpened() {
            }

            // 表示全屏的广告页面已关闭。
            @Override
            public void onAdClosed() {
                minterstitialAd.destroy();
            }
        });
    }
}