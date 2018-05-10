package com.yun.yunlibrary.okhttp.builder;

import  com.yun.yunlibrary.okhttp.OkHttpUtils;
import  com.yun.yunlibrary.okhttp.request.OtherRequest;
import  com.yun.yunlibrary.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
