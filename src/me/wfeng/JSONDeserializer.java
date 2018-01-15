package me.wfeng;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class JSONDeserializer {

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
        } else {
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
        } else if (isPrimitive(jsonObject) || jsonObject.getClass() == String.class) {
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
            Collection collection = (Collection) newMapOrList(Collection.class);
            JSONArray jsonArray = (JSONArray) jsonObject;
            for (Object o : jsonArray) {
                collection.add(deserialize(o, Object.class));
            }
            return (T) jsonArray;
        } else if (jsonObject instanceof Map && cls == Object.class) {
            Map m = (Map) newMapOrList(Map.class);
            JSONObject jsonObj = (JSONObject) jsonObject;
            Iterator<Map.Entry> iterator = jsonObj.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                m.put(deserialize(entry.getKey(), Object.class), deserialize(entry.getValue(), Object.class));
            }
            return (T) m;
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
            Class<?> declaringClass = f.getType();
            Object obj = ((JSONObject) jsonObject).get(f.getName());
            if (obj == null) {
                continue;
            }
            Object value;
            if (declaringClass.isArray()) {
                Class<?> componentType = declaringClass.getComponentType();
                JSONArray jsonArray = (JSONArray) obj;
                Object array = Array.newInstance(componentType, jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    Array.set(array, i, deserialize(jsonArray.get(i), componentType));
                }
                value = array;
            } else if (Collection.class.isAssignableFrom(declaringClass)) {
                Collection collection = (Collection) newMapOrList(declaringClass);
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
            } else if (Map.class.isAssignableFrom(declaringClass)) {
                Map m = (Map) newMapOrList(declaringClass);
                JSONObject jsonObj = (JSONObject) obj;
                Iterator<Map.Entry> iterator = jsonObj.entrySet().iterator();
                Class genericType0 = Object.class;
                Class genericType1 = Object.class;
                try {
                    genericType0 = (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
                    genericType1 = (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[1];
                } catch (Exception ignore) {
                }
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

    private static boolean isPrimitive(Object cls) {
        return cls instanceof Boolean
                || cls instanceof Double
                || cls instanceof Float
                || cls instanceof Short
                || cls instanceof Integer
                || cls instanceof Long
                || cls instanceof String
                || cls instanceof Byte
                || cls instanceof Character;
    }

    private static <T> Object newMapOrList(Class<T> cls) {
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

    private static boolean isChildOfInterface(Class iml, Class inter) {
        Class[] interfaces = iml.getInterfaces();
        for (Class cls : interfaces) {
            if (cls == inter) {
                return true;
            }
        }
        return iml == inter;
    }

}
