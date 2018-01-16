package me.wfeng;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JSONDeserializer {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static <T> T fromJson(String json, Class<T> cls) {
        if (cls == Boolean.class || cls == boolean.class) {
            return (T) Boolean.valueOf(json.trim());
        } else if (cls == Integer.class || cls == int.class) {
            return (T) Integer.valueOf(json.trim());
        } else if (cls == Long.class || cls == long.class) {
            return (T) Long.valueOf(json.trim());
        } else if (cls == Byte.class || cls == byte.class) {
            return (T) Byte.valueOf(json.trim());
        } else if (cls == Short.class || cls == short.class) {
            return (T) Short.valueOf(json.trim());
        }else if (Date.class.isAssignableFrom(cls)) {
            try {
                return (T) sdf.parse(json.trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }else {
            Object jsonObject = JSON.parse(json);
            if (cls == null && jsonObject == null) {
                return null;
            } else if (cls == null && jsonObject != null) {
                throw new RuntimeException("can not cast to null");
            } else {
                return deserialize(jsonObject, cls);
            }
        }
    }

    private static <T> T deserialize(Object jsonObject, Class<T> cls) {

        if (jsonObject == null) {
            return null;
        } else if (Utils.isPrimitive(jsonObject) || jsonObject.getClass() == String.class) {
            return (T) jsonObject;
        } else if (cls.isArray()) {
            Class<?> componentType = cls.getComponentType();
            JSONArray jsonArray = (JSONArray) jsonObject;
            Object array = Array.newInstance(componentType, jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                Array.set(array, i, deserialize(jsonArray.get(i), componentType));
            }
            return (T) array;
        } else if (jsonObject instanceof Collection && cls == Object.class) {
            Collection collection = (Collection) Utils.newMapOrList(Collection.class);
            JSONArray jsonArray = (JSONArray) jsonObject;
            for (Object o : jsonArray) {
                collection.add(deserialize(o, Object.class));
            }
            return (T) jsonArray;
        } else if (jsonObject instanceof Map && cls == Object.class) {
            Map m = (Map) Utils.newMapOrList(Map.class);
            JSONObject jsonObj = (JSONObject) jsonObject;
            Iterator<Map.Entry> iterator = jsonObj.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                m.put(deserialize(entry.getKey(), Object.class), deserialize(entry.getValue(), Object.class));
            }
            return (T) m;
        }else if (Date.class.isAssignableFrom(cls) && cls == Object.class) {
            try {
                return (T) sdf.parse(jsonObject.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
        Field[] fields = cls.getDeclaredFields();
        T t = null;
        try {
            t = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Field f : fields) {
            f.setAccessible(true);
            Class<?> fieldClass = f.getType();
            Object obj = ((JSONObject) jsonObject).get(f.getName());
            if (obj == null) {
                continue;
            }
            Object value;
            if (fieldClass.isArray()) {
                Class<?> componentType = fieldClass.getComponentType();
                JSONArray jsonArray = (JSONArray) obj;
                Object array = Array.newInstance(componentType, jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    Array.set(array, i, deserialize(jsonArray.get(i), componentType));
                }
                value = array;
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Collection collection = (Collection) Utils.newMapOrList(fieldClass);
                JSONArray jsonArray = (JSONArray) obj;
                Class genericType = Object.class;
                try {
                    genericType = (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
                } catch (Exception ignore) {
                }
                for (Object o : jsonArray) {
                    collection.add(deserialize(o, genericType));
                }
                value = collection;
            } else if (Map.class.isAssignableFrom(fieldClass)) {
                Map m = (Map) Utils.newMapOrList(fieldClass);
                Class genericType0 = Object.class;
                Class genericType1 = Object.class;
                try {
                    genericType0 = (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
                    genericType1 = (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[1];
                } catch (Exception ignore) {
                }
                JSONObject jsonObj = (JSONObject) obj;
                Iterator<Map.Entry> iterator = jsonObj.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    m.put(deserialize(entry.getKey(), genericType0), deserialize(entry.getValue(), genericType1));
                }
                value = m;
            } else {
                value = obj;
            }
            try {
                f.set(t, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

}
