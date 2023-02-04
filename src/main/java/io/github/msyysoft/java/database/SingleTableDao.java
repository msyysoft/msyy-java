package io.github.msyysoft.java.database;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SingleTableDao extends AbstractDao {

    public <T extends TableBean> int addNewRecord(T bean) {
        int i = SingleTablePersistUtils.addNewRecord(this, bean);
        clearBeanConditions(bean);
        return i;
    }

    public <T extends TableBean> int addNewRecordBySqlConditions(T bean) {
        if (recordNumberBySqlConditions(bean) == 0) {
            return addNewRecord(bean);
        }
        return 0;
    }

    public <T extends TableBean> int addNewRecordBatch(List<T> beanList) {
        int[] ii = SingleTablePersistUtils.addNewRecordBatch(this, beanList);
        if (!CollectionUtils.isEmpty(beanList)) {
            beanList.forEach(this::clearBeanConditions);
        }
        if (ii != null) {
            int i = 0;
            for (int j : ii) i += j;
            return i;
        } else {
            return 0;
        }
    }

    public <T extends TableBean> int addNewRecordBatchBySqlConditions(List<T> beanList) {
        if (!CollectionUtils.isEmpty(beanList)) {
            List<? extends TableBean> addList = beanList.stream().filter(bean -> recordNumberBySqlConditions(bean) == 0).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(addList)) {
                return addNewRecordBatch(addList);
            }
        }
        return 0;
    }

    public <T extends TableBean> int updateRecordAllByPrimaryKey(T bean) {
        int i = SingleTablePersistUtils.updateRecordAllByPrimaryKey(this, bean);
        clearBeanConditions(bean);
        return i;
    }

    public <T extends TableBean> int updateRecordColumnsBySqlConditions(T bean) {
        int i = SingleTablePersistUtils.updateRecordColumnsBySqlConditions(this, bean);
        clearBeanConditions(bean);
        return i;
    }

    public <T extends TableBean> int deleteRecordBySqlConditions(T bean) {
        int i = SingleTablePersistUtils.deleteRecordBySqlConditions(this, bean);
        clearBeanConditions(bean);
        return i;
    }

    public <T extends TableBean> int clearTable(T bean) {
        int i = SingleTablePersistUtils.clearTable(this, bean);
        clearBeanConditions(bean);
        return i;
    }

    public <T extends TableBean> T getRecordByPKMap(T bean, Map<String, Object> paramMap) {
        T t = SingleTablePersistUtils.getRecordByPKMap(this, bean, paramMap);
        clearBeanConditions(bean);
        return t;
    }

    public <T extends TableBean> List<T> getRecordListBySqlConditions(T bean) {
        List<T> tt = SingleTablePersistUtils.getRecordListBySqlConditions(this, bean);
        clearBeanConditions(bean);
        return tt;
    }

    public <T extends TableBean> int recordNumberBySqlConditions(T bean) {
        int i = SingleTablePersistUtils.recordNumberBySqlConditions(this, bean);
        clearBeanConditions(bean);
        return i;
    }

    public <T extends TableBean> void clearBeanConditions(T bean) {
        if (bean != null)
            bean.clearAllConditions();
    }
}
