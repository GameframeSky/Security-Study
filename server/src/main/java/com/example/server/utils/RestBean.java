package com.example.server.utils;

import lombok.Data;

/**
 * @author army
 * @date 2023/12/29 13:59
 */

@Data
public class RestBean<T>{
    private int status;
    private boolean success;
    private T message;

    public RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static <T> RestBean<T> success(){
        return new RestBean<>(200 , true, null);
    }

    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200 , true,data );
    }

    public static <T> RestBean<T> failed(){
        return new RestBean<>(401 , false, null);
    }

    public static <T> RestBean<T> failed(T data){
        return new RestBean<>(401 , false, data);
    }
}
