package me.wfeng;

import java.util.LinkedHashMap;

/**
 * Created by wfeng on 2018/01/14.
 */
public class JSONObject extends LinkedHashMap {

    public JSONObject() {
    }

    public JSONObject(int size) {
        super(size);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Integer getInt(String key) {
        return (Integer) get(key);
    }

    public Long getLong(String key) {
        return (Long) get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public Double getDouble(String key) {
        return (Double) get(key);
    }

    public Float getFloat(String key) {
        return (Float) get(key);
    }

    public Short getShort(String key) {
        return (Short) get(key);
    }

    public Byte getByte(String key) {
        return (Byte) get(key);
    }

}
