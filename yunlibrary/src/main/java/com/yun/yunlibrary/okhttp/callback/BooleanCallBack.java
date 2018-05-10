package com.yun.yunlibrary.okhttp.callback;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.yun.yunlibrary.utils.JsonUtils;
import com.yun.yunlibrary.utils.ToastUtils;

import okhttp3.Response;

/**
 * Created by Lupy
 * on 2018/3/30.
 */

public abstract class BooleanCallBack extends Callback<Boolean> {
    public BooleanCallBack(Context context) {
        super(context);
    }

    @Override
    public Boolean parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        Logger.t("okhttp");
        Logger.json(result);
        String code = JsonUtils.getJSONObjectKeyVal(result, "status");
        if ("200".equals(code)) {
            return true;
        } else {
            ToastUtils.showShort(context,JsonUtils.getJSONObjectKeyVal(result, "message"));
            return false;
        }
    }

}
