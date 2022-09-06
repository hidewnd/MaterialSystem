package com.lyne.module.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialRack;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 货架
 *
 * @author lyne
 */
@Repository
public interface MaterialRackMapper extends BaseMapper<MaterialRack> {

    /**
     * 货架绑定物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存入数量
     * @return 影响值
     */
    int bindMaterial(@Param("rackId") String rackId, @Param("recordId") String recordId,
                     @Param("materialId") String materialId, @Param("stock") Long stock);

    /**
     * 货架取消绑定物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存入数量
     * @return 影响值
     */
    int removeMaterial(@Param("rackId") String rackId, @Param("recordId") String recordId,
                       @Param("materialId") String materialId, @Param("stock") Long stock);

    /**
     * 货架修改绑定物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存入数量
     * @return 影响值
     */
    int updateMaterial(@Param("rackId") String rackId, @Param("recordId") String recordId,
                       @Param("materialId") String materialId, @Param("stock") Long stock);


    /**
     * 查询所有货架中所有物料信息
     * 【慎用】
     * @return list
     */
    List<Material> getAllMaterial();

    /**
     * 查询指定的物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return list
     */
    List<Material> getMaterialList(@Param("rackId") String rackId, @Param("recordId") String recordId,
                                   @Param("materialId") String materialId);

    /**
     * 查询指定的物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return list
     */
    Material getMaterial(@Param("rackId") String rackId, @Param("recordId") String recordId,
                         @Param("materialId") String materialId);

    /**
     * 获取指定货架中所有物料ID
     * @param rackId        货架ID
     * @return list
     */
    List<String> getMaterialIds(String rackId);

    /**
     * 获取指定货架的即时可容纳容量值
     * @param rackId        货架ID
     * @return 可容纳容量
     */
    long getActualCapacity(String rackId);

    /**
     * 获取指定货架中指定物料的绑定数量
     * @param rackId        货架ID
     * @param materialId    货架ID
     * @return 绑定数量
     */
    Long getMaterialStock(@Param("rackId") String rackId, @Param("recordId") String recordId, @Param("materialId") String materialId);

    /**
     * 分页查询 货架
     * @param iPage page
     * @return iPage
     */
    IPage<MaterialRack> selectByPage(IPage<MaterialRack> iPage);

    /**
     * 分页查询 物料库存
     * @param iPage page
     * @return iPage
     */
    IPage<Material> selectStockByPage(IPage<Material> iPage);

}
