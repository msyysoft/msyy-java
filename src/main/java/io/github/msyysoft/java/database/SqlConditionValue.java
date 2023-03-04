package io.github.msyysoft.java.database;

/**
 * operation 可以设置 > < >= <= = !=
 */
public class SqlConditionValue {
    private Object value;
    private String operation;

    public SqlConditionValue(String operation, Object value) {
        this.operation = operation == null ? "=" : operation;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getOperation() {
        return operation;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
