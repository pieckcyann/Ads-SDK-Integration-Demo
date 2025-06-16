package com.xiaoyou.adsdkIntegration.demoapp.ads.kwai;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kwai.network.sdk.KwaiAdSDK;
import com.kwai.network.sdk.api.KwaiAdLoaderManager;
import com.kwai.network.sdk.constant.KwaiError;
import com.kwai.network.sdk.event.AllianceConstants;
import com.kwai.network.sdk.loader.business.interstitial.data.KwaiInterstitialAd;
import com.kwai.network.sdk.loader.business.interstitial.data.KwaiInterstitialAdConfig;
import com.kwai.network.sdk.loader.business.interstitial.data.KwaiInterstitialAdRequest;
import com.kwai.network.sdk.loader.business.interstitial.interf.IKwaiInterstitialAdListener;
import com.kwai.network.sdk.loader.common.interf.AdLoadListener;
import com.kwai.network.sdk.loader.common.interf.IKwaiAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.kwai.Config;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

// 将这个BIGO示例类也改为一个loader
public class Kwai_InterstitialLoader implements AdLoader {

    private static final String TAG = "kwai interstitial";
    private static Context context;
    private final Activity activity;

    @Nullable
    private KwaiInterstitialAd mKwaiInterstitialAd = null;

    @Nullable
    private IKwaiAdLoader<KwaiInterstitialAdRequest> mKwaiInterstitialAdLoader = null;

