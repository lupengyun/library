package com.yun.yunlibrary.okhttp.callback;

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.yun.yunlibrary.utils.ToastUtils;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T> {

    protected final String EMPTY_DATA = "EMPTY_DATA";

    protected Context context;

    public Callback(Context context) {
        this.context = context;
    }

    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request, int id) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter(int id) {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, long total, int id) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateReponse(Response response, int id) {
        if (!response.isSuccessful()) {
            int errorCode = response.code();
            Logger.t("errorUrl");
            Logger.d("错误地址：" + response.request().url());
            Logger.d("错误码："+errorCode);
            if (String.valueOf(errorCode).startsWith("5") || String.valueOf(errorCode).startsWith("6")) {
                ToastUtils.showShort(context, "服务器错误");
            } else if (String.valueOf(errorCode).startsWith("4")) {
                ToastUtils.showShort(context, "请求错误");
            }
            return false;
        }
        return true;
    }


    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response, int id) throws Exception;

    public void onError(Call call, Exception e, int id) {
        String message = e.getMessage();
        switch (message) {
            case "cancle":
                ToastUtils.showShort(context, "请求取消");
                break;
            case "parseError":
                ToastUtils.showShort(context, "解析异常错误");
                break;
            case "fail":
                ToastUtils.showShort(context, "请求失败");
                break;
        }

        Logger.t("failUrl");
        Logger.d("请求失败地址：" + call.request().url());
    }

    public abstract void onResponse(T response, int id);


    public static Callback CALLBACK_DEFAULT = new Callback(null) {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(Object response, int id) {

        }
    };

    /**
     * 处理服务处理返回错误
     *
     * @param error
     */
    protected void handlError(String error) {
        Logger.t("okhttp");
        Logger.i("error code<=========> %s", error);
        switch (error) {
            case "500":
                ToastUtils.showShort(context, "系统繁忙，请稍后重试");
                break;
            case "400":
                ToastUtils.showShort(context, "表单验证未通过");
                break;
            case "401":
                ToastUtils.showShort(context, "登陆过期，请重新登陆");
                Intent intent = new Intent("com.shipfocus.login");
                context.startActivity(intent);
                break;
            case "403":
                ToastUtils.showShort(context, "暂无权访问");
                break;
            case "300":
                ToastUtils.showShort(context, "缺少必要参数");
                break;
            case "301":
                ToastUtils.showShort(context, "请先登录");
                Intent loginintent = new Intent("com.shipfocus.login");
                context.startActivity(loginintent);
                break;
            case "100":
                ToastUtils.showShort(context, "操作失败");
                break;
        }
    }

}