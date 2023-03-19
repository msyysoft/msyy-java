package io.github.msyysoft.java.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleTablePersistUtils {

    private static Logger log = LoggerFactory.getLogger(SingleTablePersistUtils.class);
    public static final String EMPTY = "";
    private static final String COLUMN_PERFIX = "alias_";

    /**
     * insert method
     *
     * @param dao
     * @param bean
     * @return
     */
    public static <T extends TableBean> int addNewRecord(AbstractDao dao, T bean) {
        bean.putInBeanMap();
        dealBeanMapForInsert(bean);
        String sql = getSqlInsert(bean.getSingleTableName(), bean.beanMap);
        log.debug("single-table inset sql >>> " + sql);
        return dao.getNamedParameterJdbcTemplate().update(sql, bean.beanMap);
    }

    /**
     * insert batch method, non-support default value
     *
     * @param dao
     * @param beanList
     * @return
     */
    public static <T extends TableBean> int[] addNewRecordBatch(AbstractDao dao, List<T> beanList) {
        if (!CollectionUtils.isEmpty(beanList)) {
            SqlParameterSource[] batchArgs = new SqlParameterSource[beanList.size()];
            int i = 0;
            for (T bean : beanList) {
                bean.putInBeanMap();
                batchArgs[i] = new MapSqlParameterSource(bean.beanMap);
                i++;
            }
            String sql = beanList.get(0).getSqlInsertBatch();
            log.debug("single-table inset batch sql >>> " + sql);
            return dao.getNamedParameterJdbcTemplate().batchUpdate(sql, batchArgs);
        }
        return null;
    }

    /**
     * update record data by primary key, update all column
     *
     * @param dao
     * @param bean
     * @return
     */
    public static <T extends TableBean> int updateRecordAllByPrimaryKey(AbstractDao dao, T bean) {
        bean.putInBeanMap();
        String sql = bean.getSqlUpdateAll();
        log.debug("single-table update all sql >>> " + sql);
        if (bean.hasPrimaryKey())
            return dao.getNamedParameterJdbcTemplate().update(sql, bean.beanMap);
        return 0;
    }

    /**
     * update record data by sql condition, need set update column enum
     *
     * @param dao
     * @param bean
     * @return
     */
    public static <T extends TableBean> int updateRecordColumnsBySqlConditions(AbstractDao dao, T bean) {
        bean.putInBeanMap();
        String sql = getSqlUpdate(bean.getSingleTableName(), bean.sqlUpdateColumnsMap, bean.sqlConditionsMap, bean);
        log.debug("single-table update column sql >>> " + sql);
        return dao.getNamedParameterJdbcTemplate().update(sql, bean.sqlUpdateColumnAliasMap);
    }

    /**
     * delete record by sql conditions, throw exception if sql conditions empty
     *
     * @param dao
     * @param bean
     * @return delete record number
     */
    public static <T extends TableBean> int deleteRecordBySqlConditions(AbstractDao dao, T bean) {
        bean.putInBeanMap();
        String sql = getSqlDelete(bean.getSingleTableName(), bean.sqlConditionsMap);
        log.debug("single-table delete sql >>> " + sql);
        return dao.getNamedParameterJdbcTemplate().update(sql, bean.sqlConditionsMap);
    }

    /**
     * clear table
     *
     * @param dao
     * @param bean
     * @return
     */
    public static <T extends TableBean> int clearTable(AbstractDao dao, T bean) {
        String sql = getSqlClearTable(bean.getSingleTableName());
        log.debug("single-table clear table sql >>> " + sql);
        return dao.getNamedParameterJdbcTemplate().update(sql, new HashMap<String, Object>());
    }

    /**
     * get single record bean by parameter
     *
     * @param dao
     * @param bean
     * @param paramMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TableBean> T getRecordByPKMap(AbstractDao dao, T bean, Map<String, Object> paramMap) {
        String sql = getSqlSelect(bean.getSqlSelectAll(), paramMap, bean.sortConditionsMap, null, null);
        log.debug("single-table select by pk sql >>> " + sql);
        return dao.getNamedParameterJdbcTemplate().queryForObject(sql, paramMap, new BeanPropertyRowMapper<T>((Class<T>) bean.getClass()));
    }

    /**
     * get record list by sql conditions
     *
     * @param dao
     * @param bean
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TableBean> List<T> getRecordListBySqlConditions(AbstractDao dao, T bean) {
        bean.putInBeanMap();
        String sql = getSqlSelect(bean.getSqlSelectAll(), bean.sqlConditionsMap, bean.sortConditionsMap, bean.pageConditionNumber, bean.pageConditionSize);
        log.debug("single-table select sql >>> " + sql);
        return dao.getNamedParameterJdbcTemplate().query(sql, bean.sqlConditionsMap, new BeanPropertyRowMapper<T>((Class<T>) bean.getClass()));
    }

    /**
     * get record number by sql conditions
     *
     * @param dao
     * @param bean
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TableBean> int recordNumberBySqlConditions(AbstractDao dao, T bean) {
        bean.putInBeanMap();
        String sql = getSqlCount(bean.getSqlSelectAll(), bean.sqlConditionsMap);

        log.debug("single-table count sql >>> " + sql);
        List<Map<String, Object>> list = dao.getNamedParameterJdbcTemplate().queryForList(sql, bean.sqlConditionsMap);
        return CollectionUtils.isEmpty(list) ? 0 : Integer.parseInt(((Map) list.get(0)).get("num").toString());
    }

    /**
     * insert data, remove default column and pk's value, if it is null
     *
     * @param bean
     */
    private static <T extends TableBean> void dealBeanMapForInsert(T bean) {
        for (String colName : bean.getColumnNameArr()) {
            if (bean.beanMap.get(colName) == null)
                bean.beanMap.remove(colName);
        }
    }

    /**
     * get insert sql string, if beanMap is empty, return ""
     *
     * @param table_name
     * @param beanMap
     * @return
     */
    private static String getSqlInsert(String table_name, Map<String, Object> beanMap) {
        if (beanMap != null && !beanMap.isEmpty()) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert into ").append(table_name).append(" (");
            final StringBuilder namedBuf = new StringBuilder(beanMap.size() * 16);
            final StringBuilder templateBuf = new StringBuilder(beanMap.size() * 16);
            String[] columns = beanMap.keySet().toArray(new String[0]);
            for (int i = 0, l = columns.length; i < l; i++) {
                if (i > 0) {
                    namedBuf.append(", ");
                    templateBuf.append(", ");
                }
                namedBuf.append(columns[i]);
                templateBuf.append(":").append(columns[i]);
            }
            sql.append(namedBuf).append(") values (").append(templateBuf).append(")");
            return sql.toString();
        }
        return EMPTY;
    }

    /**
     * get update columns sql, if sqlUpdateColumnsMap or sqlConditionsMap is
     * empty, return ""
     *
     * @param table_name
     * @param sqlUpdateColumnsMap
     * @param sqlConditionsMap
     * @param bean
     * @return
     */
    private static <T extends TableBean> String getSqlUpdate(String table_name, Map<String, Object> sqlUpdateColumnsMap, Map<String, Object> sqlConditionsMap, T bean) {
        if (sqlUpdateColumnsMap != null && !sqlUpdateColumnsMap.isEmpty() && sqlConditionsMap != null && !sqlConditionsMap.isEmpty()) {
            StringBuffer sql = new StringBuffer();
            String[] updatecolumns = sqlUpdateColumnsMap.keySet().toArray(new String[0]);
            sql.append("update ").append(table_name).append(" set ");
            bean.sqlUpdateColumnAliasMap.clear();
            for (int i = 0, l = updatecolumns.length; i < l; i++) {
                String column = updatecolumns[i];
                sql.append(column).append(" = :").append(column);
                if (i < l - 1)
                    sql.append(", ");
            }
            bean.sqlUpdateColumnAliasMap.putAll(sqlUpdateColumnsMap);
            sql.append(" where ");
            String[] columns = sqlConditionsMap.keySet().toArray(new String[0]);
            for (int i = 0, l = columns.length; i < l; i++) {
                if (i > 0)
                    sql.append(" and ");
                String column = columns[i];
                if (sqlConditionsMap.get(column) == null)
                    sql.append(column).append(" is null ");
                else
                    sql.append(column).append(" = :").append(COLUMN_PERFIX).append(column).append(" ");
                bean.sqlUpdateColumnAliasMap.put(COLUMN_PERFIX + column, sqlConditionsMap.get(column));
            }
            return sql.toString();
        }
        return EMPTY;
    }

    /**
     * get delete sql string, if sqlConditionsMap is empty, return ""
     *
     * @param table_name
     * @param sqlConditionsMap
     * @return
     */
    private static String getSqlDelete(String table_name, Map<String, Object> sqlConditionsMap) {
        if (sqlConditionsMap != null && !sqlConditionsMap.isEmpty()) {
            StringBuffer sql = new StringBuffer();
            sql.append("delete from ").append(table_name).append(" where ");
            String[] columns = sqlConditionsMap.keySet().toArray(new String[0]);
            for (int i = 0, l = columns.length; i < l; i++) {
                if (i > 0)
                    sql.append(" and ");
                String column = columns[i];
                Object obj = sqlConditionsMap.get(column);
                if (obj == null)
                    sql.append(column).append(" is null ");
                else {
                    if (obj instanceof SqlConditionValue) {
                        SqlConditionValue scv = (SqlConditionValue) obj;
                        sql.append(column).append(" " + scv.getOperation() + " ");
                        if (scv.getOperation().contains("in")) {
                            sql.append(" (:").append(column).append(") ");
                        } else {
                            sql.append(" :").append(column).append(" ");
                        }
                        sqlConditionsMap.put(column, scv.getValue());
                    } else {
                        sql.append(column).append(" = :").append(column).append(" ");
                    }
                }
            }
            return sql.toString();
        }
        return EMPTY;
    }

    /**
     * get clear table sql string
     *
     * @param table_name
     * @return
     */
    private static String getSqlClearTable(String table_name) {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ").append(table_name);
        return sql.toString();
    }

    /**
     * get select sql string
     *
     * @param selectAllSql
     * @param sqlConditionsMap
     * @return
     */
    private static String getSqlSelect(String selectAllSql, Map<String, Object> sqlConditionsMap, Map<String, String> sortConditionsMap, Integer pageConditionNumber, Integer pageConditionSize) {
        StringBuffer sql = new StringBuffer(selectAllSql);
        if (sqlConditionsMap != null && !sqlConditionsMap.isEmpty()) {
            sql.append(" where ");
            String[] columns = sqlConditionsMap.keySet().toArray(new String[0]);
            for (int i = 0, l = columns.length; i < l; i++) {
                if (i > 0)
                    sql.append(" and ");
                String column = columns[i];
                Object obj = sqlConditionsMap.get(column);
                if (obj == null)
                    sql.append(column).append(" is null ");
                else {
                    if (obj instanceof SqlConditionValue) {
                        SqlConditionValue scv = (SqlConditionValue) obj;
                        sql.append(column).append(" " + scv.getOperation() + " ");
                        if (scv.getOperation().contains("in")) {
                            sql.append(" (:").append(column).append(") ");
                        } else {
                            sql.append(" :").append(column).append(" ");
                        }
                        sqlConditionsMap.put(column, scv.getValue());
                    } else {
                        sql.append(column).append(" = :").append(column).append(" ");
                    }
                }
            }
        }
        if (sortConditionsMap != null && !sortConditionsMap.isEmpty()) {
            sql.append(" order by ");
            for (Map.Entry<String, String> sorter : sortConditionsMap.entrySet()) {
                sql.append(" " + sorter.getKey() + " " + sorter.getValue());
            }
        }
        if (pageConditionNumber != null && pageConditionSize != null) {
            int offset = (pageConditionNumber - 1) * pageConditionSize;
            offset = Math.max(offset, 0);
            pageConditionSize = pageConditionSize > 0 ? pageConditionSize : 15;
            sql.append(" limit ").append(pageConditionSize).append(" offset ").append(offset);
        }
        return sql.toString();
    }

    /**
     * get count sql string
     *
     * @param selectAllSql
     * @param sqlConditionsMap
     * @return
     */
    private static String getSqlCount(String selectAllSql, Map<String, Object> sqlConditionsMap) {
        StringBuffer sql = new StringBuffer(selectAllSql.replace("*", "count(1) as num"));
        if (sqlConditionsMap != null && !sqlConditionsMap.isEmpty()) {
            sql.append(" where ");
            String[] columns = sqlConditionsMap.keySet().toArray(new String[0]);
            for (int i = 0, l = columns.length; i < l; i++) {
                if (i > 0)
                    sql.append(" and ");
                String column = columns[i];
                Object obj = sqlConditionsMap.get(column);
                if (obj == null)
                    sql.append(column).append(" is null ");
                else {
                    if (obj instanceof SqlConditionValue) {
                        SqlConditionValue scv = (SqlConditionValue) obj;
                        sql.append(column).append(" " + scv.getOperation() + " ");
                        if (scv.getOperation().contains("in")) {
                            sql.append(" (:").append(column).append(") ");
                        } else {
                            sql.append(" :").append(column).append(" ");
                        }
                        sqlConditionsMap.put(column, scv.getValue());
                    } else {
                        sql.append(column).append(" = :").append(column).append(" ");
                    }
                }
            }
        }
        return sql.toString();
    }
}
