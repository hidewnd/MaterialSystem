package com.lyne.module.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;

import java.util.List;

/**
 * 记录服务
 *
 * @author lyne
 */
public interface RegistrationService extends IService<Registration> {
    /**
     * 记录物料信息
     *
     * @param regId     记录ID
     * @param matId     物料ID
     * @param number    物料数量
     * @return boolean
     */
    boolean bindMaterial(String regId,  String matId, Long number);

    /**
     * 记录物料信息
     * @param regMaterial reg
     * @return boolean
     */
    boolean bindMaterial(RegMaterial regMaterial);

    /**
     * 批量记录物料信息
     * @param list 记录列表
     * @return boolean
     */
    boolean bindMaterial(List<RegMaterial> list);

    /**
     * 移除记录物料信息
     * @param regId     记录ID
     * @param matId     物料ID
     * @return boolean
     */
    boolean removeBind(String regId, String matId);

    /**
     * 修改出入库记录中物料信息
     * @param regMaterial 绑定信息
     * @return boolean
     */
    boolean updateMaterial(RegMaterial regMaterial);

    /**
     * 批量修改出入库记录中物料信息
     * @param list 记录列表
     * @return boolean
     */
    boolean updateMaterial(List<RegMaterial> list);

    /**
     * 新增记录信息
     * @param registration reg
     * @return recordId
     */
    String saveRecord(Registration registration);

    /**
     * 获取记录中所有物料
     *
     * @param regId 记录ID
     * @return list
     */
    List<RegMaterial> getMaterialList(String regId);

    /**
     * 获取记录中所有物料
     *
     * @param regId 记录ID
     * @return list
     */
    List<String> getMaterialIds(String regId);

    /**
     * 获取记录中指定物料
     *
     * @param regId 记录ID
     * @param matId 物料ID
     * @return material
     */
    RegMaterial getMaterial(String regId, String matId);

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<Registration> queryByPage(int page, int size);

}
