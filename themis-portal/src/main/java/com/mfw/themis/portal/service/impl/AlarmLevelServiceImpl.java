package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfw.themis.common.constant.enums.AlertRateTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AlarmLevelConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.WebException;
import com.mfw.themis.common.model.AlertRate;
import com.mfw.themis.common.model.dto.AlarmLevelDTO;
import com.mfw.themis.dao.mapper.AlarmLevelDao;
import com.mfw.themis.dao.po.AlarmLevelPO;
import com.mfw.themis.portal.controller.model.AlarmLevelReqDTO;
import com.mfw.themis.portal.service.AlarmLevelService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AlarmLevelServiceImpl implements AlarmLevelService {

    @Autowired
    private AlarmLevelDao alarmLevelDao;

    /**
     * 报警等级列表
     *
     * @return
     */
    @Override
    public List<AlarmLevelDTO> alarmLevelList() {

        QueryWrapper<AlarmLevelPO> wrapper = new QueryWrapper<>();

        wrapper.lambda().eq(AlarmLevelPO::getIsDelete, 0);

        List<AlarmLevelPO> alarmLevelPOList = alarmLevelDao.selectList(wrapper);

        return AlarmLevelConvert.toDTOList(alarmLevelPOList);
    }

    @Override
    public AlarmLevelDTO create(AlarmLevelReqDTO req) {
        if (req == null) {
            throw new WebException("参数错误");
        }
        AlarmLevelDTO alarmLevel = convertToDTO(req);
        AlarmLevelPO po = AlarmLevelConvert.toPO(alarmLevel);
        alarmLevelDao.insert(po);
        alarmLevel.setId(po.getId());
        return alarmLevel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(AlarmLevelReqDTO req) {
        AlarmLevelDTO alarmLevelDTO = convertToDTO(req);
        if (alarmLevelDTO.getId() == null) {
            throw new WebException("参数错误,id 不存在");
        }
        AlarmLevelPO alarmLevelPO = alarmLevelDao.selectById(alarmLevelDTO.getId());
        if (alarmLevelPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_LEVEL, alarmLevelDTO.getId());
        }

        return alarmLevelDao.updateById(AlarmLevelConvert.toPO(alarmLevelDTO)) > 0;
    }

    @Override
    public Boolean delete(Long id) {
        AlarmLevelPO alarmLevelPO = alarmLevelDao.selectById(id);
        if (alarmLevelPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_LEVEL, id);
        }

        alarmLevelPO.setIsDelete(true);
        return alarmLevelDao.updateById(alarmLevelPO) > 0;
    }

    /**
     * 参数转化成为dto
     *
     * @param req
     * @return
     */
    private AlarmLevelDTO convertToDTO(AlarmLevelReqDTO req) {
        AlertRate alertRate = new AlertRate();
        alertRate.setRateInterval(
                Optional.ofNullable(req.getRateInterval()).orElseThrow(() -> new WebException("alarmRate参数字段缺失")));
        alertRate.setRateTimes(
                Optional.ofNullable(req.getRateTimes()).orElseThrow(() -> new WebException("alarmRate参数字段缺失"))
        );
        alertRate.setRateType(AlertRateTypeEnum.getByCode(
                Optional.ofNullable(req.getRateType()).orElseThrow(() -> new WebException("alarmRate参数字段缺失"))
        ));

        return AlarmLevelDTO.builder()
                .id(req.getId())
                .alarmChannels(req.getAlarmChannels())
                .alarmRate(alertRate)
                .alarmScope(req.getAlarmScope())
                .level(req.getLevel())
                .build();
    }
}
