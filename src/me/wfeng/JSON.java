package me.wfeng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wfeng on 2018/01/14.
 */
public class JSON {

    private static int position;

    private static Map<String, Object> NULL_OR_BOOLEAN = new HashMap() {
        {
            put("null", null);
            put("false", false);
            put("true", true);
        }
    };

    public static Object parse(String json) {
        position = 0;
        if (json == null || "".equals(json.trim())) {
            throw new RuntimeException("null or empty string.");
        } else if (json.trim().matches("^(-?\\d+)(\\.\\d+)?$")) {
            return parseNumber(json.trim());
        } else if ("{[\"'".indexOf(json.trim().charAt(0))==-1) {
            json = json.trim().toLowerCase();
            if (NULL_OR_BOOLEAN.containsKey(json)) {
                return NULL_OR_BOOLEAN.get(json);
            } else  {
                throw new RuntimeException("json formot error, value is illegal");
            }
        } else {
            return nextValue(json);
        }
    }

    private static Object nextValue(String json) {
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = nextChar(json);
                switch (c) {
                    case '{':
                        JSONObject jsonObject = new JSONObject();
                        if (nextChar(json) == '}') {
                            return jsonObject;
                        }
                        position--;
                        do {
                            Object key = nextValue(json);
                            if (nextChar(json) == ':') {
                                jsonObject.put(key, nextValue(json));
                            } else {
                                throw new RuntimeException("jsonObject format error at position: " + position);
                            }
                        }while (nextChar(json) == ',');
                        position--;
                        if (nextChar(json) == '}') {
                            return jsonObject;
                        } else {
                            throw new RuntimeException("jsonObject format error at position: " + position);
                        }
                    case '[':
                        JSONArray jsonArray = new JSONArray();
                        if (nextChar(json) == ']') {
                            return jsonArray;
                        }
                        position--;
                        do  {
                            jsonArray.add(nextValue(json));
                        }while (nextChar(json) == ',');
                        position--;
                        if (nextChar(json) == ']') {
                            return jsonArray;
                        } else {
                            throw new RuntimeException("jsonArray format error at position: " + position);
                        }
                    case '"':
                    case '\'':
                        char ch = nextChar(json);
                        while (ch != '"' && ch != '\'') {
                            if (ch == '\\') {
                                switch (nextChar(json)) {
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
                                    case '\\':
                                        sb.append('\\');
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
                            } else {
                                sb.append(ch);
                            }
                            ch = nextChar(json);
                        }
                        return sb.toString();
                    default:
                        if (c == '}' || c == ']' || c== ',') {
                            position--;
                            return parseFundamentalType(sb.toString());
                        } else {
                            sb.append(c);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("json format error at position: " + position);
    }

    private static Object parseFundamentalType(String str) {
        if (NULL_OR_BOOLEAN.containsKey(str.toLowerCase())) {
            return NULL_OR_BOOLEAN.get(str.toLowerCase());
        } else {
            return parseNumber(str);
        }
    }

    private static Object parseNumber(String s) {
        try {
            Long val = Long.parseLong(s);
            if (val.byteValue() == val) {
                return val.byteValue();
            } else if (val.shortValue() == val) {
                return val.shortValue();
            } else if (val.intValue() == val) {
                return val.intValue();
            } else {
                return val;
            }
        } catch (Exception e) {
            return Double.parseDouble(s);
        }
    }

    private static char nextChar(String json) throws ArrayIndexOutOfBoundsException {
        while (json.charAt(position) == ' ')
            position++;
        return json.charAt(position++);
    }

}
