package com.lyne.module.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.material.exception.MaterialServerException;
import com.lyne.module.material.mapper.MaterialDepotMapper;
import com.lyne.module.material.mapper.MaterialRackMapper;
import com.lyne.module.material.service.DepotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * 仓库服务
 *
 * @author lyne
 */
@Service
public class DepotServiceImpl extends ServiceImpl<MaterialDepotMapper, MaterialDepot> implements DepotService {
    @Resource
    private MaterialDepotMapper depotMapper;
    @Resource
    private MaterialRackMapper rackMapper;

    /**
     * 仓库绑定货架
     *
     * @param depotId 仓库ID
     * @param rackId  货架ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRack(String depotId, String rackId) {
        DataUtil.checkNumbers(depotId, rackId);
        MaterialRack rack = rackMapper.selectById(rackId);
        Assert.notNull(rack, "未知货架");
        Assert.notNull(depotMapper.selectById(depotId), "未知仓库");
        if (Objects.equals(rack.getDepotId(), depotId)) {
            return true;
        }
        rack.setDepotId(depotId);
        return rackMapper.updateById(rack) >= 0;
    }

    /**
     * 仓库解绑货架
     *
     * @param depotId 仓库ID
     * @param rackId  货架ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRack(String depotId, String rackId) {
        DataUtil.checkNumbers(depotId, rackId);
        MaterialRack rack = rackMapper.selectById(rackId);
        Assert.notNull(rack, "未知货架");
        Assert.notNull(depotMapper.selectById(depotId), "未知仓库");
        if (rack.getDepotId() == null || !Objects.equals(rack.getDepotId(), depotId)) {
            throw new MaterialServerException("该货架与仓库尚未绑定");
        }
        rack.setDepotId(null);
        return rackMapper.updateById(rack) >= 0;
    }


    /**
     * 获取仓库总货架
     *
     * @param depotId 仓库ID
     * @return RAC
     */
    @Override
    public List<MaterialRack> getRackList(String depotId) {
        if (StringUtil.isEmpty(depotId)) {
            throw new ArgumentException("参数异常");
        }
        return depotMapper.getRack(depotId);
    }

    /**
     * 获取仓库指定货架
     *
     * @param depotId 仓库ID
     * @param rackId  货架ID
     * @return RAC
     */
    @Override
    public MaterialRack getRackById(String depotId, String rackId) {
        DataUtil.checkNulls(depotId, rackId);
        return depotMapper.getRackByRack(depotId, rackId);
    }

    /**
     * 多数据查询
     * @param wrapper wrapper
     * @return list
     */
    @Override
    public List<MaterialDepot> queryList(Wrapper<MaterialDepot> wrapper) {
        List<MaterialDepot> materialDepots = depotMapper.selectList(wrapper);
        materialDepots.forEach(materialDepot -> materialDepot
                .setRackList(getRackList(materialDepot.getDepotId())));
        return materialDepots;
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<MaterialDepot> queryByPage(int page, int size) {
        PageObject<MaterialDepot> pageObject = new PageObject<>();
        IPage<MaterialDepot> iPage = depotMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }


}
