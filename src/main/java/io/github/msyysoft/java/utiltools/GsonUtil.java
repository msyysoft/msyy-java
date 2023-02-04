package io.github.msyysoft.java.utiltools;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GsonUtil {

    /**
     * 根据传入的json数组，返回指定包含类型的集合
     *
     * @param jsonStr [{id:1,name:gx},{id:2,name:wxm}]
     * @param clazz
     * @return
     */
    public static <T> List<T> jsonArrayToList(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.trim().equals("")) {
            return null;
        }
        List<T> resultList = new ArrayList<T>();
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonStr); // 将json字符串转换成JsonElement
        JsonArray jsonArray = jsonElement.getAsJsonArray(); // 将JsonElement转换成JsonArray
        Iterator<JsonElement> it = jsonArray.iterator(); // Iterator处理
        while (it.hasNext()) { // 循环
            jsonElement = (JsonElement) it.next(); // 提取JsonElement
            String menu = jsonElement.toString(); // JsonElement转换成String
            T bean = gson.fromJson(menu, clazz); // String转化成JavaBean
            resultList.add(bean); // 加入List
        }
        return resultList;
    }

    /**
     * 将传入的对象转为json字符串，支持不固定参数 使用示例：allToJson(1,2,3)，allToJson()，
     * allToJson(null)， allToJson(new TreeBean(), "aaa")
     *
     * @return json字符串
     * @author Allen
     */
    public static String allToJson(Object... objArray) {
        Gson gson = new Gson();
        if (objArray != null) {
            return gson.toJson(objArray);
        }
        return gson.toJson(new Object[0]);
    }

    /**
     * 判断json是否是有效的json字符串
     *
     * @param jsonReader
     * @return
     */
    public static boolean isJsonValid(JsonReader jsonReader) {
        try {
            JsonToken token;
            loop:
            while ((token = jsonReader.peek()) != JsonToken.END_DOCUMENT && token != null) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        break;
                    case END_ARRAY:
                        jsonReader.endArray();
                        break;
                    case BEGIN_OBJECT:
                        jsonReader.beginObject();
                        break;
                    case END_OBJECT:
                        jsonReader.endObject();
                        break;
                    case NAME:
                        jsonReader.nextName();
                        break;
                    case STRING:
                    case NUMBER:
                    case BOOLEAN:
                    case NULL:
                        jsonReader.skipValue();
                        break;
                    case END_DOCUMENT:
                        break loop;
                    default:
                        throw new AssertionError(token);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断json是否是有效的json字符串
     *
     * @param json
     * @return
     */
    public static boolean isJsonValid(String json) {
        return isJsonValid(new JsonReader(new StringReader(json)));
    }

    public static final TypeAdapter<Date> DATE = new TypeAdapter<Date>() {
        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new Date(new SimpleDateFormat(DateUtil.DATE_FORMAT).parse(in.nextString()).getTime());
            } catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(new SimpleDateFormat(DateUtil.DATE_FORMAT).format(value));
            }
        }
    };

    public static final TypeAdapter<java.sql.Time> TIME = new TypeAdapter<java.sql.Time>() {
        @Override
        public java.sql.Time read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new java.sql.Time(new SimpleDateFormat(DateUtil.TIME_FORMAT).parse(in.nextString()).getTime());
            } catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, java.sql.Time value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(new SimpleDateFormat(DateUtil.TIME_FORMAT).format(value));
            }
        }
    };

    public static final TypeAdapter<java.sql.Timestamp> DATETIME = new TypeAdapter<java.sql.Timestamp>() {
        @Override
        public java.sql.Timestamp read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new java.sql.Timestamp(new SimpleDateFormat(DateUtil.DATETIME_FORMAT).parse(in.nextString()).getTime());
            } catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, java.sql.Timestamp value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(new SimpleDateFormat(DateUtil.DATETIME_FORMAT).format(value));
            }
        }
    };

    /**
     * gson下划线转驼峰
     */
    public enum FieldNamingPolicy implements FieldNamingStrategy {
        UNDERSCORES_UPPER_CAMEL_CASE {
            public String translateName(Field f) {
                return StringUtil.lineToHump(f.getName());
            }
        };

        private FieldNamingPolicy() {
        }
    }

    private static Gson gson;

    public static GsonBuilder getGsonBuilder() {
        GsonBuilder gb = new Gson().newBuilder();
        gb.setDateFormat(DateUtil.DATETIME_FORMAT);
        gb.enableComplexMapKeySerialization();
        //gb.setPrettyPrinting();
        return gb;
    }

    public static Gson getGson(GsonBuilder gb) {
        if (gson == null) {
            gson = gb.create();
        }
        return gson;
    }

    public static Gson getGson() {
        return getGson(getGsonBuilder());
    }

    /**
     * 使用自定义gson格式化返回的json
     */
    public static class GsonHttpMessageConverter extends org.springframework.http.converter.json.GsonHttpMessageConverter {
        public GsonHttpMessageConverter() {
            super();
            setGson(GsonUtil.getGson());
        }
    }

}
