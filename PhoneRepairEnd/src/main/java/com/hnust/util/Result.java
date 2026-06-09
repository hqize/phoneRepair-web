package com.hnust.util;

import lombok.Data;

@Data
public class Result<T> {
    private int code; //状态码(登录成功=200,登录失败=400)
    private String msg; //提示信息
    private Object data; //返回数据
    private long timestamp; //时间戳

    //成功响应：提示信息+返回的数据
    public static <T> Result<T> success(String msg, T data) { //返回成功的result
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static <T> Result<T> success(T data) { //返回数据
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    //失败响应：状态码+提示信息
    public static <T> Result<T> fail(String msg, Integer code) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}