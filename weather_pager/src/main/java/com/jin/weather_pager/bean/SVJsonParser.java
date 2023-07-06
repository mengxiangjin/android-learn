package com.jin.weather_pager.bean;


import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */
public class SVJsonParser {
    /**
     * 将json转换成bean对象
     * @param data
     * @param clz
     * @return
     */
    public static <T> T fromJson(String data, Class<T> clz) {
        return SVGsonManager.getInstance().getGson().fromJson(data, clz);
    }
    /**
     * 将json转换成list对象
     * @param data
     * @param listType
     * @return
     */
    public static <T> T fromJson(String data, Type listType)
    {
        return SVGsonManager.getInstance().getGson().fromJson(data, listType);
    }
    public static <T> String toJson(T t)
    {
        return SVGsonManager.getInstance().getGson().toJson(t);
    }

    /**
     * 把json字符串转换成集合
     * @param list
     * @param <E>
     * @return
     */
    public static <E> String getJsonStringByList(List<E> list)
    {
        StringBuilder strJson = new StringBuilder("[");
        Gson gson = new Gson();
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                strJson.append(gson.toJson(list.get(i)) + ",");
            } else {
                strJson.append(gson.toJson(list.get(i)));
            }
        }
        strJson = strJson.append("]");
        return strJson.toString();
    }
}
