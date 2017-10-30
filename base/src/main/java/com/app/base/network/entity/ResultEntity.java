package com.app.base.network.entity;

/**
 * desc：通用返回模版
 * author：haojie
 * date：2017-06-06
 */
public class ResultEntity<T> {
    //状态值（200,500 等）
    private int code;
    //文字描述
    private String message;
    //返回数据
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
