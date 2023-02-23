package com.mfw.themis.common.constant.enums;

/**
 * 筛选操作符
 * @author guosp
 */
public enum FilterMetricOperatorEnum {

    IS("is", "=", "等于"),
    IS_NOT("is_not", "≠", "不等于"),
    IS_ONE_OF("is_one_of", "⊆", "包含"),
    IS_NOT_ONE_OF("is_not_one_of", "⊄", "不包含"),
    GT("gt", ">", "大于"),
    LT("lt", "<", "小于"),
    GTE("gte", ">=", "大于等于"),
    LTE("lte", "<=", "小于等于"),
    LIKE("like", "like", "like");

    private String value;

    private String operator;

    private String description;

    FilterMetricOperatorEnum(String value, String operator, String description) {
        this.value = value;
        this.operator = operator;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public String getoperator() {
        return this.operator;
    }

    public String getDescription() {
        return this.description;
    }

    public static FilterMetricOperatorEnum getByoperator(String operator) {
        for (FilterMetricOperatorEnum entity : FilterMetricOperatorEnum.values()) {
            if (entity.operator.equals(operator)) {
                return entity;
            }
        }
        throw new IllegalArgumentException("未找到对应的值, operator:[" + operator + "]");
    }

    public static FilterMetricOperatorEnum theValueOf(String value) {
        for (FilterMetricOperatorEnum entity : FilterMetricOperatorEnum.values()) {
            if (entity.value.equals(value)) {
                return entity;
            }
        }
        throw new IllegalArgumentException("未找到对应的值, value:[" + value + "]");
    }
}
