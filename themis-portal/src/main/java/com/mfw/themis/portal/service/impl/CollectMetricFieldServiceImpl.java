package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.CollectMetricFieldConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO.Field;
import com.mfw.themis.dao.mapper.CollectMetricFieldDao;
import com.mfw.themis.dao.po.CollectMetricFieldPO;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoData;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.portal.model.dto.QueryCollectMetricFieldParam;
import com.mfw.themis.portal.service.CollectMetricFieldService;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 上报事件服务接口
 * @author wenhong
 */
@Service
@Slf4j
public class CollectMetricFieldServiceImpl implements CollectMetricFieldService {

    @Autowired
    private CollectMetricFieldDao collectMetricFieldDao;

    @Autowired
    private EmployeeClient employeeClient;
    /**
     * 上报事件列表
     *
     * @return
     */
    @Override
    public IPage<CollectMetricFieldDTO> collectMetricFieldList(QueryCollectMetricFieldParam req) {

        QueryWrapper<CollectMetricFieldPO> wrapper = new QueryWrapper<>();

        if(null != req.getAppCode()){
            wrapper.lambda().eq(CollectMetricFieldPO::getAppCode, req.getAppCode());
        }

        if(null != req.getStatus()){
            wrapper.lambda().eq(CollectMetricFieldPO::getStatus, req.getStatus());
        }

        if(null != req.getKeyword() && StringUtils.isNotBlank(req.getKeyword())){
            wrapper.lambda().and(w->w.like(CollectMetricFieldPO::getMetric, req.getKeyword())
                    .or().like(CollectMetricFieldPO::getDescription, req.getKeyword()));
        }

        wrapper.lambda().eq(CollectMetricFieldPO::getIsDelete, 0);
        wrapper.lambda().orderByDesc(CollectMetricFieldPO::getId);

        Page<CollectMetricFieldPO> page = new Page<>(req.getPage(), req.getPageSize());
        IPage<CollectMetricFieldPO> dbResult = collectMetricFieldDao.selectPage(page, wrapper);

        IPage<CollectMetricFieldDTO> result = new Page<>(req.getPage(),
                req.getPageSize());
        BeanUtils.copyProperties(dbResult, result);

        List<CollectMetricFieldDTO> list = CollectMetricFieldConvert.toDTOList(dbResult.getRecords());

        // 设置更新人姓名
        setItemEmployeeName(list);

        result.setRecords(list);


        return result;
    }

    /**
     * 设置更新人姓名
     * @param list
     */
    private void setItemEmployeeName(List<CollectMetricFieldDTO> list){
        if(null == list || list.size() <= 0){
            return;
        }

        Set<Long> uids = list.stream().map(CollectMetricFieldDTO::getOperator).collect(Collectors.toSet());

        if(uids.size() > 0){
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(new ArrayList<>(uids));
            if(null == empInfoListResponse.getData()){
                list.forEach(item -> {
                    item.setOperatorUname("");
                });

                return;
            }
            Map<Long, EmpInfoData> employeInfoMap = empInfoListResponse.getData().stream()
                    .collect(Collectors.toMap(EmpInfoData::getUid, Function.identity(), (value1, value2) -> value2));

            list.forEach(item -> {
                if(item.getOperator() > 0){
                    if(employeInfoMap.containsKey(item.getOperator())){
                        item.setOperatorUname(employeInfoMap.get(item.getOperator()).getName());
                    }
                }else{
                    item.setOperatorUname("");
                }
            });
        }
    }

    /**
     * 创建上报事件字段
     * @param collectMetricFieldDTO
     * @return
     */
    @Override
    public Integer create(CollectMetricFieldDTO collectMetricFieldDTO){
        // 校验数据
        checkCollectMetric(collectMetricFieldDTO);

        CollectMetricFieldPO po = CollectMetricFieldConvert.toPO(collectMetricFieldDTO);

        po.setIsDelete(false);
        if(null == collectMetricFieldDTO.getStatus()){
            po.setStatus(EnableEnum.DISABLE.getCode());
        }else{
            po.setStatus(collectMetricFieldDTO.getStatus().getCode());
        }

        return collectMetricFieldDao.insert(po);
    }

    /**
     * 更新上报事件字段
     * @param collectMetricFieldDTO
     * @return
     */
    @Override
    public Integer update(CollectMetricFieldDTO collectMetricFieldDTO){

        CollectMetricFieldPO po = collectMetricFieldDao.selectById(collectMetricFieldDTO.getId());
        if(null == po){
            throw new ResourceNotFoundException(ResourceEnum.COLLECT_METRIC, collectMetricFieldDTO.getId().longValue());
        }

        // 校验数据
        checkCollectMetric(collectMetricFieldDTO);

        CollectMetricFieldPO newPO = CollectMetricFieldConvert.toPO(collectMetricFieldDTO);
        BeanUtils.copyProperties(newPO, po);
        if(null != collectMetricFieldDTO.getStatus()){
            po.setStatus(collectMetricFieldDTO.getStatus().getCode());
        }
        po.setIsDelete(false);
        po.setMtime(null);

        return collectMetricFieldDao.updateById(po);
    }

