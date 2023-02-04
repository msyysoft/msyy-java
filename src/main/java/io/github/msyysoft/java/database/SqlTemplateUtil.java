package io.github.msyysoft.java.database;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Title: 用来处理类路径下sqltemplate中的SQL XML配置文件<br>
 * Description: 加载SQL配置文件，并解析。用来获取SQL的工具。程序员不需要使用，组件开发人员可以使用。<br>
 * Date: 2018-01-04<br>
 * Copyright (c) 2018 dyfc <br>
 */
public class SqlTemplateUtil {

    private static SqlTemplateUtil ourInstance;

    protected static final Logger logger = LoggerFactory.getLogger(SqlTemplateUtil.class);

    public static SqlTemplateUtil getInstance() {
        if (ourInstance == null) {
            ourInstance = new SqlTemplateUtil();
        }
        return ourInstance;
    }

    private SqlTemplateUtil() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sqlTemplateDir = "sqltemplate";
    private Logger log = LoggerFactory.getLogger(SqlTemplateUtil.class);
    private HashMap<String, String> sqlMap;
    private Configuration cfg = null;
    private StringTemplateLoader sTmpLoader = null;
    private List<String> repeatSqlkeyList = new ArrayList<String>();

    /**
     * 在未实例化之前可以设置 sqlTemplateDir
     *
     * @param sqlTemplateDir
     * @return
     */
    public boolean setSqlTemplateDir(String sqlTemplateDir) {
        if (ourInstance == null) {
            this.sqlTemplateDir = sqlTemplateDir;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 初始化方法，syscontext组件中的程序会调用该方法完成对SQL配置文件的记载。 调用该方法的时候static块被执行
     */
    private void init() throws IOException, DocumentException {
        sqlMap = new HashMap<String, String>();

        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(null).getResources("classpath:" + sqlTemplateDir + "/**/*.xml");
        for (Resource rc : resources) {
            realinit(rc);
        }

        if (repeatSqlkeyList.size() > 0) {
            for (String repeatSql : repeatSqlkeyList) {
                log.error("Repeat SQL Key: [ " + repeatSql + " ]");
            }
        } else {
            cfg = new Configuration();
            sTmpLoader = new StringTemplateLoader();
            for (String sqlKey : sqlMap.keySet()) {
                sTmpLoader.putTemplate(sqlKey, sqlMap.get(sqlKey));
                cfg.setTemplateLoader(sTmpLoader);
                cfg.setDefaultEncoding("UTF-8");
            }
        }
    }

    /**
     * 加载类路径sqltemplate文件夹中所有XML文件，并用jdom解析配置文件
     */
    private void realinit(Resource rc) throws IOException, DocumentException {
        Document doc = null;
        SAXReader read = new SAXReader();
        doc = read.read(rc.getInputStream());

        Element root = doc.getRootElement();
        String name, key;
        String nameSpace = root.attributeValue("namespace");
        if (StringUtils.isEmpty(nameSpace)) {
            nameSpace = "";
        } else {
            nameSpace = nameSpace + "@";
        }
        for (Iterator<Element> element = root.elementIterator(); element.hasNext(); ) {
            Element sql = element.next();
            if ("sqlElement".equals(sql.getName())) {
                key = nameSpace + sql.attribute("key").getValue();
                if (sqlMap.containsKey(key)) {
                    repeatSqlkeyList.add(key);
                } else {
                    sqlMap.put(key, sql.getText());
                }
            }
        }
    }

    public Set<String> getAllSqlKey() {
        return sqlMap.keySet();
    }

    /**
     * 根据SQL配置文件中的索引key值，取到配置文件中的SQL语句字符串
     *
     * @param sqlKey，SQL XML配置文件中的索引key值
     * @return String 返回索引key值对应的SQL语句字符串
     */
    public String getSqlString(String sqlKey) {
        return getSqlString(sqlKey, null);
    }

    /**
     * 根据SQL配置文件中的索引key值，取到配置文件中的SQL语句字符串 并将SQL语句中Freemarker中的名称占位符用map中的数据替换掉。
     *
     * @param sqlKey，SQL-XML配置文件中的索引key值
     * @param paramMap，Freemarker中名称占位符为key，需要替换的数据位value装入map，通过该Map进行替换
     * @return String 返回索引key值对应的SQL语句字符串，并替换SQL语句中Freemarker名称占位符
     */
    public String getSqlString(String sqlKey, Map<String, Object> paramMap) {
        String sql = "";
        if (paramMap == null) {
            sql = sqlMap.get(sqlKey);
        } else {
            try {
                Template template = cfg.getTemplate(sqlKey);
                StringWriter writer = new StringWriter();
                template.process(paramMap, writer);
                sql = writer.toString();
            } catch (Exception e) {
                log.error(e.getMessage());
                return "ERROR";
            }
        }

        log.debug(sqlKey + ":[ " + sql + " ]");
        return sql;
    }

    /**
     * 根据SQL配置文件中的索引key值，取到配置文件中的SQL语句的分页查询语句
     *
     * @param sqlKey，SQL-XML配置文件中的索引key值
     * @param pagesize，每一页显示数据的条数
     * @param pagenum，查询哪一页的数据
     * @return String 返回索引key值对应的SQL语句的分页查询语句
     */
    public String getPageSqlString(String sqlKey, int pagesize, int pagenum) {
        return getPageSqlString(sqlKey, pagesize, pagenum, null);
    }

    /**
     * 根据SQL配置文件中的索引key值，取到配置文件中的SQL语句的分页查询语句
     * 并将SQL语句中Freemarker中的名称占位符用map中的数据替换掉。
     *
     * @param sqlKey，SQL-XML配置文件中的索引key值
     * @param pagesize，每一页显示数据的条数
     * @param pagenum，查询哪一页的数据
     * @param paramMap，Freemarker中名称占位符为key，需要替换的数据位value装入map，通过该Map进行替换
     * @return String 返回索引key值对应的SQL语句的分页查询语句，并替换SQL语句中Freemarker名称占位符
     */
    public String getPageSqlString(String sqlKey, int pagesize, int pagenum, Map<String, Object> paramMap) {
        String sql = sqlMap.get(sqlKey);
        if (paramMap != null) {
            try {
                Template template = cfg.getTemplate(sqlKey);
                StringWriter writer = new StringWriter();
                template.process(paramMap, writer);
                sql = writer.toString();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        int offset = (pagenum - 1) * pagesize;
        offset = offset > 0 ? offset : 0;
        pagesize = pagesize > 0 ? pagesize : 15;
        sql = sql + " limit " + pagesize + " offset " + offset;

        log.debug(sqlKey + ":[ " + sql + " ]");
        return sql;
    }

    /**
     * 根据SQL配置文件中的索引key值，取到配置文件中的SQL语句的查询总条数的SQL语句
     *
     * @param sqlKey，SQL XML配置文件中的索引key值
     * @return String 返回索引key值对应的SQL语句的查询总条数SQL语句
     */
    public String getRecordNumberSqlString(String sqlKey) {
        return getRecordNumberSqlString(sqlKey, null);
    }

    /**
     * 根据SQL配置文件中的索引key值，取到配置文件中的SQL语句的查询总条数的SQL语句
     * 并将SQL语句中的Freemarker占位符用Map中的数据替换掉
     *
     * @param sqlKey，SQL-XML配置文件中的索引key值
     * @param paramMap，Freemarker中名称占位符为key，需要替换的数据位value装入map，通过该Map进行替换
     * @return String 返回索引key值对应的SQL语句的查询总条数SQL语句，并替换SQL语句中Freemarker名称占位符
     */
    public String getRecordNumberSqlString(String sqlKey, Map<String, Object> paramMap) {
        String sql = sqlMap.get(sqlKey);
        if (paramMap != null) {
            try {
                Template template = cfg.getTemplate(sqlKey);
                StringWriter writer = new StringWriter();
                template.process(paramMap, writer);
                sql = writer.toString();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        sql = "select count(1) as num from (" + sql + ") t_forcount";
        return sql;
    }
}
