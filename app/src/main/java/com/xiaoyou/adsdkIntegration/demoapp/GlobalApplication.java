package com.xiaoyou.adsdkIntegration.demoapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.anythink.core.api.ATSDK;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.kwai.network.sdk.KwaiAdSDK;
import com.kwai.network.sdk.api.KwaiInitCallback;
import com.kwai.network.sdk.api.SdkConfig;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalApplication extends Application {
    private static final String MAX_SDK_KEY = "9uHgeBwag3NXva9MC23ToO3q11Ve59bF1uwg4qGltdGmCQ7OSByFZ_3b1ZF7krMlkHQo5gXzIokVDsvg1rwbr-";
    private static final String TAKU_APP_KEY = "a119b998c7158dde7ad7cc134365c7824";
    private static final String TAKU_APP_ID = "h67d39ef1bbfe7";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化各广告 SDK
        initAppLovinMaxSdk();
        initTopOnSdk();
        initKwaiSdk(this);
        // TODO: initBigoSdk();
    }

    // 初始化 AppLovin MAX SDK 和 Adjust SDK
    private void initAppLovinMaxSdk() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppLovinSdkInitializationConfiguration.Builder initConfigBuilder =
                    AppLovinSdkInitializationConfiguration.builder(MAX_SDK_KEY, this)
                            .setMediationProvider(AppLovinMediationProvider.MAX);

            try {
                String currentGaid = AdvertisingIdClient.getAdvertisingIdInfo(this).getId();
                if (currentGaid != null) {
                    initConfigBuilder.setTestDeviceAdvertisingIds(Collections.singletonList(currentGaid));
                }
            } catch (Throwable ignored) {
            }

            AppLovinSdk.getInstance(this).initialize(initConfigBuilder.build(), config -> {
                // AppLovin SDK 初始化完成后，初始化 Adjust
                AdjustConfig adjustConfig = new AdjustConfig(getApplicationContext(), "{YourAppToken}", AdjustConfig.ENVIRONMENT_SANDBOX);
                Adjust.onCreate(adjustConfig);
                registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
            });

            executor.shutdown();
        });
    }

    private void initTopOnSdk() {
        ATSDK.init(getApplicationContext(), TAKU_APP_ID, TAKU_APP_KEY); // 初始化 SDK
        ATSDK.start();    // v6.2.95+，针对国内 SDK，调用 start 启动 SDK。海外 SDK 无调用
        ATSDK.setNetworkLogDebug(true);
    }

    private void initKwaiSdk(Context appContext) {
        KwaiAdSDK.init(appContext, new SdkConfig.Builder()
                .appId("899999")  // 必填，请替换为你申请的 appId
                .token("EaCw0AipSYyvf3E7")   // 必填，请替换为你申请的 token
                .appStoreUrl("https://play.google.com/store/apps/details?id=com.test.video")
                .debug(false) // 接入调试时设为 true，正式版务必为 false
                .setInitCallback(new KwaiInitCallback() {
                    @Override
                    public void onSuccess() {
                        // 初始化成功
                        Log.i("KwaiSDK", "Init success.");
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        // 初始化失败，打印错误码和信息
                        Log.e("KwaiSDK", "Init failed. Code: " + code + ", Msg: " + msg);
                    }
                })
                .build());
    }

    public static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    }

}
