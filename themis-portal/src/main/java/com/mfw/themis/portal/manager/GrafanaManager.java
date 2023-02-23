package com.mfw.themis.portal.manager;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.common.constant.enums.FilterMetricOperatorEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.exception.WebException;
import com.mfw.themis.common.model.bo.AppMetricBO;
import com.mfw.themis.dao.po.union.AppMetricUnionPO;
import com.mfw.themis.portal.grafana.client.GrafanaClient;
import com.mfw.themis.portal.grafana.client.models.Dashboard;
import com.mfw.themis.portal.grafana.client.models.DashboardMeta;
import com.mfw.themis.portal.grafana.client.models.DashboardPanel;
import com.mfw.themis.portal.grafana.client.models.DashboardPanelTarget;
import com.mfw.themis.portal.grafana.client.models.DashboardPanelTargetMetric;
import com.mfw.themis.portal.grafana.client.models.DashboardPanelTargetSetting;
import com.mfw.themis.portal.grafana.client.models.DashboardRow;
import com.mfw.themis.portal.grafana.client.models.GrafanaDashboard;
import com.mfw.themis.portal.grafana.configuration.GrafanaConfiguration;
import com.mfw.themis.portal.grafana.exceptions.GrafanaDashboardDoesNotExistException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author wenhong
 */
@Component
@Slf4j
public class GrafanaManager {

    private static final String HOST = "https://grafana.mfwdev.com/";
    private static final String API_KEY = "eyJrIjoiS2dvNjZHTWV1VTlVQnFPeHVDUjJZTk5RZllONm5tb3MiLCJuIjoidGhlbWlzLWFsYXJtIiwiaWQiOjF9";
    private static final String DATA_SOURCE = "Elasticsearch-Themis";

    private static GrafanaClient grafanaClient;

    /**
     * Setup the client
     */
    static {
        GrafanaConfiguration grafanaConfiguration =
                new GrafanaConfiguration().host(HOST).apiKey("Bearer " + API_KEY);
        grafanaClient = new GrafanaClient(grafanaConfiguration);
    }

    /**
     * 获取dashBoard url
     * @param grafanaUid
     * @param appCode
     * @return
     */
    public String getGrafanaUrlByUid(String grafanaUid, String appCode) {
        return HOST + "d/" + grafanaUid + "/" + getDashBoardName(appCode);
    }

    /**
     * 判断dashBoard是否存在
     * @param dashboardName
     * @return
     */
    public Boolean isDashBoardExist(String dashboardName) {

        try {
            grafanaClient.getDashboard(dashboardName);
        } catch (GrafanaDashboardDoesNotExistException notExist) {
            return false;
        } catch (Exception e) {
            log.error("get dashboard error", e);
            return false;
        }

        return true;
    }

    /**
     * 创建dashboard
     */
    public String createDashBoard(String appCode, List<AppMetricUnionPO> appMetricUnionList) {
        /**
         * 先移除已创建的dashboard
         */
        removeDashboard(appCode);

        String dashboardName = getDashBoardName(appCode);

        List<DashboardPanel> dashboardPanelList = new ArrayList<>();
        appMetricUnionList.forEach(appMetricUnion -> {
            dashboardPanelList.add(getDashboardPannel(appMetricUnion));
        });
        DashboardRow dashboardRow =
                DashboardRow.builder()
                        .collapse(false)
                        .panels(dashboardPanelList).build();
        Dashboard dashboard =
                Dashboard.builder()
                        .title(dashboardName)
                        .schemaVersion(1)
                        .rows(new ArrayList<>(Collections.singletonList(dashboardRow))).build();

        DashboardMeta dashboardMeta = DashboardMeta.builder().canSave(true).slug(dashboardName).build();
        GrafanaDashboard grafanaDashboard =
                GrafanaDashboard.builder().meta(dashboardMeta).dashboard(dashboard).build();

        try {
            DashboardMeta dashboardRes = grafanaClient.createDashboard(grafanaDashboard);
            if (null == dashboardRes.getUid()) {
                log.error("create dashboard empty, {}", dashboardRes);
            } else {
                log.info("create dashboard url {}", HOST + dashboardRes.getUrl().substring(1));
                return dashboardRes.getUid();
            }
        } catch (Exception e) {
            log.error("create dashboard error", e);
        }

        return null;
    }

    /**
     * 移除dashboard
     */
    public Boolean removeDashboard(String appCode) {

        String dashboardName = getDashBoardName(appCode);
        if (!isDashBoardExist(dashboardName)) {
            return true;
        }

        try {
            String res = grafanaClient.deleteDashboard(dashboardName);
            log.info("remove dashboard {}", res);
        } catch (Exception e) {
            log.error("remove dashboard error", e);
            return false;
        }

        return true;
    }

    /**
     * 获取dashboard name
     */
    public String getDashBoardName(String appCode) {
        return "themis_" + appCode;
    }

