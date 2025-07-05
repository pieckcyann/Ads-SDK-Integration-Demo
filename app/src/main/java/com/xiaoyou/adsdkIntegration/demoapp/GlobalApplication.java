package com.xiaoyou.adsdkIntegration.demoapp;

import static com.xiaoyou.adsdkIntegration.demoapp.constants.KeysConfig.BIGO_APP_ID;
import static com.xiaoyou.adsdkIntegration.demoapp.constants.KeysConfig.KWAI_APP_ID;
import static com.xiaoyou.adsdkIntegration.demoapp.constants.KeysConfig.KWAI_TOKEN;
import static com.xiaoyou.adsdkIntegration.demoapp.constants.KeysConfig.MAX_SDK_KEY;
import static com.xiaoyou.adsdkIntegration.demoapp.constants.KeysConfig.TOPON_APP_ID;
import static com.xiaoyou.adsdkIntegration.demoapp.constants.KeysConfig.TOPON_APP_KEY;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.anythink.core.api.ATSDK;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.kwai.network.sdk.KwaiAdSDK;
import com.kwai.network.sdk.api.KwaiInitCallback;
import com.kwai.network.sdk.api.SdkConfig;

import java.util.Collections;

import sg.bigo.ads.BigoAdSdk;
import sg.bigo.ads.api.AdConfig;

public class GlobalApplication extends Application {

    private static GlobalApplication instance;

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.xiaoyou.action.DUMP_INFO");
        filter.addAction("dumpInfo");
        filter.addAction("dumpAdMarketUrl");

        DebugInfoReceiver receiver = new DebugInfoReceiver();
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);


        // 初始化各广告 SDK
        initAppLovinMaxSdk();
        initTopOnSdk();
        initKwaiSdk(this);
        initBigoSdk();
    }

    // 初始化 AppLovin MAX SDK 和 Adjust SDK
    private void initAppLovinMaxSdk() {

        // ExecutorService executor = Executors.newSingleThreadExecutor();
        // executor.execute(() -> {
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

        AppLovinSdk.getInstance(this)
                .initialize(initConfigBuilder.build(), config -> {
                    // AppLovin SDK 初始化完成后，初始化 Adjust
                    // AdjustConfig adjustConfig = new AdjustConfig(getApplicationContext(), "{YourAppToken}", AdjustConfig.ENVIRONMENT_SANDBOX);
                    // Adjust.onCreate(adjustConfig);
                    // registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
                });

        // AppLovinSdk.getInstance(this).showMediationDebugger();

        AppLovinSdk sdk = AppLovinSdk.getInstance(this);

        sdk.getSettings().setVerboseLogging(true);

        // sdk.getSettings().setMediatedNetworkConfigurations(
        //         Collections.singletonMap("applovin", null) // 👈 禁用 AppLovin Exchange
        // );
        // executor.shutdown();
        // });

    }

    private void initTopOnSdk() {
        ATSDK.init(getApplicationContext(), TOPON_APP_ID, TOPON_APP_KEY); // 初始化 SDK
        // ATSDK.start();    // v6.2.95+，针对国内 SDK，调用 start 启动 SDK。海外 SDK 无调用
        ATSDK.setNetworkLogDebug(true);
    }

    private void initKwaiSdk(Context appContext) {
        KwaiAdSDK.init(appContext, new SdkConfig.Builder()
                .appId(KWAI_APP_ID)  // 必填，请替换为你申请的 appId
                .token(KWAI_TOKEN)   // 必填，请替换为你申请的 token
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

    private void initBigoSdk() {
        AdConfig config = new AdConfig.Builder()
                .setAppId(BIGO_APP_ID)
                // .setDebug(BuildConfig.DEBUG)
                // .setChannel("<your-app-channel>")
                // .setAge("<your-app-user-age>")
                // .setGender(AdConfig.GENDER_MALE)
                // .setActivatedTime("<your-app-activated-timestamp>")
                .build();

        BigoAdSdk.initialize(this, config, new BigoAdSdk.InitListener() {
            @Override
            public void onInitialized() {
                // You can request ads now!
                Log.i("BigoAdSdk", "Init success.");
                // Notify.notify("BigoAdSdk", GlobalApplication.this, "Init success.");
            }
        });
    }

    // public static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
    //     @Override
    //     public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    //     }
    //
    //     @Override
    //     public void onActivityStarted(@NonNull Activity activity) {
    //     }
    //
    //     @Override
    //     public void onActivityResumed(@NonNull Activity activity) {
    //         Adjust.onResume();
    //     }
    //
    //     @Override
    //     public void onActivityPaused(@NonNull Activity activity) {
    //         Adjust.onPause();
    //     }
    //
    //     @Override
    //     public void onActivityStopped(@NonNull Activity activity) {
    //     }
    //
    //     @Override
    //     public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    //     }
    //
    //     @Override
    //     public void onActivityDestroyed(@NonNull Activity activity) {
    //     }
    // }

}
