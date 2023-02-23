package com.mfw.themis.common.constant.enums;

/**
 * @author jiangqiao
 */
public enum GlobalCodeEnum {

    /**
     * 全局返回码定义 - 0开头
     */
    GL_SUCC_0000(0, "成功"),
    GL_FAIL_NO_AUTH(991, "无权限访问"),
    GL_FAIL_NO_LOGIN(992, "用户未登录"),
    GL_FAIL_9993(993, "请求被限流"),
    GL_FAIL_9994(994, "无可用服务"),
    GL_FAIL_9995(995, "参数异常"),
    GL_FAIL_9996(996, "不支持的HttpMethod"),
    GL_FAIL_9997(997, "HTTP错误"),
    GL_FAIL_9998(998, "参数错误"),
    GL_FAIL_9999(999, "系统异常"),

    /**
     * TOPIC 1000码段
     */
    GL_FAIL_1000(1000, "Topic同步失败"),
    GL_FAIL_1001(1001, "启动topic订阅失败"),
    GL_FAIL_1002(1002, "暂停topic订阅失败"),
    GL_FAIL_1003(1003, "创建topic失败"),
    GL_FAIL_1004(1004, "删除topic失败"),
    GL_FAIL_1005(1005, "修改topic失败"),
    GL_FAIL_1006(1006, "查找topic失败"),


    //producer topic conf
    PRODUCER_TOPIC_CONF_FAIL(200001, "获取producer topic conf 失败"),
    PRODUCER_TOPIC_CONF_ADD_FAIL(200002, "添加producer topic conf 失败"),
    PRODUCER_TOPIC_CONF_UPDATE_FAIL(200003, "修改producer topic conf 失败"),
    PRODUCER_TOPIC_CONF_DELETE_FAIL(200004, "删除producer topic conf 失败"),
    PRODUCER_TOPIC__HAS_EXIST(200005, "topic已纯在，无法创建"),
    PRODUCER_TOPIC__CREATE_FAIL(200006, "broker创建topic失败"),
    PRODUCER_TOPIC__APPLY_FAIL(200007, "producer申请topic失败"),
    PRODUCER_TOPIC__APPLY_UPDATE_FAIL(200007, "producer更新topic失败"),
    PRODUCER_TOPIC__START_FAIL(200008, "producer申请topic失败"),
    PRODUCER_TOPIC__SUSPEND_FAIL(200009, "producer申请topic失败"),
    //cluster
    CLUSTER_ALL_FAIL(210001, "获取cluster信息失败"),
    CLUSTER_ADD_FAIL(210002, "添加cluster信息失败"),
    CLUSTER_UPDATE_FAIL(210003, "修改cluster信息失败"),
    CLUSTER_DELETE_FAIL(210004, "删除cluster信息失败"),
    CLUSTER_INSTANCE_FAIL(210005, "cluster无可用服务");
    /**
     * 编码
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;


    GlobalCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举类型
     *
     * @param code 编码
     * @return
     */
    public static GlobalCodeEnum getByCode(Integer code) {
        //判空
        if (code == null) {
            return null;
        }
        //循环处理
        GlobalCodeEnum[] values = GlobalCodeEnum.values();
        for (GlobalCodeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