    /**
     * 字段校验
     * @param collectMetricFieldDTO
     */
    private void checkCollectMetric(CollectMetricFieldDTO collectMetricFieldDTO){
        QueryWrapper<CollectMetricFieldPO> queryWrapper = new QueryWrapper<>();

        // 检查metric唯一性
        queryWrapper.lambda().eq(CollectMetricFieldPO::getAppCode, collectMetricFieldDTO.getAppCode());
        queryWrapper.lambda().eq(CollectMetricFieldPO::getMetric, collectMetricFieldDTO.getMetric());
        if(null != collectMetricFieldDTO.getId()){
            queryWrapper.lambda().ne(CollectMetricFieldPO::getId, collectMetricFieldDTO.getId());
        }

        List<CollectMetricFieldPO> collectMetricFieldList = collectMetricFieldDao.selectList(queryWrapper);
        if(null != collectMetricFieldList && collectMetricFieldList.size() > 0){
            throw new ServiceException("事件编码："+collectMetricFieldDTO.getMetric()+"已存在");
        }

        Map<String, String> tagsMap = collectMetricFieldDTO.getTags().stream()
                .collect(Collectors.toMap(CollectMetricFieldDTO.Field::getMetric, CollectMetricFieldDTO.Field::getType));

        Map<String, String> fieldsMap = collectMetricFieldDTO.getFields().stream()
                .collect(Collectors.toMap(CollectMetricFieldDTO.Field::getMetric, CollectMetricFieldDTO.Field::getType));

        // 检查是否有字段名相同类型不同的情况
        QueryWrapper<CollectMetricFieldPO> queryWrapperAll = new QueryWrapper<>();
        queryWrapper.lambda().eq(CollectMetricFieldPO::getAppCode, collectMetricFieldDTO.getAppCode());
        List<CollectMetricFieldPO> appFieldList = collectMetricFieldDao.selectList(queryWrapperAll);
        if(null != appFieldList){

            appFieldList.forEach(collectMetricField -> {
                if(collectMetricField.getMetric().equals(collectMetricFieldDTO.getMetric())){
                    return;
                }

                CollectMetricFieldDTO metricFieldDTO = CollectMetricFieldConvert.toDTO(collectMetricField);
                if(null != metricFieldDTO.getTags()){
                    metricFieldDTO.getTags().forEach(field -> {
                        if(tagsMap.containsKey(field.getMetric()) && !tagsMap.get(field.getMetric()).equals(field.getType())){
                            throw new ServiceException("该应用已定义过字段：" + field.getMetric()+"，当前类型与已定义类型不一致");
                        }
                    });
                }

                if(null != metricFieldDTO.getFields()){
                    metricFieldDTO.getFields().forEach(field -> {
                        if(fieldsMap.containsKey(field.getMetric()) && !fieldsMap.get(field.getMetric()).equals(field.getType())){
                            throw new ServiceException("该应用已定义过字段：" + field.getMetric()+"，当前类型与已定义类型不一致");
                        }
                    });
                }
            });

        }

    }

    /**
     * 删除上报事件字段
     * @param id
     * @return
     */
    @Override
    public Integer delete(Integer id, Long operator){
        CollectMetricFieldPO collectMetricFieldPO = collectMetricFieldDao.selectById(id);
        if(null == collectMetricFieldPO){
            throw new ResourceNotFoundException(ResourceEnum.COLLECT_METRIC, id.longValue());
        }

        collectMetricFieldPO.setMtime(null);
        collectMetricFieldPO.setOperator(operator);
        collectMetricFieldPO.setIsDelete(true);

        return collectMetricFieldDao.updateById(collectMetricFieldPO);
    }

    /**
     * 启用/禁用上报事件
     * @param id
     * @param status
     * @param operator
     * @return
     */
    @Override
    public Integer changeStatus(Integer id, Integer status, Long operator){

        CollectMetricFieldPO po = collectMetricFieldDao.selectById(id);
        if(null == po){
            throw new ResourceNotFoundException(ResourceEnum.COLLECT_METRIC, id.longValue());
        }

        po.setMtime(null);
        po.setOperator(operator);
        po.setStatus(status);

        return collectMetricFieldDao.updateById(po);
    }

}
