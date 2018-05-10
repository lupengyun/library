package com.yun.yunlibrary.okhttp.callback;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.yun.yunlibrary.utils.JsonUtils;
import com.yun.yunlibrary.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class StringCallback extends Callback<String> {
    public StringCallback(Context context) {
        super(context);
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        String result = response.body().string();
        Logger.t("okhttp");
        Logger.json(result);
        String code = JsonUtils.getJSONObjectKeyVal(result, "status");
        if ("200".equals(code)) {
            String resultFinal = JsonUtils.getJSONObjectKeyVal(result, "body");
            if (TextUtils.isEmpty(resultFinal)) {
                handlerFail(EMPTY_DATA, "body data is empty");
            }
            return resultFinal;
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
