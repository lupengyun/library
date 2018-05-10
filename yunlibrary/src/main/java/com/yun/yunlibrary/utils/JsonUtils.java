package com.yun.yunlibrary.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lupy Create on 2017/12/14
 * @describe
 */

public class JsonUtils {

    private static final Gson GSON;

    static {
        GSON = new Gson();
//        GSON = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()//打开Export注解，但打开了这个注解,副作用，要转换和不转换都要加注解
//                .setDateFormat("yyyy-MM-dd HH:mm:ss")//序列化日期格式  "yyyy-MM-dd"
//                .setPrettyPrinting() //自动格式化换行
//                .create();
    }

    public static Gson getGson() {
        return GSON;
    }

    /**
     * 转换为json对象
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 通过key获取value
     *
     * @param json
     * @param key
     * @return
     */
    public static String getJSONObjectKeyVal(String json, String key) {
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object == null || key == null) {
            return "";
        }
        String result = null;
        Object obj;
        try {
            obj = object.get(key);
            if (obj == null || obj.equals(null)) {
                result = "";
            } else {
                result = obj.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将json转化为cls对象
     *
     * @param jsonString   json字符串
     * @param cls          对应的类
     * @param defaultValue 默认值
     * @return
     */
    public static <T> T json2Object(String jsonString, Class<T> cls, T defaultValue) {
        T t = defaultValue;
        t = GSON.fromJson(jsonString, cls);
        return t;
    }

    /**
     * 将json转化为cls对象，默认值为null
     *
     * @param jsonString json字符串
     * @param cls        对应的类
     * @return
     */
    public static <T> T json2Object(String jsonString, Class<T> cls) {
        return json2Object(jsonString, cls, null);
    }

    /**
     * 转换为数组
     *
     * @param jsonString
     * @param <T>
     * @return
     */
    public static <T> List<T> json2List(String jsonString, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(jsonString).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(new Gson().fromJson(elem, tClass));
        }
        return list;
    }
}
