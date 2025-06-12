package com.xiaoyou.adsdkIntegration.demoapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 将这里的max的sdk的初始化封装一下，因为我还要同时初始化其他平台（例如topon）的sdk
public class GlobalApplication
        extends Application {
    // If you want to test your own AppLovin SDK key, change the value here and update the package name in the build.gradle
    // private static final String YOUR_SDK_KEY = "05TMDQ5tZabpXQ45_UTbmEGNUtVAzSTzT6KmWQc5_CuWdzccS4DCITZoL3yIWUG3bbq60QC_d4WF28tUC4gVTF";
    private static final String YOUR_SDK_KEY = "9uHgeBwag3NXva9MC23ToO3q11Ve59bF1uwg4qGltdGmCQ7OSByFZ_3b1ZF7krMlkHQo5gXzIokVDsvg1rwbr-";

    @Override
    public void onCreate() {
        super.onCreate();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            AppLovinSdkInitializationConfiguration.Builder initConfigBuilder = AppLovinSdkInitializationConfiguration.builder(YOUR_SDK_KEY, this);
            initConfigBuilder.setMediationProvider(AppLovinMediationProvider.MAX);

            try {
                // Enable test mode by default for the current device. Cannot be run on the main thread.
                String currentGaid = AdvertisingIdClient.getAdvertisingIdInfo(this).getId();
                if (currentGaid != null) {
                    initConfigBuilder.setTestDeviceAdvertisingIds(Collections.singletonList(currentGaid));
                }
            } catch (Throwable ignored) {
            }

            // Initialize the AppLovin SDK
            AppLovinSdk.getInstance(this).initialize(initConfigBuilder.build(), appLovinSdkConfiguration -> {
                // AppLovin SDK is initialized, start loading ads now or later if ad gate is reached

                // Initialize Adjust SDK
                AdjustConfig adjustConfig = new AdjustConfig(getApplicationContext(), "{YourAppToken}", AdjustConfig.ENVIRONMENT_SANDBOX);
                Adjust.onCreate(adjustConfig);

                registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
            });

            executor.shutdown();
        });
    }

    private static final class AdjustLifecycleCallbacks
            implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull final Activity activity, @Nullable final Bundle bundle) {
        }

        @Override
        public void onActivityStarted(@NonNull final Activity activity) {
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
        public void onActivityStopped(@NonNull final Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull final Activity activity, @NonNull final Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(@NonNull final Activity activity) {
        }
    }
}
