package com.lyne.module.material.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.material.exception.MaterialServerException;
import com.lyne.module.material.mapper.MaterialMapper;
import com.lyne.module.material.mapper.RegistrationMapper;
import com.lyne.module.material.service.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * 物料进出库服务
 *
 * @author lyne
 */
@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration>
        implements RegistrationService {

    @Resource
    private RegistrationMapper registrationMapper;
    @Resource
    private MaterialMapper materialMapper;

    //==================
    //      修改操作
    //==================

    /**
     * 记录物料信息
     *
     * @param regId  记录ID
     * @param matId  物料ID
     * @param number 物料数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMaterial(String regId, String matId, Long number) {
        DataUtil.checkNumbers(regId, matId, number);
        Registration registration = getById(regId);
        Assert.notNull(registration, "该记录不存在");
        Assert.notNull(materialMapper.selectById(matId), "该物料不存在");
        return registrationMapper.bindMaterial(regId, matId, number) >= 0;
    }

    /**
     * 记录物料信息
     * @param regMaterial reg
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMaterial(RegMaterial regMaterial) {
        DataUtil.checkNulls(regMaterial, regMaterial.getRegistrationId(), regMaterial.getMaterialId(),
                regMaterial.getNumber());
        return bindMaterial(regMaterial.getRegistrationId(), regMaterial.getMaterialId(), regMaterial.getNumber());
    }

    /**
     * 批量记录物料信息
     * @param list 记录列表
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMaterial(List<RegMaterial> list) {
        DataUtil.checkNulls(list);
        if (list.size() == 0) {
            return false;
        }
        for (RegMaterial regMaterial : list) {
            if (!bindMaterial(regMaterial)) {
                throw new MaterialServerException("批量记录物料信息失败");
            }
        }
        return true;
    }

    /**
     * 移除本记录物料信息
     *
     * @param regId 记录ID
     * @param matId 物料ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBind(String regId, String matId) {
        DataUtil.checkNumbers(regId, matId);
        Registration registration = getById(regId);
        Assert.notNull(registration, "该记录不存在");
        Assert.notNull(materialMapper.selectById(matId), "该物料不存在");
        List<String> ids = registrationMapper.getMaterialIds(regId);
        // 检查记录中是否有记录
        if (ids.stream().noneMatch(aLong -> Objects.equals(aLong, matId))) {
            throw new MaterialServerException("该出入库记录中不存在该物料记录");
        }
        return registrationMapper.removeBind(regId, matId) >= 0;
    }

    /**
     * 修改记录物料绑定信息
     * @param regMaterial 绑定信息
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterial(RegMaterial regMaterial) {
        DataUtil.checkNulls(regMaterial, regMaterial.getMaterialId(), regMaterial.getRegistrationId());
        Registration registration = getById(regMaterial.getRegistrationId());
        Assert.notNull(registration, "该记录不存在");
        Assert.notNull(materialMapper.selectById(regMaterial.getMaterialId()), "该物料不存在");
        List<String> ids = registrationMapper.getMaterialIds(regMaterial.getRegistrationId());
        // 检查记录中是否有记录
        if (ids.stream().noneMatch(aLong -> Objects.equals(aLong, regMaterial.getMaterialId()))) {
            return bindMaterial(regMaterial);
        }
        return registrationMapper.updateRegMaterial(regMaterial) >= 0;
    }

    /**
     * 批量修改出入库记录中物料信息
     * @param list 记录列表
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterial(List<RegMaterial> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (RegMaterial regMaterial : list) {
            if (!updateMaterial(regMaterial)) {
                throw new MaterialServerException("批量更新出入库记录物料信息失败");
            }
        }
        return true;
    }

    /**
     * 新增记录信息
     * @param registration reg
     * @return recordId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveRecord(Registration registration) {
        if (registration.getRegSign() == null) {
            registration.setSign(Registration.Sign.ENTER);
        }
        if (saveOrUpdate(registration)) {
            return registration.getRegId();
        }
        return null;
    }


    //==================
    //      查询操作
    //==================

    /**
     * 获取记录中所有物料
     *
     * @param regId 记录ID
     * @return list
     */
    @Override
    public List<RegMaterial> getMaterialList(String regId) {
        DataUtil.checkNumbers(regId);
        return registrationMapper.getMaterialList(regId);
    }

    @Override
    public List<String> getMaterialIds(String regId) {
        DataUtil.checkNumbers(regId);
        return registrationMapper.getMaterialIds(regId);
    }

    /**
     * 获取记录中指定物料
     *
     * @param regId 记录ID
     * @param matId 物料ID
     * @return material
     */
    @Override
    public RegMaterial getMaterial(String regId, String matId) {
        DataUtil.checkNumbers(regId, matId);
        return registrationMapper.getMaterial(regId, matId);
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<Registration> queryByPage(int page, int size) {
        PageObject<Registration> pageObject = new PageObject<>();
        IPage<Registration> iPage = registrationMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }


}
