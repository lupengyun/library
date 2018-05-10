package com.yun.yunlibrary.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.android.tu.loadingdialog.LoadingDialog;
import com.orhanobut.logger.Logger;
import com.yun.yunlibrary.okhttp.builder.GetBuilder;
import com.yun.yunlibrary.okhttp.builder.HeadBuilder;
import com.yun.yunlibrary.okhttp.builder.OtherRequestBuilder;
import com.yun.yunlibrary.okhttp.builder.PostFileBuilder;
import com.yun.yunlibrary.okhttp.builder.PostFormBuilder;
import com.yun.yunlibrary.okhttp.builder.PostStringBuilder;
import com.yun.yunlibrary.okhttp.callback.Callback;
import com.yun.yunlibrary.okhttp.request.RequestCall;
import com.yun.yunlibrary.okhttp.utils.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by zhy on 15/8/17.
 */
public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 10_000L;
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;

//    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();


    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = new ArrayList<Cookie>();
                    if (!TextUtils.isEmpty(UrlConfig.ticket)) {
                        Cookie.Builder builder = new Cookie.Builder();
                        builder.name("ticket");
                        builder.value(UrlConfig.ticket);
                        builder.httpOnly();
                        builder.secure();
//                        builder.path(url.toString());
                        builder.domain(url.host());
//                        builder.path(url.)
                        cookies.add(builder.build());
                    }
                    return cookies;
                }
            })
                    .readTimeout(20000, TimeUnit.MILLISECONDS)
                    .connectTimeout(20000, TimeUnit.MILLISECONDS)
                    .build();
        } else {
            mOkHttpClient = okHttpClient;
        }

        mPlatform = Platform.get();
    }


    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient(null);
    }


    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public static LoadingDialog loadingDialog;

    public static void showLoading(Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        //开始等待界面
        LoadingDialog.Builder builder = new LoadingDialog.Builder(context);
        builder.setMessage("请稍候...");
        builder.setCancelable(false);
        builder.setCancelOutside(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    public static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void execute(final RequestCall requestCall, Callback callback) {


        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();


        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                hideLoading();
//                finalCallback.onError(call,new IOException("fail"),id);
                Logger.t("okhttp");
                Logger.e("requst fails : %s", e.getMessage());
                sendFailResultCallback(call, new IOException("fail"), finalCallback, id);

            }

            @Override
            public void onResponse(final Call call, final Response response) {
                /**
                 * 关闭loading
                 */
                hideLoading();
                try {
                    if (call.isCanceled()) {
                        sendFailResultCallback(call, new IOException("cancle"), finalCallback, id);
                        return;
                    }

                    if (!finalCallback.validateReponse(response, id)) {
                        return;
                    }

                    Object o = finalCallback.parseNetworkResponse(response, id);
                    sendSuccessResultCallback(o, finalCallback, id);
                } catch (Exception e) {
                    Logger.t("okhttp");
                    Logger.e("requst exception :%s", e.getMessage() + e.getStackTrace().toString());
                    e.printStackTrace();
                    sendFailResultCallback(call, new IOException("parseError"), finalCallback, id);
                } finally {
                    if (response.body() != null)
                        response.body().close();
                }

            }
        });
    }


    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {
        if (callback == null) return;

        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e, id);
                callback.onAfter(id);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback == null) return;
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                if (object != null) {
                    callback.onResponse(object, id);
                    callback.onAfter(id);
                }
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public static class METHOD {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}

