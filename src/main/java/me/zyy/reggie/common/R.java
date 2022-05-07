package me.zyy.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> {

    private Integer code;
    private T data;
    private String msg;

    private Map map = new HashMap(); //动态数据

    public R(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public R(Integer code, String msg) {
        this(code, null, msg);
    }

    public static <T> R<T> success(Integer code, T data, String msg) {
        return new R<>(code, data, msg);
    }

    public static <T> R<T> success(Integer code, String msg) {
        return new R<>(code,  msg);
    }

    public static <T> R<T> error(Integer code, String msg) {
        return new R<>(code,  msg);
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
