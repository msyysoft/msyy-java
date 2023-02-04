package io.github.msyysoft.java.database;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 常用方法集合
 */
public class SqlHelper {
    /**
     * 模糊匹配时将传入的参数的特殊字符(%\_)进行转义，并将前后增加通配符%
     *
     * @param sqlParam
     * @return
     */
    public static String getSqlLikeParam(String sqlParam) {
        sqlParam = sqlParam.replace("\\", "\\\\");
        sqlParam = sqlParam.replace("%", "\\%");
        sqlParam = sqlParam.replace("_", "\\_");
        return "%" + sqlParam + "%";
    }

    /**
     * 使用in查询时将传入的参数进行按(,)或指定字符分离并返回list
     *
     * @param sqlParam
     * @return
     */
    public static List<String> getSqlInParam(String sqlParam) {
        return getSqlInParam(sqlParam, null);
    }

    /**
     * 使用in查询时将传入的参数进行按(,)或指定字符分离并返回list
     *
     * @param sqlParam
     * @return
     */
    public static List<String> getSqlInParam(String sqlParam, String regex) {
        List<String> retParamList = new ArrayList<>();
        if (!StringUtils.isEmpty(sqlParam)) {
            List<String> list = Arrays.asList(sqlParam.split(StringUtils.isEmpty(regex) ? "," : regex));
            for (String p : list) {
                if (!StringUtils.isEmpty(p)) {
                    retParamList.add(p);
                }
            }
        }
        return retParamList;
    }

    /**
     * 单表主键查询时传入的带主键的map
     *
     * @param id
     * @return
     */
    public static Map<String, Object> getIDMap(Object id) {
        return getIDMap("id", id);
    }

    /**
     * 单表主键查询时传入的带主键的map
     *
     * @param id
     * @return
     */
    public static Map<String, Object> getIDMap(String key, Object id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(key, id);
        return paramMap;
    }

}
