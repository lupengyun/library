package com.yun.yunlibrary.okhttp.callback;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.yun.yunlibrary.utils.JsonUtils;
import com.yun.yunlibrary.utils.ToastUtils;

import java.util.List;

import okhttp3.Response;

/**
 * Created by Lupy
 * on 2017/12/25.
 */

public abstract class ShipEntryListCallBack<T> extends Callback<List<T>> {

    private Class<T> tClass;

    public ShipEntryListCallBack(Context context, Class<T> tClass) {
        super(context);
        this.tClass = tClass;
    }

    @Override
    public List<T> parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        Logger.t("okhttp");
        Logger.json(result);
        String code = JsonUtils.getJSONObjectKeyVal(result, "status");
        if ("200".equals(code)) {
            String data = JsonUtils.getJSONObjectKeyVal(result, "body");
            if(TextUtils.isEmpty(data)){
                handlerFail(EMPTY_DATA,"EMPTY_DATA");
                return null;
            }
            List<T> list = JsonUtils.json2List(data, tClass);
            return list;
        } else {
            String message = JsonUtils.getJSONObjectKeyVal(result, "message");
            if (TextUtils.isEmpty(message)) {
                handlError(code);
            } else {
                ToastUtils.showShort(context,message);
            }
            handlerFail(code,message);
            return null;
        }
    }

    /**
     * 处理失败
     * @param code
     * @param message
     */
    public void handlerFail(String code,String message){

    }
}
