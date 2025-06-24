package com.jin.movie.bean;

public class RespPlayBack {

    private boolean success;
    private int code;
    private String message;
    private RespPlayBackList data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RespPlayBackList getData() {
        return data;
    }

    public void setData(RespPlayBackList data) {
        this.data = data;
    }
}
