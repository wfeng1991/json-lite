package me.wfeng;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class JSONSerializer {

    private static String FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String toJson(Object object){
        return serialize(object);
    }

    private static String serialize(Object object){
        if(object==null){
            return "null";
        }else if(Utils.isPrimitive(object)){
            return object.toString();
        }else if(object.getClass()==String.class){
            return "'"+object.toString()+"'";
        }else if(Date.class.isAssignableFrom(object.getClass())){
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_PATTERN);
            return serialize(sdf.format(object));
        }else if (object.getClass().isArray()) {
            return serializeArray(object);
        }else if(Collection.class.isAssignableFrom(object.getClass())){
            return serializeCollection((Collection)object);
        }else if (Map.class.isAssignableFrom(object.getClass())) {
            return serializeMap(object);
        }else {
            return serializeObject(object);
        }
    }

    private static String serializeObject(Object object) {
        Class<?> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append('{');
            for (Field f:fields){
                f.setAccessible(true);
                if ("serialVersionUID".equals(f.getName())
                        ||f.getName().startsWith("this$")
                        ||f.isAnnotationPresent(JsonIngore.class)){
                    continue;
                }
                Class<?> fieldClass = f.getType();
                Object value = f.get(object);
                if (value==null){
                    sb.append(serialize(f.getName())+":"+serialize(value)+",");
                }else if (fieldClass.isArray()) {
                    sb.append(serialize(f.getName())+":"+ serializeArray(value)+",");
                }else if(Collection.class.isAssignableFrom(fieldClass)){
                    sb.append(serialize(f.getName())+":"+ serializeCollection((Collection)value)+",");
                }else if (Map.class.isAssignableFrom(fieldClass)) {
                    sb.append(serialize(f.getName())+":"+serializeMap(value)+",");
                }else{
                    sb.append(serialize(f.getName())+":"+serialize(value)+",");
                }
            }
            if(sb.charAt(sb.length()-1)==','){
                sb.deleteCharAt(sb.length()-1);
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("serialize error",e);
        }
    }

    private static String serializeArray(Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int length= Array.getLength(value);
        for(int i=0;i<length;i++)
        {
            Object o=Array.get(value,i);
            sb.append(serialize(o)+",");
        }
        if (length>0){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(']');
        return sb.toString();
    }

    private static String serializeMap(Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Map m = (Map)value;
        Iterator iterator = m.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            sb.append(serialize(entry.getKey())+":"+serialize(entry.getValue())+",");
        }
        if (m.size()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append('}');
        return sb.toString();
    }

    private static String serializeCollection(Collection value){
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Object o:value){
            sb.append(serialize(o)+",");
        }
        if (value.size()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(']');
        return sb.toString();
    }

}
