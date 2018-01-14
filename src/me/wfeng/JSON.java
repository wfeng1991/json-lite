package me.wfeng;

import java.text.ParseException;

/**
 * Created by wfeng on 2018/01/14.
 */
public class JSON {

    private static int ptr;

    public static Object parse(String json){
        ptr=0;
        if(json==null){
            return null;
        }else if(json.trim().matches("^(-?\\d+)(\\.\\d+)?$")){
           try {
               Long val = Long.parseLong(json.trim());
               if(val.byteValue()==val){
                   return val.byteValue();
               }else if(val.shortValue()==val){
                   return val.shortValue();
               }else if(val.intValue()==val){
                   return val.intValue();
               }else{
                   return val;
               }
           }catch (NumberFormatException e){
               return Double.parseDouble(json.trim());
           }
        }else if(!json.trim().startsWith("{") &&  !json.trim().startsWith("[")){
            if("null".equalsIgnoreCase(json.trim().toString())){
                return null;
            }else if("false".equalsIgnoreCase(json.trim().toString())){
                return Boolean.FALSE;
            }else if("true".equalsIgnoreCase(json.trim().toString())){
                return Boolean.TRUE;
            }else{
                return json.trim();
            }
        }else{
            return nextValue(json);
        }
    }

    private static Object nextValue(String json){
        try{
            StringBuffer sb = new StringBuffer();
            while (true){
                char c = nextChar(json);
                switch (c){
                    case '{':
                        JSONObject jsonObject = new JSONObject();
                        if (nextChar(json)=='}'){
                            return jsonObject;
                        }
                        ptr--;
                        String key = (String) nextValue(json);
                        if(nextChar(json)==':') {
                            jsonObject.put(key,nextValue(json));
                        }else{
                            throw new RuntimeException("jsonObject format error at position: "+ptr);
                        }
                        while (nextChar(json)==','){
                            key = (String) nextValue(json);
                            if(nextChar(json)==':') {
                                jsonObject.put(key,nextValue(json));
                            }else{
                                throw new RuntimeException("jsonObject format error at position: "+ptr);
                            }
                        }
                        ptr--;
                        if (nextChar(json)=='}'){
                            return jsonObject;
                        }else{
                            throw new RuntimeException("jsonObject format error at position: "+ptr);
                        }
                    case '[':
                        JSONArray jsonArray =new JSONArray();
                        if(nextChar(json)==']'){
                            return jsonArray;
                        }
                        ptr--;
                        jsonArray.add(nextValue(json));
                        while (nextChar(json)==','){
                            jsonArray.add(nextValue(json));
                        }
                        ptr--;
                        if(nextChar(json)==']'){
                            return jsonArray;
                        }else{
                            throw new RuntimeException("jsonArray format error at position: "+ptr);
                        }
                    case ',':
                        ptr--;
                        if("null".equalsIgnoreCase(sb.toString())){
                            return null;
                        }else if("false".equalsIgnoreCase(sb.toString())){
                            return Boolean.FALSE;
                        }else if("true".equalsIgnoreCase(sb.toString())){
                            return Boolean.TRUE;
                        }else{
                            try {
                                Long val = Long.parseLong(sb.toString());
                                if(val.byteValue()==val){
                                    return val.byteValue();
                                }else if(val.shortValue()==val){
                                    return val.shortValue();
                                }else if (val.intValue()==val){
                                    return val.intValue();
                                } else {
                                    return val;
                                }
                            } catch (Exception e){
                                return Double.parseDouble(sb.toString());
                            }
                        }
                    case '"':
                    case '\'':
                        char ch=nextChar(json);
                        while (ch!='"' && ch!='\''){
                            if (ch=='\\'){
                                switch (nextChar(json)){
                                    case 'b':
                                        sb.append('\b');
                                        break;
                                    case 't':
                                        sb.append('\t');
                                        break;
                                    case 'n':
                                        sb.append('\n');
                                        break;
                                    case 'r':
                                        sb.append('\r');
                                        break;
                                    case '\'':
                                        sb.append('\'');
                                        break;
                                    case '"':
                                        sb.append('\"');
                                        break;
                                    case 'u':
                                        int num = 0;
                                        for (int i = 3; i >= 0; --i) {
                                            int tmp = nextChar(json);
                                            if (tmp <= '9' && tmp >= '0')
                                                tmp = tmp - '0';
                                            else if (tmp <= 'F' && tmp >= 'A')
                                                tmp = tmp - ('A' - 10);
                                            else if (tmp <= 'f' && tmp >= 'a')
                                                tmp = tmp - ('a' - 10);
                                            else
                                                throw new RuntimeException("Illegal hex code");
                                            num += tmp << (i * 4);
                                        }
                                        sb.append((char) num);
                                        break;
                                }
                            }else {
                                sb.append(ch);
                            }
                            ch = nextChar(json);
                        }
                        return sb.toString();
                    default:
                        if (c=='}'||c==']'){
                            ptr--;
                            return sb.toString();
                        }else{
                            sb.append(c);
                        }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException("json format error at position: "+ptr);
    }

    private static char nextChar(String json) throws ArrayIndexOutOfBoundsException{
        while (json.charAt(ptr)==' ')
            ptr++;
        return json.charAt(ptr++);
    }

}
