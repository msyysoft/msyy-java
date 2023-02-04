package io.github.msyysoft.java.utiltools;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 集合工具
 */
public class CollectionUtil {
    /**
     * 获取List中指定属性的值为propertyVal的列表
     *
     * @param propertyName
     * @param propertyVal
     */
    public static List<Map<String, Object>> getSamePropertyList(List<Map<String, Object>> list, String propertyName, String propertyVal) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : list) {
            String val = map.get(propertyName).toString();
            if (val == null) {
                continue;
            }
            if (val.equals(propertyVal)) {
                resultList.add(map);
            }
        }
        if (resultList.size() == 0) {
            return null;
        }
        return resultList;
    }

    /**
     * 根据传入的属性名称、遍历list返回属性值与propertyVal相等的记录
     *
     * @param list
     * @param propertyName
     * @param propertyVal
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> getSamePropertyListBean(Collection<T> list, String propertyName, Object propertyVal) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<T> resultList = new ArrayList<T>();
        for (T bean : list) {
            Object val = null;
            try {
                if (bean instanceof java.util.Map) {
                    val = ((java.util.Map) bean).get(propertyName);
                } else {
                    PropertyDescriptor p = new PropertyDescriptor(propertyName,
                            bean.getClass());
                    val = p.getReadMethod().invoke(bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (val == null) {
                continue;
            }
            if (val.equals(propertyVal)) {
                resultList.add(bean);
            }
        }
        if (resultList.size() == 0) {
            return null;
        }
        return resultList;
    }
}
