package me.wfeng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static boolean isPrimitive(Object obj) {
        return obj instanceof Boolean
                || obj instanceof Double
                || obj instanceof Float
                || obj instanceof Short
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Byte
                || obj instanceof Character;
    }

    public static <T> Object newMapOrList(Class<T> cls) {
        T t = null;
        if (cls.isInterface()) {
            if (cls == Map.class) {
                return new HashMap<>();
            } else if (isChildOfInterface(cls, Collection.class)) {
                return new ArrayList<>();
            } else {
                new RuntimeException("can not new instance.");
            }
        } else {
            try {
                t = (T) cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public static boolean isChildOfInterface(Class iml, Class inter) {
        Class[] interfaces = iml.getInterfaces();
        for (Class cls : interfaces) {
            if (cls == inter) {
                return true;
            }
        }
        return iml == inter;
    }

}
