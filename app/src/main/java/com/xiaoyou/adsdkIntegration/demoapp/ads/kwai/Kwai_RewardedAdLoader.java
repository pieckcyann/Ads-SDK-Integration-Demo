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
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAd;
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdConfig;
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdRequest;
import com.kwai.network.sdk.loader.business.reward.interf.IKwaiRewardAdListener;
import com.kwai.network.sdk.loader.common.interf.AdLoadListener;
import com.kwai.network.sdk.loader.common.interf.IKwaiAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.kwai.Config;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

public class Kwai_RewardedAdLoader implements AdLoader {

    private static final String TAG = "KwaiRewardLoader";
    private static Context context;
    private final Activity activity;
    private KwaiRewardAd mKwaiRewardAd = null;
    private IKwaiAdLoader<KwaiRewardAdRequest> mKwaiRewardAdLoader = null;

    public Kwai_RewardedAdLoader(Activity activity) {
        context = activity.getApplicationContext();
        this.activity = activity;
        loadRewardAd();
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, context, text);
    }

    /**
     * 请求激励广告 | Request for reward ads
     */
    @Override
    public void loadAd() {
        loadRewardAd();
    }

    private void loadRewardAd() {
        // 此为测试tagId，应用发布后请使用后台申请的正式tagId ｜ This is a test tagId. Please use the official tagId applied in the backend after the application is released
        String tagId = Config.getInstance(context).getRewardTagId();
        String floorPrice = Config.getInstance(context).getFloorPrice();
        String extInfo = Config.getInstance(context).getExtInfo();

        KwaiAdLoaderManager loaderManager = KwaiAdSDK.getKwaiAdLoaderManager();
        if (loaderManager != null) {
            mKwaiRewardAdLoader = loaderManager.buildRewardAdLoader(
                    new KwaiRewardAdConfig
                            .Builder(new RewardAdLoadListener()) // 回调函数会告知请求数据成功与否以及将 KwaiRewardAd 数据返回回来。
                            .withKwaiRewardAdListener(new RewardAdListener()) // 回调函数是监听用户行为的，比如激励广告展示、用户点击、播放完成、获取奖励等监听
                            .build()
            );

            KwaiRewardAdRequest request = new KwaiRewardAdRequest(tagId);
            request.extParams.put(AllianceConstants.Request.BID_FLOOR_PRICE, floorPrice);
            request.extParams.put(AllianceConstants.Request.MEDIATION_EXT_INFO, extInfo);

            mKwaiRewardAdLoader.loadAd(request);
        } else {
            notify("KwaiAdLoaderManager is null, SDK might not be initialized");
        }
    }

    @Override
    public void showAd() {
        // onAdLoadSuccess 回调会将 mKwaiRewardAd 设置为 KwaiRewardAd 类型变量，且一定不为空
        // 展示数据之前需要判断数据是否 ready
        if (mKwaiRewardAd != null && mKwaiRewardAd.isReady() && activity != null) {
            mKwaiRewardAd.show(activity);
        } else {
            notify("Kwai 激励广告还未准备好");
        }
    }

    // 发送竞价函数
    public void sendBidWin() {
        if (mKwaiRewardAd != null) {
            String minWinPrice = Config.getInstance(context).getMinWinPrice();
            mKwaiRewardAd.getBidController().sendBidWin(minWinPrice);
        }
    }

    // 发送竞价失败
    public void sendBidLose() {
        if (mKwaiRewardAd != null) {
            String loseCode = Config.getInstance(context).getLoseCode();
            String winPrice = Config.getInstance(context).getWinAdPrice();
            mKwaiRewardAd.getBidController().sendBidLose(loseCode, winPrice);
        }
    }


    // 页面关闭的时候，做一下资源清理，建议做一下 | It is recommended to clean up the resources when the page is closed
    public void release() {
        if (mKwaiRewardAdLoader != null) {
            mKwaiRewardAdLoader.release();
            mKwaiRewardAdLoader = null;
        }
    }

    private static class RewardAdListener implements IKwaiRewardAdListener {

        @Override
        public void onAdShow() {
            Log.i(TAG, "onAdShow");
        }

        @Override
        public void onAdShowFailed(@NonNull KwaiError error) {
            Kwai_RewardedAdLoader.notify("Kwai 激励广告展示失败，错误代码：" + error.getCode() + "\n" + " 报错信息：" + error.getMsg());
        }

        @Override
        public void onAdClick() {
            Log.i(TAG, "onAdClick");
        }

        @Override
        public void onAdPlayComplete() {
            Log.i(TAG, "onAdPlayComplete");
        }

        @Override
        public void onRewardEarned() {
            Log.i(TAG, "onAdEarned");
        }

        @Override
        public void onAdClose() {
            Log.i(TAG, "onAdClose");
        }
    }

    /// ///////////////////////////////// Callbacks ////////////////////////////////////////

    private class RewardAdLoadListener implements AdLoadListener<KwaiRewardAd> {

        @Override
        public void onAdLoadStart(@Nullable String trackId) {
            // Kwai_RewardedAdLoader.notify("Kwai 激励广告开始加载");
        }

        @Override
        public void onAdLoadSuccess(@Nullable String trackId, @NonNull KwaiRewardAd kwaiRewardAd) {
            mKwaiRewardAd = kwaiRewardAd;
            Kwai_RewardedAdLoader.notify("Kwai 激励广告加载成功，报价为：" + kwaiRewardAd.getPrice());
        }

        @Override
        public void onAdLoadFailed(@Nullable String trackId, @NonNull KwaiError kwaiError) {
            Kwai_RewardedAdLoader.notify("Kwai 激励广告展示失败，错误代码：" + kwaiError.getCode() + "\n" + " 报错信息：" + kwaiError.getMsg());
        }
    }
}
