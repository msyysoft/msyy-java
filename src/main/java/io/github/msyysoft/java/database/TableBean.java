package io.github.msyysoft.java.database;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TableBean {
    protected transient Map<String, Object> beanMap = new HashMap<String, Object>();

    protected transient Map<String, Object> sqlConditionsMap = new HashMap<String, Object>();

    protected transient Map<String, String> sortConditionsMap = new LinkedHashMap<String, String>();

    protected transient Map<String, Object> sqlUpdateColumnsMap = new HashMap<String, Object>();

    protected transient Map<String, Object> sqlUpdateColumnAliasMap = new HashMap<String, Object>();

    protected void setSqlUpdateColumnAliasMap(Map<String, Object> sqlUpdateColumnAliasMap) {
        this.sqlUpdateColumnAliasMap = sqlUpdateColumnAliasMap;
    }

    public void clearAllConditions() {
        sqlConditionsMap.clear();
        sortConditionsMap.clear();
        sqlUpdateColumnsMap.clear();
        sqlUpdateColumnAliasMap.clear();
    }

    /**
     * get table name, mapping database table
     *
     * @return
     */
    protected abstract String getSingleTableName();

    /**
     * whether exist primary key
     *
     * @return
     */
    protected abstract boolean hasPrimaryKey();

    /**
     * get table column names array
     *
     * @return
     */
    protected abstract String[] getColumnNameArr();

    /**
     * put bean content into bean-map
     *
     * @return
     */
    protected abstract void putInBeanMap();

    /**
     * get update all sql
     *
     * @return
     */
    protected abstract String getSqlUpdateAll();

    /**
     * get select all sql, exclude where condition
     *
     * @return
     */
    protected abstract String getSqlSelectAll();

    /**
     * get insert batch sql
     *
     * @return
     */
    protected abstract String getSqlInsertBatch();
}
