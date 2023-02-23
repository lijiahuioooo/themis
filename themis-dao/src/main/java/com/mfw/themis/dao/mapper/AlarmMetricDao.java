package com.mfw.themis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfw.themis.dao.po.AlarmMetricPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * @author liuqi
 */
@Repository
@Mapper
public interface AlarmMetricDao extends BaseMapper<AlarmMetricPO> {

}
