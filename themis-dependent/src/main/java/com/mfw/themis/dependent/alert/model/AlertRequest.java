package com.mfw.themis.dependent.alert.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统部报警接口请求
 *
 * @author liuqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {

    /**
     * 支持设置5种级别 normal普通 warning警告 urgency紧急 critical严重 disaster灾难 默认warning.
     */
    private String level;
    /**
     * 告警来源 如prometheus、zabbix...
     */
    private String source;
    /**
     * 告警时间 RFC3339时间格式 "{year}-{month}-{day}T{hour}:{min}:{sec}[.{frac_sec}]Z" example: 2020-07-08T08:00:13Z.
     */
    private String alertTime;
    /**
     * 告警标题 最大长度120字符.
     */
    private String title;
    /**
     * 告警描述或内容 UTF-8编码 最大长度1000字符. 就算你发送了最大字符的长度，但也不能保证可能都发送出来，比如短信里一条最多330个字符， 当然还要把标题、告警时间等都算在一起
     * 又比如企业微信，最大长度约2000个字符，也是把标题、告警时间算在一起的 语音短信是不发送告警内容，接听后会听到"收到紧急告警"的播报.
     */
    private String content;
    /**
     * 告警接收人 员工号,邮箱,手机号,mfwuid 注意格式都是字符串 我们推荐使用 员工号 > 邮箱 > 手机号 > mfwuid 最多一次发送30人 超过30人的报警，我们只发送前30人.
     */
    private List<String> receivers;
    /**
     * 告警状态 故障前pre、故障中in、已恢复solve.
     */
    private String state;
    /**
     * 告警通道 email邮箱 wechat_work企业微信 sms短信 voice_sms语音短信 默认通道为企业微信.
     */
    private List<String> channels;
    /**
     * 告警唯一标识 暂未启用
     */
    private String uniqueId;
    /**
     * 更多信息的链接 设置此链接，在企业微信里，将显示更多信息链接.
     */
    private String moreUrl;
}