    /**
     * 拼装metric
     * @param appMetricUnionPO
     * @return
     */
    private DashboardPanelTargetMetric getDashboardPanelTargetMetric(AppMetricUnionPO appMetricUnionPO) {

        GroupTypeEnum groupTypeEnum = GroupTypeEnum.getByCode(appMetricUnionPO.getGroupType());

        String type;
        String field;
        List<String> percents=null;
        switch (groupTypeEnum) {
            case COUNT:
                type = "count";
                field = "select field";
                break;
            case AVG:
                type = "avg";
                field = appMetricUnionPO.getGroupField();
                break;
            case MIN:
                type = "min";
                field = appMetricUnionPO.getGroupField();
                break;
            case MAX:
                type = "max";
                field = appMetricUnionPO.getGroupField();
                break;
            case SUM:
                type = "sum";
                field = appMetricUnionPO.getGroupField();
                break;
            case PERCENT_50:
                type = "percentiles";
                field = appMetricUnionPO.getGroupField();
                percents = new ArrayList<>();
                percents.add(GroupTypeEnum.PERCENT_50.getValue());
                break;
            case PERCENT_90:
                type = "percentiles";
                field = appMetricUnionPO.getGroupField();
                percents = new ArrayList<>();
                percents.add(GroupTypeEnum.PERCENT_90.getValue());
                break;
            case PERCENT_95:
                type = "percentiles";
                field = appMetricUnionPO.getGroupField();
                percents = new ArrayList<>();
                percents.add(GroupTypeEnum.PERCENT_95.getValue());
                break;
            case PERCENT_99:
                type = "percentiles";
                field = appMetricUnionPO.getGroupField();
                percents = new ArrayList<>();
                percents.add(GroupTypeEnum.PERCENT_99.getValue());
                break;
            case PERCENT_999:
                type = "percentiles";
                field = appMetricUnionPO.getGroupField();
                percents = new ArrayList<>();
                percents.add(GroupTypeEnum.PERCENT_999.getValue());
                break;
            default:
                throw new WebException("聚合类型不支持");
        }

        return  DashboardPanelTargetMetric.builder()
                .field(field)
                .id("1")
                .type(type)
                .settings(DashboardPanelTargetSetting.builder().percents(percents).build()).build();
    }

    /**
     * 拼装ES查询语句
     * @param appMetricUnion
     * @return
     */
    private String getQuery(AppMetricUnionPO appMetricUnion) {
        AppMetricBO appMetricBO = AppMetricConvert.toAppMetricBO(appMetricUnion);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("_index:\"%s\"", getIndexName(appMetricUnion)));

        appMetricBO.getExpressionList().forEach(expression -> {
            final String metric = expression.getOrDefault("metric", "").toString();
            final String filterMetricOperator = expression.getOrDefault("filterMetricOperator", "").toString();
            final String metricValue = expression.getOrDefault("metricValue", "").toString();
            if (StringUtils.isAnyBlank(metric, filterMetricOperator, metricValue)) {
                throw new WebException("appMetricBO" + JSON.toJSONString(appMetricBO) + "指标表达式异常expression:" + JSON
                        .toJSONString(expression));
            }

            FilterMetricOperatorEnum operatorEnum = FilterMetricOperatorEnum.theValueOf(filterMetricOperator);

            if (operatorEnum.equals(FilterMetricOperatorEnum.IS)) {
                stringBuilder.append(String.format(" AND %s:%s", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.GT)) {
                stringBuilder.append(String.format(" AND %s:>%s", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.LT)) {
                stringBuilder.append(String.format(" AND %s:<%s", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.GTE)) {
                stringBuilder.append(String.format(" AND %s:>=%s", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.LTE)) {
                stringBuilder.append(String.format(" AND %s:<=%s", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.IS_NOT)) {
                stringBuilder.append(String.format(" NOT %s:%s", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.IS_NOT_ONE_OF)) {
                stringBuilder.append(String.format(" NOT %s:(\"%s\")", metric, metricValue));
                return;
            }

            if (operatorEnum.equals(FilterMetricOperatorEnum.IS_ONE_OF)) {
                stringBuilder.append(String.format(" AND %s:(\"%s\")", metric, metricValue));
                return;
            }
        });

        return stringBuilder.toString();
    }

    /**
     * 获取索引名
     * @param appMetricUnion
     * @return
     */
    private String getIndexName(AppMetricUnionPO appMetricUnion) {
        return "themis_" + appMetricUnion.getAppCode() + "_*";
    }

    /**
     * 获取dashBoard pannel
     * @param appMetricUnion
     * @return
     */
    private DashboardPanel getDashboardPannel(AppMetricUnionPO appMetricUnion) {
        DashboardPanelTargetMetric metric = getDashboardPanelTargetMetric(appMetricUnion);
        DashboardPanelTargetMetric agg = 
                DashboardPanelTargetMetric.builder()
                        .field("datetime")
                        .id("2")
                        .type("date_histogram")
                        .settings(DashboardPanelTargetSetting.builder().interval("1m").min_doc_count(0).trimEdges(0).build()).build();

        String query = getQuery(appMetricUnion);
        DashboardPanelTarget dashboardPanelTarget =
                DashboardPanelTarget.builder()
                        .refId("A")
                        .query(query)
                        .timeField("datetime")
                        .metrics(new ArrayList<>(Collections.singletonList(metric)))
                        .bucketAggs(new ArrayList<>(Collections.singletonList(agg))).build();
        return DashboardPanel.builder()
                .targets(new ArrayList<>(Collections.singletonList(dashboardPanelTarget)))
                .datasource(DATA_SOURCE)
                .type(DashboardPanel.Type.GRAPH)
                .fill(1)
                .title(appMetricUnion.getName())
                .linewidth(1)
                .lines(true)
                .height("300px")
                .span(12).build();
    }

}
