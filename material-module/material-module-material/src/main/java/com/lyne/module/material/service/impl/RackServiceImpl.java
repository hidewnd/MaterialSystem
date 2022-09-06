package com.lyne.module.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.material.exception.MaterialServerException;
import com.lyne.module.material.mapper.MaterialMapper;
import com.lyne.module.material.mapper.MaterialRackMapper;
import com.lyne.module.material.service.RackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * 货架服务
 *
 * @author lyne
 */
@Service
public class RackServiceImpl extends ServiceImpl<MaterialRackMapper, MaterialRack> implements RackService {

    @Resource
    private MaterialRackMapper rackMapper;

    @Resource
    private MaterialMapper materialMapper;

    /**
     * 物料存库 -> 货架
     *
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存库数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMaterial(String rackId, String recordId, String materialId, Long stock) {
        DataUtil.checkNumbers(rackId, materialId, stock);
        if (stock <= 0) {
            return false;
        }
        // 可用容量
        long capacity = rackMapper.getActualCapacity(rackId);
        // 物料
        Material material = materialMapper.selectById(materialId);
        Assert.notNull(material, "物料不存在");
        if (capacity >= 0 && capacity - stock * material.getCapacityRatio() < 0) {
            throw new MaterialServerException("当前容量不足,可用容量为： " + capacity);
        }
        return rackMapper.bindMaterial(rackId, recordId, materialId, stock) >= 0;
    }

    /**
     * 物料批量入库
     * @param list list
     * @return true
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMaterial(List<Material> list) {
        if (list.size() > 0) {
            for (Material material : list) {
                if (!bindMaterial(material.getRackId(), material.getRecordId(), material.getMaterialId(),
                        material.getStock())) {
                    throw new MaterialServerException("批量新增物料库存失败");
                }
            }
        }
        return true;
    }


    /**
     * 物料出库
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存库数
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMaterial(String rackId, String recordId, String materialId, Long stock) {
        DataUtil.checkNumbers(rackId, materialId, stock);
        return rackMapper.removeMaterial(rackId, recordId, materialId, stock) >= 0;
    }

    /**
     * 修改物料库存数
     * @param rackId     货架ID
     * @param materialId 物料ID
     * @param stock      存库数
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterial(String rackId, String recordId, String materialId, Long stock) {
        DataUtil.checkNumbers(rackId, materialId, stock);
        List<Material> materialList = getMaterialList(rackId);
        if (materialList.size() == 0) {
            return bindMaterial(rackId, recordId, materialId, stock);
        }
        for (Material material : materialList) {
            if (Objects.equals(material.getMaterialId(), materialId)) {
                return rackMapper.updateMaterial(rackId, recordId, materialId, stock) >= 0;
            }
        }
        return bindMaterial(rackId, recordId, materialId, stock);
    }

    /**
     * 批量修改物料库存数
     * @param list list
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterial(List<Material> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (Material material : list) {
            if (!updateMaterial(material.getRackId(), material.getRecordId(), material.getMaterialId(),
                    material.getStock())) {
                throw new MaterialServerException("批量修改物料库存失败");
            }
        }
        return true;
    }


    @Override
    public List<Material> getAllMaterial() {
        return rackMapper.getAllMaterial();
    }

    @Override
    public List<String> getMaterialIds(String rackId) {
        return rackMapper.getMaterialIds(rackId);
    }

    /**
     * 获取该货架中的库存物料
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return list
     */
    @Override
    public List<Material> getMaterialList(String rackId, String recordId, String materialId) {
        if (rackId != null) DataUtil.checkNumbers(rackId);
        if (materialId != null) DataUtil.checkNumbers(materialId);
        if (recordId != null) DataUtil.checkNumbers(recordId);
        return rackMapper.getMaterialList(rackId, recordId, materialId);
    }

    /**
     * 获取该货架中的库存物料
     * @param rackId        货架ID
     * @return list
     */
    @Override
    public List<Material> getMaterialList(String rackId) {
        DataUtil.checkNumbers(rackId);
        return getMaterialList(rackId, null, null);
    }


    /**
     * 多值查询
     * @param wrapper 条件
     * @return list
     */
    @Override
    public List<MaterialRack> queryList(Wrapper<MaterialRack> wrapper) {
        List<MaterialRack> list = list(wrapper);
        list.forEach(materialRack -> materialRack.setMaterialList(getMaterialList(materialRack.getRackId())));
        return list;
    }

    /**
     * 查询指定的物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return list
     */
    @Override
    public Material getMaterial(String rackId, String recordId, String materialId) {
        DataUtil.checkNumbers(rackId, materialId);
        if (recordId != null) {
            DataUtil.checkNumbers(recordId);
        }
        return rackMapper.getMaterial(rackId, recordId, materialId);
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<MaterialRack> queryByPage(int page, int size) {
        PageObject<MaterialRack> pageObject = new PageObject<>();
        IPage<MaterialRack> iPage = rackMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<Material> queryStockByPage(int page, int size) {
        PageObject<Material> pageObject = new PageObject<>();
        IPage<Material> iPage = rackMapper.selectStockByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }
}
