package com.lyne.module.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 记录
 *
 * @author lyne
 */
@Repository
public interface RegistrationMapper extends BaseMapper<Registration> {

    /**
     * 分页查询
     * @param iPage page
     * @return list
     */
    IPage<Registration> selectByPage(IPage<Registration> iPage);

    /**
     * 获取记录绑定物料列表
     * @param regId 记录id
     * @return list
     */
    List<RegMaterial> getMaterialList(String regId);

    /**
     * 获取所有绑定信息
     * @return list
     */
    List<RegMaterial> getAllMaterial();

    /**
     * 获取记录绑定的物料详情
     * @param regId 记录Id
     * @param matId 物料Id
     * @return 物料详情
     */
    RegMaterial getMaterial(@Param("regId") String regId, @Param("matId") String matId);

    /**
     * 获取记录的中的物料Id值
     * @param regId 记录Id
     * @return ids
     */
    List<String> getMaterialIds(String regId);

    /**
     * 添加记录绑定物料
     * 若存在则更新，否则插入
     * @param regId 记录Id
     * @param matId 物料Id
     * @param number 数量
     * @return 影响值
     */
    int bindMaterial(@Param("regId") String regId, @Param("matId") String matId, @Param("number") Long number);

    /**
     * 移除绑定信息
     * @param regId 记录Id
     * @param matId 物料Id
     * @return 影响值
     */
    int removeBind(@Param("regId") String regId, @Param("matId") String matId);


    /**
     * 更新记录的物料绑定信息
     * @param regMaterial 绑定信息
     * @return 影响值
     */
    int updateRegMaterial(RegMaterial regMaterial);
}
