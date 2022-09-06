package com.lyne.module.manager.material.infrastructure.factories;

import cn.hutool.core.convert.Convert;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.application.service.RackService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

/**
 * 领域实体构建工厂
 * 记录
 *
 * @author lyne
 */
@Component
public class RecordFactory<T extends RecordVo> extends AbstractFactory<T, Registration> {
    private static final Logger log = LoggerFactory.getLogger(RecordFactory.class);
    @Resource
    private FeignMaterialService feignMaterialService;
    @Resource
    private MaterialFactory materialFactory;
    @Resource
    private RackService rackService;
    @Resource
    private RackFactory rackFactory;

    @Override
    public void save(T recordVo) {
        MongoDBService.save(recordVo);
    }

    @Override
    public void delete(T recordVo) {
        MongoDBService.remove(recordVo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(Registration t) {
        return (T) get(t.getRegId(), t.getRegSign());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getById(String id) {
        RecordVo one = MongoDBService.findOneById(EnterRecordVo.class, id);
        if (one == null) {
            one = MongoDBService.findOneById(OuterRecordVo.class, id);
        }
        if (one == null) {
            one = constructor(id);
        }
        return (T) one;
    }

    @SuppressWarnings("unchecked")
    public T getById(String id, int sign) {
        RecordVo one;
        switch (sign) {
            case Registration.ENTER:
                one = MongoDBService.findOneById(EnterRecordVo.class, id);
                break;
            case Registration.OUTER:
                one = MongoDBService.findOneById(OuterRecordVo.class, id);
                break;
            default:
                throw new ArgumentException("参数未知错误");
        }
        if (one == null) {
            one = constructor(id);
        }
        if (one == null) {
            return null;
        }
        return (T) one;
    }

    public RecordVo get(String id, Integer sign) {
        RecordVo recordVo = null;
        if (sign == Registration.ENTER) {
            recordVo = MongoDBService.findOneById(EnterRecordVo.class, id);
        } else if (sign == Registration.OUTER) {
            recordVo = MongoDBService.findOneById(OuterRecordVo.class, id);
        }
        if (recordVo == null) {
            recordVo = constructor(id);
        }
        return recordVo;
    }

    /**
     * 构造领域实体
     *
     * @param regId id
     */
    private RecordVo constructor(String regId) {
        R<?> r = feignMaterialService.queryOne("registration", "id", Convert.toStr(regId),
                SecurityConstants.INNER);
        if (r.getCode() == HttpStatus.SUCCESS) {
            Registration registration = ParseUtil.parseJson(r.getData(), Registration.class);
            if (registration != null) {
                RecordVo convert = convert(registration);
                // 显性声明
                if (convert.getSign() == Registration.ENTER) {
                    EnterRecordVo enter = (EnterRecordVo) convert;
                    if (enter.getStatus() >= EnterRecordVo.Status.Recorded.getStatus()) {
                        enter.setExecuteApproval(RecordVo.PASS);
                    }
                    if (enter.getStatus() >= EnterRecordVo.Status.Written.getStatus()) {
                        enter.setApplyApproval(RecordVo.PASS);
                    }
                    MongoDBService.save(enter);
                } else {
                    OuterRecordVo outer = (OuterRecordVo) convert;
                    if (outer.getStatus() >= OuterRecordVo.Status.Review.getStatus()) {
                        outer.setExecuteApproval(RecordVo.PASS);
                    }
                    if (outer.getStatus() >= OuterRecordVo.Status.Worked.getStatus()) {
                        outer.setApplyApproval(RecordVo.PASS);
                    }
                    MongoDBService.save(outer);
                }
                return convert;
            }
        }
        return null;
    }

    /**
     * 同步数据
     */
    @Override
    public void syncData() {
        R<?> r = feignMaterialService.queryList("registration", "all", SecurityConstants.INNER);
        if (r.getCode() == HttpStatus.SUCCESS && r.getData() != null) {
            List<Registration> list = ParseUtil.parseList(r.getData(), Registration.class);
            long total = MongoDBService.getMongoTemplate().estimatedCount(EnterRecordVo.class)
                    + MongoDBService.getMongoTemplate().estimatedCount(OuterRecordVo.class);
            if (list != null && list.size() > total) {
                list.forEach(registration -> {
                    RecordVo convert = convert(registration);
                    try {
                        MongoDBService.getMongoTemplate().insert(convert);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    /**
     * 数据转换
     *
     * @param registration reg
     */
    private RecordVo convert(Registration registration) {
        RecordVo vo;
        switch (registration.getRegSign()) {
            case Registration.ENTER:
                EnterRecordVo enterRecordVo = new EnterRecordVo();
                enterRecordVo.setRecordId(registration.getRegId());
                enterRecordVo.setNumber(registration.getNumber());
                enterRecordVo.setValue(registration.getValue());
                enterRecordVo.setStatus(registration.getStatus());
                enterRecordVo.setCreateBy(registration.getCreateBy());
                enterRecordVo.setUpdateBy(registration.getUpdateBy());
                constructorEnterList(enterRecordVo);
                vo = enterRecordVo;
                break;
            case Registration.OUTER:
                OuterRecordVo outerRecordVo = new OuterRecordVo();
                outerRecordVo.setRecordId(registration.getRegId());
                outerRecordVo.setNumber(registration.getNumber());
                outerRecordVo.setValue(registration.getValue());
                outerRecordVo.setStatus(registration.getStatus());
                outerRecordVo.setCreateBy(registration.getCreateBy());
                outerRecordVo.setUpdateBy(registration.getUpdateBy());
                constructorListOut(outerRecordVo);
                vo = outerRecordVo;
                break;
            default:
                throw new MaterialManagerException("类型错误");
        }
        return vo;
    }

    /**
     * 出库记录
     * 构造所属物料映射
     * 分配具体出库物料
     * @param vo 待参
     */
    public void constructorListOut(OuterRecordVo vo) {
        DataUtil.checkNulls(vo);
        if (vo.getMaterialNumberMap() == null || vo.getMaterialNumberMap().isEmpty()) {
            R<?> res = feignMaterialService.queryList2("registration", "material",
                    Convert.toStr(vo.getRecordId()), SecurityConstants.INNER);
            if (res != null && res.getCode() == HttpStatus.SUCCESS) {
                List<RegMaterial> list = ParseUtil.parseList(res.getData(), RegMaterial.class);
                if (list != null && list.size() > 0) {
                    for (RegMaterial regMaterial : list) {
                        vo.addMaterial(regMaterial.getMaterialId(), regMaterial.getNumber());
                    }
                }
            }
        }
        autoDistributeMaterial(vo);
    }

    /**
     * 自动分配出库记录物料清单
     * @param vo 出库记录
     */
    @Async
    public void autoDistributeMaterial(OuterRecordVo vo) {
        DataUtil.checkNulls(vo);
        if (Registration.Status.INITIAL.getStatus().equals(vo.getStatus())) {
            return;
        }
        // 判断出库记录状态
        if (!OuterRecordVo.Status.Distribution.getStatus().equals(vo.getStatus())
                || !RecordVo.PASS.equals(vo.getApplyApproval())) {
            log.warn("[RecordFactory] 出库记录[{}]尚未完成审批或审批失败, 中止执行出库物料分配", vo.getRecordId());
            return;
        }
        if (vo.getMaterialMap() != null && !vo.getMaterialMap().isEmpty()) {
            log.warn("[RecordFactory] 出库记录[{}]已分配出库物料, 中止执行出库物料分配", vo.getRecordId());
            return;
        }
        Map<String, Long> numberMap = vo.getMaterialNumberMap();
        if (numberMap == null || numberMap.isEmpty()) {
            log.warn("[RecordFactory] 出库记录[{}]尚未记录出库记录或数据未同步, 中止执行出库物料分配", vo.getRecordId());
            return;
        }
        // 分配物料
        numberMap.forEach((materialId, number) -> {
            HashMap<String, Long> map = vo.getMaterialMap().get(materialId);
            if (map == null || map.isEmpty() || map.values().stream().mapToLong(l -> l).sum() < number) {
                // 待分配的库存数
                Long num = number;
                if (num != null && num > 0) {
                    // 随机分配的物料详情
                    List<MaterialInfo> infoList = rackService.getMatInfoForOuter(materialId, num);
                    if (infoList != null && !infoList.isEmpty()) {
                        for (MaterialInfo info : infoList) {
                            if (num == 0L) {
                                break;
                            }
                            if (info.getNumber() >= num) {
                                vo.addMaterialInfo(info, num);
                                break;
                            }
                            vo.addMaterialInfo(info, info.getNumber());
                            num -= info.getNumber();
                        }
                    }
                }
            }
        });
        // 更新待取物料
        HashMap<String, HashMap<String, Long>> materialMap = vo.getMaterialMap();
        materialMap.forEach((materialId, qrcodeMap) -> qrcodeMap.forEach((qrcode, number) -> {
            if (StringUtil.hasText(qrcode)) {
                String[] datas = MaterialInfo.decodeQR(qrcode);
                RackVo rackVo = rackFactory.getById(datas[2]);
                rackVo.addTemporary(qrcode, number);
                rackFactory.save(rackVo);
            }
        }));
        // 更新出入库记录状态
        vo.setStatus(OuterRecordVo.Status.Worked.getStatus());
        MongoDBService.save(vo);
    }

    /**
     * 构造下属数据
     *
     * @param vo 待参
     */
    private void constructorEnterList(EnterRecordVo vo) {
        R<?> res = feignMaterialService.queryList("registration", "material", null,
                Convert.toStr(vo.getRecordId()), SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<RegMaterial> list = ParseUtil.parseList(res.getData(), RegMaterial.class);
            if (list != null && list.size() > 0) {
                list.forEach(regMaterial -> {
                    MaterialVo materialVo = materialFactory.getById(regMaterial.getMaterialId());
                    vo.constructorRecord(materialVo.getMaterialId(), regMaterial.getNumber());
                });
            }
        }
        MongoDBService.save(vo);
    }


    /**
     * 实体转换
     */
    public Registration toRegistration(RecordVo vo) {
        if (vo == null) {
            throw new ArgumentException("vo空值");
        }
        Registration registration = new Registration();
        Integer sign = vo.getSign();
        registration.setRegSign(sign);
        switch (sign) {
            case Registration.ENTER:
                EnterRecordVo enterRecordVo = (EnterRecordVo) vo;
                registration.setRegId(enterRecordVo.getRecordId());
                registration.setNumber(enterRecordVo.getNumber());
                registration.setValue(enterRecordVo.getValue());
                registration.setStatus(enterRecordVo.getStatus());
                registration.setCreateBy(enterRecordVo.getCreateBy());
                registration.setUpdateBy(enterRecordVo.getUpdateBy());
                break;
            case Registration.OUTER:
                OuterRecordVo outerRecordVo = (OuterRecordVo) vo;
                registration.setRegId(outerRecordVo.getRecordId());
                registration.setNumber(outerRecordVo.getNumber());
                registration.setValue(outerRecordVo.getValue());
                registration.setStatus(outerRecordVo.getStatus());
                registration.setCreateBy(outerRecordVo.getCreateBy());
                registration.setUpdateBy(outerRecordVo.getUpdateBy());
                break;
            default:
                throw new ArgumentException("参数异常");
        }
        return registration;
    }
}
