package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

//定义一个记录类 参数表示属性, 有(状态码, 数据, 信息)
public record RestBean<T>(int code, T data, String message) {
    public static <T> RestBean<T> success(T data){//接收了数据, 并成功返回
        return new RestBean<>(200, data, "请求成功");
    }
    public static <T> RestBean<T> success(){//没有接收数据,成功返回
        return success(null);
    }
    public static <T> RestBean<T> failure(int code, String message){
        return new RestBean<>(code, null, message);
    }
    public String asJsonString(){//将数据转化为Json
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
