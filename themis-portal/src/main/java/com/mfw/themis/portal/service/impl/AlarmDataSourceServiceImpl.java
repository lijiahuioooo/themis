package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AlarmDataSourceConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.dao.mapper.AlarmDataSourceDao;
import com.mfw.themis.dao.po.AlarmDataSourcePO;
import com.mfw.themis.portal.model.dto.QueryAlarmDataSourceDTO;
import com.mfw.themis.portal.service.AlarmDataSourceService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author guosp
 */
@Service
public class AlarmDataSourceServiceImpl implements AlarmDataSourceService {


    @Autowired
    private AlarmDataSourceDao alarmDataSourceDao;

    @Override
    public AlarmDataSourceDTO queryById(Long id) {
        return AlarmDataSourceConvert.toDTO(alarmDataSourceDao.selectById(id));
    }

    @Override
    public IPage<AlarmDataSourceDTO> queryDataSourcePage(QueryAlarmDataSourceDTO queryAlarmDataSourceDTO) {
        QueryWrapper<AlarmDataSourcePO> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(queryAlarmDataSourceDTO.getName())) {
            wrapper.lambda().like(AlarmDataSourcePO::getName, queryAlarmDataSourceDTO.getName());
        }

        if (null != queryAlarmDataSourceDTO.getId()) {
            wrapper.lambda().eq(AlarmDataSourcePO::getId, queryAlarmDataSourceDTO.getId());
        }
        wrapper.lambda().eq(AlarmDataSourcePO::getIsDelete, false);

        wrapper.lambda().orderByDesc(AlarmDataSourcePO::getId);

        Page<AlarmDataSourcePO> page = new Page<>(queryAlarmDataSourceDTO.getPage(),
                queryAlarmDataSourceDTO.getPageSize());
        IPage<AlarmDataSourcePO> dbResult = alarmDataSourceDao.selectPage(page, wrapper);

        IPage<AlarmDataSourceDTO> result = new Page<>(queryAlarmDataSourceDTO.getPage(),
                queryAlarmDataSourceDTO.getPageSize());
        BeanUtils.copyProperties(dbResult, result);
        List<AlarmDataSourceDTO> list = AlarmDataSourceConvert.toDTOList(dbResult.getRecords());
        result.setRecords(list);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlarmDataSourceDTO create(AlarmDataSourceDTO alarmDataSourceDTO) {
        AlarmDataSourcePO alarmDataSourcePO = AlarmDataSourceConvert.toPO(alarmDataSourceDTO);
        alarmDataSourcePO.setIsDelete(false);
        alarmDataSourceDao.insert(alarmDataSourcePO);
        alarmDataSourceDTO.setId(alarmDataSourcePO.getId());

        return alarmDataSourceDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AlarmDataSourceDTO alarmDataSourceDTO) {
        if (alarmDataSourceDao.selectById(alarmDataSourceDTO.getId()) == null) {
            throw new ResourceNotFoundException(ResourceEnum.DATASOURCE, alarmDataSourceDTO.getId());
        }

        AlarmDataSourcePO alarmMetricPO = AlarmDataSourceConvert.toPO(alarmDataSourceDTO);
        return alarmDataSourceDao.updateById(alarmMetricPO) == 1;
    }


    @Override
    public boolean delete(Long id) {
        AlarmDataSourcePO alarmDataSourcePO = alarmDataSourceDao.selectById(id);
        if (alarmDataSourcePO == null) {
            throw new ResourceNotFoundException(ResourceEnum.DATASOURCE, id);
        }

        alarmDataSourcePO.setIsDelete(true);

        return alarmDataSourceDao.updateById(alarmDataSourcePO) == 1;
    }
}
