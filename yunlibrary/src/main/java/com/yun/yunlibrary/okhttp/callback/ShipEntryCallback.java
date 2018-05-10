package com.yun.yunlibrary.okhttp.callback;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.yun.yunlibrary.utils.JsonUtils;
import com.yun.yunlibrary.utils.ToastUtils;

import okhttp3.Response;

/**
 * @author Lupy Create on 2017/11/28
 * @describe
 */

public abstract class ShipEntryCallback<T> extends Callback<T> {

    Class<T> tClass;

    public ShipEntryCallback(Context context, Class<T> tClass) {
        super(context);
        this.tClass = tClass;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        Logger.t("okhttp");
        Logger.json(result);
        String code = JsonUtils.getJSONObjectKeyVal(result, "status");
        if ("200".equals(code)) {
            String data = JsonUtils.getJSONObjectKeyVal(result, "body");
            if (TextUtils.isEmpty(data)) {
                handlerFail(EMPTY_DATA, "body data is empty");
                return null;
            }
            T t = new Gson().fromJson(data, tClass);
            return t;
        } else {
            String message = JsonUtils.getJSONObjectKeyVal(result, "message");
            if (TextUtils.isEmpty(message)) {
                handlError(code);
            } else {
                ToastUtils.showShort(context, message);
            }
            handlerFail(code, message);
            return null;
        }
    }


    /**
     * 处理失败
     *
     * @param code
     * @param message
     */
    public void handlerFail(String code, String message) {

    }
}
