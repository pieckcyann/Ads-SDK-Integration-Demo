package com.xiaoyou.adsdkIntegration.demoapp.ads.bigo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.xiaoyou.adsdkIntegration.demoapp.MainActivity;
import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.SplashAd;
import sg.bigo.ads.api.SplashAdInteractionListener;
import sg.bigo.ads.api.SplashAdLoader;
import sg.bigo.ads.api.SplashAdRequest;

public class BIGO_AppOpenAdLoader implements AdLoader {

    private static final String TAG = "bigo splash";
    private static Context context;
    private final Activity activity;
    private SplashAd mSplashAd;

    private SplashAdRequest splashAdRequest;
    private SplashAdLoader splashAdLoader;

    public BIGO_AppOpenAdLoader(Activity activity) {
        context = activity.getApplicationContext();
        this.activity = activity;

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
        if (mSplashAd != null && !mSplashAd.isExpired()) {
            mSplashAd.show();
        } else {
            notify("BIGO 开屏广告还未加载好");
        }
    }

    private void loadSplashAd() {
        splashAdRequest = new SplashAdRequest.Builder()
                .withSlotId("10182906-10129310")
                .withAppLogo(R.drawable.ic_bug)
                .withAppName(context.getResources().getString(R.string.app_name))
                .build();

        splashAdLoader = new SplashAdLoader.Builder().
                withAdLoadListener(new AdLoadListener<SplashAd>() {
                    @Override
                    public void onError(@NonNull AdError adError) {
                        // There's something wrong during ad loading
                        // jumpToMainPage();
                        BIGO_AppOpenAdLoader.notify("BIGO 开屏广告加载失败，错误代码：" + adError.getCode() + "\n" + " 报错信息：" + adError.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull SplashAd ad) {
                        BIGO_AppOpenAdLoader.notify("BIGO 开屏广告加载成功");

                        mSplashAd = ad;
                        onSplashAdLoaded(ad);
                    }
                }).build();
        splashAdLoader.loadAd(splashAdRequest);
    }

    private void onSplashAdLoaded(SplashAd ad) {
        ad.setAdInteractionListener(new SplashAdInteractionListener() {
            @Override
            public void onAdSkipped() {
                BIGO_AppOpenAdLoader.notify("Splash AD Skipped");
                jumpToMainPage();
            }

            @Override
            public void onAdFinished() {
                BIGO_AppOpenAdLoader.notify("Splash AD Finished");
                mSplashAd.destroy();
                jumpToMainPage();
            }

            @Override
            public void onAdError(@NonNull AdError adError) {
                BIGO_AppOpenAdLoader.notify("BIGO 开屏广告展示失败，错误代码：" + adError.getCode() + "\n" + " 报错信息：" + adError.getMessage());
                mSplashAd = null;
                loadSplashAd();
            }

            @Override
            public void onAdImpression() {

            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdClosed() {
                mSplashAd.destroy();
            }
        });
        // ViewGroup containerView = activity.findViewById(R.id.splash_ad_container);
        // // Show the splash ad
        // ad.showInAdContainer(containerView);
    }

    /**
     * Skip the splash page the jump to main page of app
     */
    private void jumpToMainPage() {
        // 2 秒后跳转回主页
        // new android.os.Handler().postDelayed(() -> {
        activity.startActivity(new Intent(context, MainActivity.class));
        // }, 2000);
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    //     // hold the SplashActivity running when the user press the back button during ad showing
    //     if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
    //         if (mSplashAd != null && !mSplashAd.isSkippable()) {
    //             return true;
    //         }
    //     }
    //     return super.onKeyDown(keyCode, event);
    // }

    // @Override
    // protected void onResume() {
    //     super.onResume();
    //     int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    //     getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    // }

    // @Override
    // protected void onDestroy() {
    //     super.onDestroy();
    //     if (mSplashAd != null) mSplashAd.destroy();
    // }
}
