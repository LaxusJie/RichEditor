package com.app.base.utils;

/**
 * desc：Token工具类
 * author：haojie
 * date：2017-06-29
 */
public class TokenUtils {

    private TokenUtils(){}

    private static TokenUtils instance;

    public static TokenUtils getInstance() {
        if(instance == null) {
            instance = new TokenUtils();
        }
        return instance;
    }

    private String token = "";

    public String getToken() {
        return token;
    }

    public void saveToken(String token) {
        this.token = token;
    }
}
