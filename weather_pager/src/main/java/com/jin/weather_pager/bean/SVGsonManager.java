package com.jin.weather_pager.bean;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/27.
 */

public class SVGsonManager {

    /**
     * 构造一个生成Gson对象单例(懒汉)的gsonManager管理者
     */
    private static SVGsonManager mGsonManager;
    private static Gson mGson;

    private SVGsonManager() {
    }

    ;

    public static SVGsonManager getInstance() {
        synchronized (SVGsonManager.class) {
            if (mGsonManager == null) {
                mGsonManager = new SVGsonManager();
            }
        }
        return mGsonManager;
    }

    /**
     * 返回一个gson对象
     *
     * @return
     */
    public Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        }
        return mGson;
    }

    public class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringNullAdapter();
        }
    }

    public class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            // TODO Auto-generated method stub
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            // TODO Auto-generated method stub
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }
}
