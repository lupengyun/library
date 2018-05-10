package com.yun.yunlibrary.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

/**
 * @author Lupy Create on 2017/12/21
 * @describe 字体颜色工具
 */

public class SpanUtils {
    /**
     * 获取相应的颜色的span
     *
     * @param str
     * @param color
     * @return
     */
    public static SpannableString getColorSpan(String str, int color) {
        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(foregroundColorSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 获取相应大小的span
     *
     * @param str
     * @param size
     * @return
     */
    public static SpannableString getSizeSpan(String str, int size) {
        SpannableString spannableString = new SpannableString(str);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(size);
        spannableString.setSpan(absoluteSizeSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