    public Kwai_InterstitialLoader(Activity activity) {
        context = activity.getApplicationContext();
        this.activity = activity;
        loadInterAd();
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, context, text);
    }

    /**
     * 请求插屏广告 ｜ Request interstitial ad
     */
    @Override
    public void loadAd() {
        loadInterAd();
    }

    private void loadInterAd() {
        // 此为测试tagId，应用发布后请使用后台申请的正式tagId ｜ This is a test tagId. Please use the official tagId applied in the backend after the application is released
        String tagId = Config.getInstance(context).getInterstitialTagId();
        String floorPrice = Config.getInstance(context).getFloorPrice();
        // 额外信息，可选，长度不能超过2048 ｜ 2048Additional information, optional, length cannot exceed 2048
        String extInfo = Config.getInstance(context).getExtInfo();
        // 初始化失败或者未初始化SDK，获取的loaderManager为空 ｜ Initialization failed or SDK not initialized. The loaderManager obtained is empty
        KwaiAdLoaderManager loaderManager = KwaiAdSDK.getKwaiAdLoaderManager();
        if (loaderManager != null) {
            // 构建一个Loader对象，｜ Construct a Loader object
            // ps:
            // 1. 一个Loader只能执行一次load ｜ A loader can only perform a load once
            // 2. 支持预载，只需提前调用loadAd ｜ Preloading is supported, just call loadAd in advance
            mKwaiInterstitialAdLoader = loaderManager.buildInterstitialAdLoader(
                    new KwaiInterstitialAdConfig
                            .Builder(new InterstitialAdLoadListener())
                            .withKwaiInterstitialAdListener(new InterstitialAdListener())
                            .build()
            );
            // 开始发起请求 ｜ Starting a request
            KwaiInterstitialAdRequest kwaiInterstitialAdRequest = new KwaiInterstitialAdRequest(tagId);
            // 选填，设置底价，默认是0 ｜ Optional, set the floor price as 0 by default
            kwaiInterstitialAdRequest.extParams.put(AllianceConstants.Request.BID_FLOOR_PRICE,
                    floorPrice);
            // 选填，接收map json字符串，ps：整个字符串长度不能超过2048。| Optional, receive map json string, ps: the length of the whole string can not exceed 2048.
            kwaiInterstitialAdRequest.extParams.put(AllianceConstants.Request.MEDIATION_EXT_INFO, extInfo);
            mKwaiInterstitialAdLoader.loadAd(kwaiInterstitialAdRequest);
        } else {
            notify("KwaiAdLoaderManager is null, SDK might not be initialized");
        }
    }

    /**
     * 展示插屏广告 | Show interstitial ad
     */
    @Override
    public void showAd() {
        // 展示之前判断数据是否ready | Determine if the data is ready before displaying
        if (mKwaiInterstitialAd != null && mKwaiInterstitialAd.isReady() && activity != null) {
            mKwaiInterstitialAd.show(activity);
        } else {
            notify("Kwai 插屏广告还未准备好");
        }
    }

    private void mockBidWin() {
        if (mKwaiInterstitialAd != null) {
            // 如果使用我们SDK的广告没有涉及到竞价，当然这个方法也就没有必要调用了 | If bidding is not involved in the ads that use our SDK, there is no need for this method to be called
            // 真实场景，这个埋点具体的上报时机如下 | For a real-world scenario, the specific timing of this event tracking is reported as follows
            // 1 -- 广告数据加载成功 | Ad data loaded successfully
            // 2 -- 使用了我们的广告参与了竞价，并且我们的广告价格最高 | Used our ads to participate in a bidding process and our ads were the highest priced
            // ps: 两个条件是且的关系，1 && 2 调用sendBidWin | ps: two conditions are and relations, 1 && 2 call sendBidWin
            // param: minWinPrice代表次高价，类型是String，单位是$ | param: minWinPrice represents the next highest price and the type is String and unit is $
            String minWinPrice = Config.getInstance(context).getMinWinPrice();
            mKwaiInterstitialAd.getBidController().sendBidWin(minWinPrice);
        }
    }

    private void mockBidLose() {
        // 如果使用我们SDK的广告没有涉及到竞价，当然这个方法也就没有必要调用了 | If bidding is not involved in the ads that use our SDK, there is no need for this method to be called
        // 真实场景，这个埋点具体的上报时机如下 | For a real-world scenario, the specific timing of this event tracking is reported as follows
        // 1 -- 广告数据加载成功 | Ad data loaded successfully
        // 2 -- 使用了我们的广告参与了竞价，但是价格不是最高 | Used our ads to participate in the bidding, but the price was not the highest
        // ps: 两个条件是且的关系，1 && 2 调用sendBidLose | ps: two conditions are and relations, 1 && 2 call sendBidWin
        // param:loseCode 竞败原因 详见接入文档定义OPEN-RTB标准协议保持一致 | Reason for failure See intergration document definitions and consistent with OPEN-RTB Standard Protocol
        // param:winPrice 胜出的广告的价格，类型是String，单位是$ | param:winPrice the price of the winning ad, type is String, unit is $
        if (mKwaiInterstitialAd != null) {
            String loseCode = Config.getInstance(context).getLoseCode();
            String winPrice = Config.getInstance(context).getWinAdPrice();
            mKwaiInterstitialAd.getBidController().sendBidLose(loseCode, winPrice);
        }
    }

    // 页面关闭的时候，做一下资源清理，建议做一下 | It is recommended to clean up the resources when the page is closed
    public void release() {
        if (mKwaiInterstitialAdLoader != null) {
            mKwaiInterstitialAdLoader.release();
            mKwaiInterstitialAdLoader = null;
        }
    }

    ///////////////////////////////////// 回调 | Callback /////////////////////////////////////////////

    /**
     * 插屏广告页面状态监听 | Interstitial ads page status listening
     */
    private static class InterstitialAdListener implements IKwaiInterstitialAdListener {

        /**
         * 页面打开成功 | Page opens successfully
         */
        @Override
        public void onAdShow() {
            Kwai_InterstitialLoader.notify("onAdShow");
        }

        /**
         * 页面打开失败 | Page open failure
         */
        @Override
        public void onAdShowFailed(@NonNull KwaiError error) {
            Kwai_InterstitialLoader.notify("Kwai 插屏广告打开失败，错误代码：" + error.getCode() + "\n" + " 报错信息：" + error.getMsg());
        }

        /**
         * 页面发生点击 | A click on the page occurs
         */
        @Override
        public void onAdClick() {
            Log.i(TAG, "onAdClick");
        }


        /**
         * 页面关闭 | Page close
         */
        @Override
        public void onAdClose() {
            Log.i(TAG, "onAdClose");
        }

        /**
         * 视频播放完成 | Video play complete
         */
        @Override
        public void onAdPlayComplete() {
            Log.i(TAG, "onAdPlayComplete");
        }
    }

    /**
     * 插屏广告加载监听 | Load listener for interstitial ads
     */
    private class InterstitialAdLoadListener implements AdLoadListener<KwaiInterstitialAd> {

        @Override
        public void onAdLoadStart(@Nullable String trackId) {
            // Kwai_InterstitialLoader.notify("Kwai 插屏广告开始加载");
        }

        @Override
        public void onAdLoadSuccess(@Nullable String trackId,
                                    @NonNull KwaiInterstitialAd kwaiInterstitialAd) {
            mKwaiInterstitialAd = kwaiInterstitialAd;
            Kwai_InterstitialLoader.notify("Kwai 插屏广告加载成功，报价为：" + kwaiInterstitialAd.getPrice());
        }

        @Override
        public void onAdLoadFailed(@Nullable String trackId, @NonNull KwaiError kwaiError) {
            Kwai_InterstitialLoader.notify("Kwai 插屏广告展示失败，错误代码：" + kwaiError.getCode() + "\n" + " 报错信息：" + kwaiError.getMsg());

        }
    }

}