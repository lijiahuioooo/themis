package com.mfw.themis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.union.AppMetricUnionPO;
import com.mfw.themis.dao.po.union.QueryAppMetricPO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author liuqi
 */
public interface AppMetricDao extends BaseMapper<AppMetricPO> {

    /**
     * 查询用用指标列表
     *
     * @param page
     * @param param
     * @return
     */
    IPage<AppMetricUnionPO> selectAppMetricPageByParam(
            Page<AppMetricUnionPO> page, @Param("param") QueryAppMetricPO param);

    /**
     * Prometheus指标筛选
     *
     * @param param
     * @return
     */
    List<AppMetricUnionPO> selectAppMetricSuggestPageByParam(@Param("param") QueryAppMetricPO param);

    /**
     * 按收集类型和数据源类型查询指标
     *
     * @return
     */
    @Select("<script>" +
            "SELECT * FROM t_app_metric "
            + " WHERE metric_id IN (SELECT id FROM t_alarm_metric WHERE collect_type=#{collectType} AND is_delete=0 " +
            " <if test='sourceType!=null'>AND source_type = #{sourceType} </if> ) "
            + " AND app_id IN (SELECT id FROM t_app WHERE is_delete=0)"
            + " AND is_delete=0 AND status=1" +
            "</script>")
    List<AppMetricPO> selectAllAppMetricByCollectType(@Param("collectType") Integer collectType,
            @Param("sourceType") Integer sourceType);

    @Update("UPDATE t_app_metric SET datasource_id=#{newId} WHERE datasource_id=#{oldId}")
    int updateDataSource(@Param("oldId") Long oldId,@Param("newId")Long newId);
}
