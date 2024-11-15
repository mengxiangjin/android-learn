package com.jin.http_net;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class GsonRequest<T> extends Request<T> {

    private Response.Listener<T> mListener;
    private Gson gson;
    private Class mClass;

    public GsonRequest(Class aClass,int method, String url, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        gson = new Gson();
        mListener = listener;
        mClass = aClass;
    }

    public GsonRequest(Class aClass,String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(aClass,Method.GET, url, listener, errorListener);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (mListener != null) {
            mListener = null;
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        T obj = (T) gson.fromJson(parsed, mClass);
        return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
}
