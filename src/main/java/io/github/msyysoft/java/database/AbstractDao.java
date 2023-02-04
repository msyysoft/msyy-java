package io.github.msyysoft.java.database;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: 数据库操作通用DAO基类<br>
 * Description: 操作数据库的各个业务DAO需要从该抽象DAO继承。开发人员编写DAO强制从该类继承。<br>
 * Date: 2018-01-04<br>
 * Copyright (c) 2018 dyfc <br>
 */
public abstract class AbstractDao {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取Spring中的NamedParameterJdbcTemplate对象，通过该对象可以进行JDBC对数据库的操作
     *
     * @return NamedParameterJdbcTemplate Spring框架中用来进行JDBC操作的模板工具
     */
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        if (namedParameterJdbcTemplate == null)
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        return namedParameterJdbcTemplate;
    }

    /**
     * 获取Spring中的JdbcTemplate对象，通过该对象可以进行JDBC对数据库的操作
     *
     * @return JdbcTemplate Spring框架中用来进行JDBC操作的模板工具
     */
    public JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null)
            jdbcTemplate = new JdbcTemplate(getDataSource());
        return jdbcTemplate;
    }

    /**
     * 子类在继承抽象DAO之后，要实现该抽象方法，返回当前开发人员所编写的DAO类要使用哪个数据源
     **/
    public abstract DataSource getDataSource();

    /**
     * 执行没有参数的SQL查询语句
     * 根据配置文件中配置的SQL语句的对应的key值，进行查询。并将查询的每行结果按字段为key，数据为value封装到Map。
     * 并将每行的Map装入List 可以在DAO中，将查询结果封装到Java对象。
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @return List 将结果集每行数据集按列名为key，数据位value装入Map，并将每行数据装入List
     **/
    public List<Map<String, Object>> queryForListByTemplate(String sqlKey) {
        return queryForListByTemplate(sqlKey, new HashMap<String, Object>());
    }

    /**
     * 执行带有参数绑定的SQL查询语句 将需要绑定到SQL语句中名称占位符位置的数据，按名称占位符为key，数据为value的方法装入Map
     * 根据配置文件中配置的SQL语句的对应的key值，进行查询。并将查询的每行结果按字段为key，数据为value封装到Map。并将每行的Map装入List
     * 可以在DAO中，将查询结果封装到Java对象。
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param paramMap，将需要绑定到SQL语句中名称占位符的数据按名称占位符为key，数据位value的方法装入Map
     * @return List 将结果集每行数据集按列名为key，数据位value装入Map，并将每行数据装入List
     **/
    public List<Map<String, Object>> queryForListByTemplate(String sqlKey, Map<String, Object> paramMap) {
        String sql = SqlTemplateUtil.getInstance().getSqlString(sqlKey, paramMap);
        return getNamedParameterJdbcTemplate().queryForList(sql, paramMap);
    }

    /**
     * 执行带没有参数绑定的SQL分页查询语句
     * 根据配置文件中配置的SQL语句的对应的key值，进行查询。并将查询的每行结果按字段为key，数据为value封装到Map。
     * 并将每行的Map装入List 可以在DAO中，将查询结果封装到Java对象。
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param pagesize，每一页显示多少天数据
     * @param pagenum，要查询哪一页的数据
     * @return List 将结果集每行数据集按列名为key，数据位value装入Map，并将每行数据装入List
     **/
    public List<Map<String, Object>> pagingForListByTemplate(String sqlKey, int pagesize, int pagenum) {
        return pagingForListByTemplate(sqlKey, pagesize, pagenum, new HashMap<String, Object>());
    }

    /**
     * 执行带有参数绑定的SQL分页查询语句 将需要绑定到SQL语句中名称占位符位置的数据，按名称占位符为key，数据为value的方法装入Map
     * 根据配置文件中配置的SQL语句的对应的key值进行查询。并将查询的每行结果按字段为key，数据为value封装到Map。
     * 并将每行的Map装入List可以在DAO中，将查询结果封装到Java对象。
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param pagesize，每一页显示多少天数据
     * @param pagenum，要查询哪一页的数据
     * @param paramMap，将需要绑定到SQL语句中名称占位符的数据按名称占位符为key，数据位value的方法装入Map
     * @return List 将结果集每行数据集按列名为key，数据为value装入Map，并将每行数据装入List
     **/
    public List<Map<String, Object>> pagingForListByTemplate(String sqlKey, int pagesize, int pagenum, Map<String, Object> paramMap) {
        String sql = SqlTemplateUtil.getInstance().getPageSqlString(sqlKey, pagesize, pagenum, paramMap);
        return getNamedParameterJdbcTemplate().queryForList(sql, paramMap);
    }

    /**
     * 返回SQL查询语句查询结果集数据条数
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @return int 返回SQL查询语句查询结果集数据条数
     **/
    public int recordNumberForListByTemplate(String sqlKey) {
        return recordNumberForListByTemplate(sqlKey, new HashMap<String, Object>());
    }

    /**
     * 返回SQL查询语句查询结果集数据条数，SQL语句中含有名称占位符需要绑定数据
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param paramMap，将需要绑定到SQL语句中名称占位符的数据按名称占位符为key，数据位value的方法装入Map
     * @return int 返回SQL查询语句查询结果集数据条数
     **/
    public int recordNumberForListByTemplate(String sqlKey, Map<String, Object> paramMap) {
        String sql = SqlTemplateUtil.getInstance().getRecordNumberSqlString(sqlKey, paramMap);
        List<Map<String, Object>> list = getNamedParameterJdbcTemplate().queryForList(sql, paramMap);
        if (CollectionUtils.isEmpty(list))
            return 0;

        return Integer.parseInt(list.get(0).get("num").toString());
    }

    /**
     * 执行DML类型的SQL语句。
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param paramMap，将需要绑定到SQL语句中名称占位符的数据按名称占位符为key，数据位value的方法装入Map
     * @return int 返回当前DML类型SQL语句的执行影响了数据库中多少条数据
     **/
    public int updateByTemplate(String sqlKey, Map<String, Object> paramMap) {
        String sql = SqlTemplateUtil.getInstance().getSqlString(sqlKey, paramMap);
        return getNamedParameterJdbcTemplate().update(sql, new MapSqlParameterSource(paramMap));
    }

    /**
     * 执行批量的DML类型的SQL语句。并且SQL语句中含有Freemarker脚本
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param templMap，SQL语句中Freemarker中占位符需要替换的Map
     * @param batchValues，SQL语句会按照数组中的Map的个数执行多次。每一个Map都是用来进行一次名称占位符替换用的。
     * @return int[] 返回批量DML语句执行之后影响了数据库中的多少条数据
     **/
    public int[] batchUpdateByTemplate(String sqlKey, Map<String, Object> templMap, Map<String, Object>[] batchValues) {
        String sql = SqlTemplateUtil.getInstance().getSqlString(sqlKey, templMap);
        return getNamedParameterJdbcTemplate().batchUpdate(sql, batchValues);
    }

    /**
     * 执行批量的DML类型的SQL语句。并且SQL语句中含有Freemarker脚本
     *
     * @param sqlKey，SQL语句配置文件配置的SQL语句的索引key字符串
     * @param templMap，SQL语句中Freemarker中占位符需要替换的Map
     * @param batchValueList，SQL语句会按照List中的Map的个数执行多次。每一个Map都是用来进行一次名称占位符替换用的。
     * @return int[] 返回批量DML语句执行之后影响了数据库中的多少条数据
     **/
    public int[] batchUpdateByTemplate(String sqlKey, Map<String, Object> templMap,
                                       List<Map<String, ?>> batchValueList) {
        String sql = SqlTemplateUtil.getInstance().getSqlString(sqlKey, templMap);

        SqlParameterSource[] batchArgs = new SqlParameterSource[batchValueList.size()];
        int i = 0;
        for (Map<String, ?> batchValues : batchValueList) {
            batchArgs[i] = new MapSqlParameterSource(batchValues);
            i++;
        }
        return getNamedParameterJdbcTemplate().batchUpdate(sql, batchArgs);
    }

    /**
     * 判断数据库中的表是否存在
     *
     * @param tableName，表的名字
     * @return boolean 表是否存在
     **/
    public boolean isTableExists(String tableName) throws SQLException {
        Connection conn = null;
        try {
            conn = getJdbcTemplate().getDataSource().getConnection();
            ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"});
            return rs.next();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
        }
    }

    /**
     * 运行DDL类型的SQL语句
     *
     * @param ddlSql，需要执行的DDL类型的SQL语句
     **/
    public void runDDL(String ddlSql) {
        getJdbcTemplate().execute(ddlSql);
    }

    /**
     * 将查询结果封装为指定集合对象返回
     *
     * @param sqlKey
     * @param paramMap
     * @param clazz
     */
    public <T> List<T> qryObjList(String sqlKey, Map<String, Object> paramMap, Class<T> clazz) {
        String sql = SqlTemplateUtil.getInstance().getSqlString(sqlKey, paramMap);
        return getNamedParameterJdbcTemplate().query(sql, paramMap, new BeanPropertyRowMapper<T>(clazz));
    }

    /**
     * 将查询结果封装为指定集合对象返回
     *
     * @param sqlKey
     * @param paramMap
     * @param clazz
     */
    public <T> List<T> pagingObjList(String sqlKey, int pagesize, int pagenum, Map<String, Object> paramMap, Class<T> clazz) {
        String sql = SqlTemplateUtil.getInstance().getPageSqlString(sqlKey, pagesize, pagenum, paramMap);
        return getNamedParameterJdbcTemplate().query(sql, paramMap, new BeanPropertyRowMapper<T>(clazz));
    }

    /**
     * 将查询结果封装为指定对象返回
     *
     * @param sqlKey
     * @param paramMap
     * @param clazz
     */
    public <T> T qryObj(String sqlKey, Map<String, Object> paramMap, Class<T> clazz) {
        String sql = SqlTemplateUtil.getInstance().getSqlString(sqlKey, paramMap);
        return getNamedParameterJdbcTemplate().queryForObject(sql, paramMap, new BeanPropertyRowMapper<T>(clazz));
    }

}
