package com.lyne.module.manager.material.domain.record.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.lyne.api.system.service.FeignSystemService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.types.SysAccount;
import com.lyne.common.core.types.SysRole;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.common.core.utils.sm2.Sm2Utils;
import com.lyne.module.manager.material.application.service.MaterialService;
import com.lyne.module.manager.material.application.service.RackService;
import com.lyne.module.manager.material.application.service.RecordService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.infrastructure.domain.EnterMaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import com.lyne.module.manager.material.domain.record.repository.RecordRepository;
import com.lyne.module.manager.material.infrastructure.constant.ServiceConstant;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialInfoFactory;
import com.lyne.module.manager.material.infrastructure.factories.RackFactory;
import com.lyne.module.manager.material.infrastructure.factories.RecordFactory;
import com.lyne.module.manager.material.infrastructure.utils.PageUtil;
import com.lyne.module.manager.material.infrastructure.utils.exception.CapacityOutOfBoundException;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import com.lyne.module.manager.material.infrastructure.utils.exception.StateCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 应用层服务实现类
 * 记录相关服务
 *
 * @author lyne
 */

@Service
public class RecordServiceImpl implements RecordService {
    private static final Logger log = LoggerFactory.getLogger(RecordService.class);
    @Resource
    private FeignSystemService feignSystemService;
    @Resource
    private RecordRepository recordRepository;
    @Resource
    private RecordFactory<RecordVo> recordFactory;
    @Resource
    private MaterialFactory materialFactory;
    @Resource
    private MaterialInfoFactory materialInfoFactory;
    @Resource
    private RackService rackService;
    @Resource
    private RackFactory rackFactory;
    @Resource
    private MaterialService materialService;

    // ================
    //      修改操作
    // ================


    /**
     * 生成入库记录
     * @param registration 前置数据
     */
    @Override
    public String generateEnter(Registration registration) {
        Assert.notNull(registration, "null值异常");
        return recordRepository.insertSync(registration);
    }

    /**
     * 生成出库记录
     * @param registration 初始数据
     * @return registrationId
     */
    @Override
    public String generateOuter(Registration registration, Map<String, String> map) {
        Assert.notNull(registration, "null值异常");
        // 库存检查
        if (!map.isEmpty()) {
            map.forEach((materialId, number) -> {
                DataUtil.checkNumbers(materialId, number);
                if (materialService.queryCapacity(materialId) < Convert.toLong(number)) {
                    throw new CapacityOutOfBoundException("该出库申请单中存在物料库存不足，请重新检查");
                }
            });
        }
        // 出库记录生成
        registration.setRegSign(Registration.OUTER);
        String recordId = recordRepository.insertSync(registration);
        if (recordId != null && !map.isEmpty()) {
            OuterRecordVo outer = OuterRecordVo.getOuter(recordFactory.getById(recordId, Registration.OUTER));
            map.forEach((key, value) -> {
                DataUtil.checkNumbers(key, value);
                outer.addMaterial(key, Convert.toLong(value));
            });
            outer.setStatus(OuterRecordVo.Status.Review.getStatus());
            recordRepository.save(outer);
            return recordId;
        }
        return null;
    }

    /**
     * 关闭记录
     * @param recordId 记录id
     */
    @Override
    public void closeRecord(String recordId, String accountId) {
        DataUtil.checkNumbers(recordId);
        RecordVo vo = recordFactory.getById(recordId);
        Assert.notNull(vo, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        // 权限判断
        SysAccount account = getAccount(accountId);
        if (vo.getCreateBy() == null || !account.getUsername().equals(vo.getCreateBy())) {
            List<SysRole> roles = account.getRoles();
            if (roles == null || roles.size() == 0) {
                throw new MaterialManagerException("用户无权限关闭记录");
            }
            if (roles.stream().map(SysRole::getRoleId).anyMatch(roleId -> Convert.toLong(roleId) > 3)) {
                throw new MaterialManagerException("用户无权限关闭记录");
            }
        }
        if (vo.getStatus() >= Registration.Status.DONE.getStatus()
                || vo.getStatus() < Registration.Status.INITIAL.getStatus()) {
            return;
        }
        Integer status = vo.getStatus();
        vo.setVoStatus(Registration.Status.CLOSE);
        switch (vo.getSign()) {
            case Registration.ENTER:
                EnterRecordVo enter = EnterRecordVo.getEnter(vo);
                MongoDBService.save(enter);
                return;
            case Registration.OUTER:
                OuterRecordVo outer = OuterRecordVo.getOuter(vo);
                if (OuterRecordVo.Status.Worked.getStatus().equals(status)) {
                    rollBackMaterial(outer);
                }
                if (StringUtil.hasText(outer.getSm2key())) {
                    outer.setSm2key("");
                    outer.setProof("");
                }
                MongoDBService.save(outer);
            default:
        }
    }

    public void rollBackMaterial(OuterRecordVo outer) {
        Assert.notNull(outer, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        // 回退已分配物料
        if (outer.getMaterialMap().isEmpty()) {
            return;
        }
        outer.getMaterialMap().forEach((materialId, qrMap) -> qrMap.forEach((qrcode, number) -> {
                    if (StringUtil.hasText(qrcode) && number != null && number > 0) {
                        MaterialInfo info = materialInfoFactory.getByQrCode(qrcode);
                        if (info != null && info.getRackId() != null) {
                            RackVo rackVo = rackFactory.getById(info.getRackId());
                            HashMap<String, Long> temporaryQueue = rackVo.getTemporaryQueue();
                            if (temporaryQueue.size() > 0) {
                                temporaryQueue.remove(qrcode);
                            }
                            rackVo.setTemporaryQueue(temporaryQueue);
                            rackFactory.save(rackVo);
                        }
                    }
                }
        ));
        outer.setMaterialMap(new HashMap<>());
        MongoDBService.save(outer);
    }

    /**
     * 出入库申请审批
     * @param recordId      入库记录
     * @param result        审批结果  1 通过 | 2 不通过
     * @param operation     操作类型 execute 申请审批 | apply 执行审批
     * @param accountId      操作人 financial 财务主管 | warehouse 仓库管理
     * @return boolean
     */
    @Override
    public boolean approval(String recordId, Integer result, String operation, String accountId) {
        DataUtil.checkNumbers(result);
        if (RecordVo.PASS.equals(result) || RecordVo.REFUSE.equals(result)) {
            RecordVo vo = recordFactory.getById(recordId);
            Assert.notNull(vo, ServiceConstant.RECORD_NOT_FOUND_RECORD);
            // 获取用户权限信息
            SysAccount sysAccount = getAccount(accountId);
            List<SysRole> roles = sysAccount.getRoles();
            String role = "";
            if (roles != null && roles.size() > 0) {
                List<String> collect = roles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    throw new MaterialManagerException("无操作权限");
                }
                if (collect.stream().anyMatch(name -> name.equals("root"))) {
                    role = "root";
                } else if (collect.stream().anyMatch(name -> name.equals("financial"))) {
                    role = "financial";
                } else if (collect.stream().anyMatch(name -> name.equals("warehouse"))) {
                    role = "warehouse";
                }
            }
            boolean res = vo.setApproval(result, operation, role, sysAccount.getUsername());
            // 出库记录后续处理
            if (res && vo.getSign() == Registration.OUTER) {
                OuterRecordVo outer = OuterRecordVo.getOuter(vo);
                // 申请通过后进自动分配物料
                if (outer.getMaterialMap().isEmpty() && RecordVo.PASS.equals(outer.getExecuteApproval())
                        && RecordVo.PASS.equals(outer.getApplyApproval())) {
                    recordFactory.autoDistributeMaterial(outer);
                }
            }
            // 入库记录后续处理
            if (res && vo.getSign() == Registration.ENTER) {
                EnterRecordVo enter = EnterRecordVo.getEnter(vo);
                recordFactory.save(enter);
            }
            // 数据同步
            recordRepository.save(vo);
            return res;
        }
        return false;
    }

    /**
     * 查询出入库申请
     * 1. 查询创建者为自己的申请单
     * 2. 查询所拥有角色待审批的申请单
     * 返回出入库记录ID及状态
     * @param accountId 用户ID
     * @return map
     */
    @Override
    public Map<String, String> queryApproval(String accountId) {
        SysAccount account = getAccount(accountId);
        DataUtil.checkNulls(account.getUsername());
        Query query = Query.query(Criteria.where("createBy").is(account.getUsername()));
        List<EnterRecordVo> enterList = MongoDBService.find(EnterRecordVo.class, query);
        List<OuterRecordVo> outerList = MongoDBService.find(OuterRecordVo.class, query);
        Map<String, String> map = new HashMap<>(getEnterRecordStateMap(enterList));
        map.putAll(getOuterRecordStateMap(outerList));
        List<SysRole> roles = account.getRoles();
        if (roles.size() > 0) {
            List<String> roleNameList = roles.stream()
                    .map(SysRole::getRoleName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for (String roleName : roleNameList) {
                if ("root".equals(roleName) || "financial".equals(roleName)) {
                    Query status = Query.query(Criteria.where("status").is(Registration.Status.Review.getStatus()));
                    enterList = MongoDBService.find(EnterRecordVo.class, status);
                    outerList = MongoDBService.find(OuterRecordVo.class, status);
                    map.putAll(getEnterRecordStateMap(enterList));
                    map.putAll(getOuterRecordStateMap(outerList));
                }
                if ("root".equals(roleName) || "warehouse".equals(roleName)) {
                    Query status = Query.query(Criteria.where("status").is(Registration.Status.WORKED.getStatus()));
                    enterList = MongoDBService.find(EnterRecordVo.class, status);
                    outerList = MongoDBService.find(OuterRecordVo.class, status);
                    map.putAll(getEnterRecordStateMap(enterList));
                    map.putAll(getOuterRecordStateMap(outerList));
                }
            }
        }
        return map;
    }

    /**
     * 入库记录登记物料
     * 绑定待入库物料数据
     * <p>非持久化操作</p>
     * @param recordId   记录Id
     * @param materialId 物料id
     * @param number     物料数量
     */
    @Override
    public void enterRecord(String recordId, String materialId, Long number, String operator) {
        EnterRecordVo enter = EnterRecordVo.getEnter(recordFactory.get(recordId, Registration.ENTER));
        if (enter.getStatus() <= Registration.Status.Review.getStatus()
                || Objects.equals(enter.getStatus(), Registration.Status.DONE.getStatus())) {
            throw new StateCheckException("该记录尚未审核或已取消");
        }
        if (EnterRecordVo.Status.Written.getStatus().equals(enter.getStatus())) {
            throw new StateCheckException("该记录正在入库中，若需要请联系管理员处理");
        }
        Assert.notNull(materialFactory.getById(materialId), "该物料不存在");
        // 更新记录状态
        enter.isWorked(EnterRecordVo.Status.Recorded);
        enter.addMaterialVoList(materialId);
        enter.addMatNumber(materialId, number, true);
        enter.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
        enter.totalNumber();
        enter.setValue(getTotalValue(enter));
        MongoDBService.save(enter);
        log.info("[RecordService] 入库记录[{}]记录物料[{}]成功: 记录物料数量：{}", recordId, materialId, number);
    }

    /**
     * 物料正式入库
     * 1. 将入库记录中的物料入库至货架
     * 2. 入库记录更新
     * 3. 入库成功生成或更新物料详情
     * 4. 依据降序存入,当货架容量不足时跳入下一个物料
     * @param recordId 入库记录ID
     */
    @Override
    public Map<String, List<String>> enterSaveRecord(String recordId, String operator) {
        DataUtil.checkNumbers(recordId);
        EnterRecordVo enter = EnterRecordVo.getEnter(recordFactory.get(recordId, Registration.ENTER));
        Map<String, List<String>> result = new HashMap<>();
        // 检查记录状态
        enterSaveStateCheck(enter);
        // 待入库物料
        List<String> list = enter.getMaterialList();
        if (list.size() == 0) {
            return result;
        }
        // 待入库物料数
        Map<String, Long> numberMap = enter.getNumberMap();
        for (String materialId : list) {
            // 该物料待入库数
            Long num = numberMap.get(materialId);
            if (num == null || num <= 0) {
                continue;
            }
            List<String> qrcodeList = new ArrayList<>();
            // 包含该物料的货架先行入库
            List<EnterMaterialVo> voList = rackListStore(enter, rackService.getRackForMaterial(materialId),
                    materialId, operator);
            num = voList.isEmpty() ? num : voList.stream().mapToLong(EnterMaterialVo::getRemain).sum();
            if (!voList.isEmpty()) {
                qrcodeList.addAll(voList.stream().map(EnterMaterialVo::getQrcode).collect(Collectors.toList()));
            }
            // 执行后剩余待入库数数判断
            if (num <= 0) {
                result.put(materialId, qrcodeList);
                continue;
            }
            // 使用所有的货架数据遍历入库
            voList = rackListStore(enter, MongoDBService.findAll(RackVo.class), materialId, operator);
            // 剩余未入库数
            num = voList.isEmpty() ? num : voList.stream().mapToLong(EnterMaterialVo::getRemain).sum();
            if (!voList.isEmpty()) {
                qrcodeList.addAll(voList.stream().map(EnterMaterialVo::getQrcode).collect(Collectors.toList()));
            }
            if (num > 0) {
                log.error("[RecordService] 系统库存容量不足, 请联系管理员处理");
            }
            result.put(materialId, qrcodeList);
            MongoDBService.save(enter);
        }
        // 全部存入后自动更新完成状态
        if (EnterRecordVo.Status.Written.getStatus().equals(enter.getStatus())
                && enter.getNumberMap().values().stream().mapToLong(l -> l).sum() == 0) {
            enter.setStatus(Registration.Status.DONE.getStatus());
        }
        enter.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
        recordRepository.save(enter);
        return result;
    }

    private List<EnterMaterialVo> rackListStore(EnterRecordVo enter, List<RackVo> list, String materialId,
                                                String operator) {
        List<EnterMaterialVo> voList = new ArrayList<>();
        if (list == null || list.size() <= 0) {
            return voList;
        }
        RackVo rackVo = null;
        while (list.size() > 0) {
            if (rackVo == null || rackVo.getCapacity() <= 0) {
                rackVo = list.remove(0);
            }
            // 剩余未存入数
            EnterMaterialVo vo = enterSaveRecord(enter, rackVo.getRackId(), materialId, operator);
            voList.add(vo);
            if (vo.getRemain() == 0) {
                break;
            }
        }
        return voList;
    }

    /**
     * 物料正式入库
     * 将指定的物料存入指定的货架中
     * 做首次写入判断
     * 当货架容量不足时, 存入货架可容纳最大值, 并返回剩余未入库数量
     * 正式写入, 修改物料详情
     * @param recordId      入库记录Id
     * @param rackId        货架Id
     * @param materialId    物料Id
     * @return number       剩余值 全部存入时返回0
     */
    @Override
    public EnterMaterialVo enterSaveRecord(String recordId, String rackId, String materialId, String operator) {
        DataUtil.checkNumbers(recordId, rackId, materialId);
        EnterRecordVo enter = EnterRecordVo.getEnter(recordFactory.get(recordId, Registration.ENTER));
        return enterSaveRecord(enter, rackId, materialId, operator);
    }

    @Override
    public EnterMaterialVo enterSaveRecord(String recordId, String rackId, String materialId, Long number, String operator) {
        DataUtil.checkNumbers(recordId, rackId, materialId);
        EnterRecordVo enter = EnterRecordVo.getEnter(recordFactory.get(recordId, Registration.ENTER));
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        MaterialVo materialVo = materialFactory.getById(materialId);
        Assert.notNull(materialVo, ServiceConstant.MATERIAL_NOT_FOUND);
        Map<String, Long> numberMap = enter.getNumberMap();
        if (numberMap.isEmpty()) {
            throw new MaterialManagerException("该记录未登记任何物料");
        }
        // 获取待入库物料数量
        Long num = enter.getNumberMap().get(materialId);
        EnterMaterialVo enterMaterialVo;
        if (num == null || num <= 0) {
            throw new MaterialManagerException("该物料未登记或已入库完毕");
        }
        if (number == null || number <= 0 || number > num) {
            throw new MaterialManagerException("入库数小于0或超过登记物料数,入库数最大不超过：" + num);
        }
        // 实际容量
        Long capacity = rackVo.getCapacity();
        if (capacity == null || capacity >= rackVo.getMaxCapacity()) {
            throw new CapacityOutOfBoundException("该货架容量已满");
        }
        // 可容纳量
        long cap = rackVo.getMaxCapacity() - capacity;
        if (cap < materialVo.getCapacityRatio()) {
            throw new CapacityOutOfBoundException("该货架容量不足，请选择其他货架");
        }
        long saveNumber = number * materialVo.getCapacityRatio() > cap ? cap / materialVo.getCapacityRatio() : number;
        // 剩余入库数
        enterMaterialVo = rackService.stored(rackId, enter.getRecordId(), materialId, saveNumber, operator);
        enter.reduceMatNumber(materialId, saveNumber);
        enterMaterialVo.setRemain(enterMaterialVo.getRemain() + (num - saveNumber));
        log.info("[RecordService] 入库记录[{}]入库物料[{}]成功, 入库数量: {}, 剩余待入库数: {}", enter.getRecordId(),
                materialId, saveNumber, enterMaterialVo.getRemain() + (num - saveNumber));
        // 全部存入后自动更新完成状态
        if (enterMaterialVo.getRemain() != null && enterMaterialVo.getRemain() == 0) {
            if (enter.getNumberMap().values().stream().mapToLong(l -> l).sum() == 0) {
                enter.setStatus(Registration.Status.DONE.getStatus());
            }
        }
        enter.setUpdateBy(operator);
        recordRepository.save(enter);
        return enterMaterialVo;
    }

    public EnterMaterialVo enterSaveRecord(EnterRecordVo enter, String rackId, String materialId, String operator) {
        Assert.notNull(enter, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        DataUtil.checkNumbers(rackId, materialId);
        // 检查记录状态
        enterSaveStateCheck(enter);
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        MaterialVo materialVo = materialFactory.getById(materialId);
        Assert.notNull(materialVo, ServiceConstant.MATERIAL_NOT_FOUND);
        Map<String, Long> numberMap = enter.getNumberMap();
        if (numberMap.isEmpty()) {
            throw new MaterialManagerException("该记录未登记任何物料");
        }
        // 获取待入库物料数量
        Long num = enter.getNumberMap().get(materialId);
        EnterMaterialVo enterMaterialVo = null;
        // 不存在或已记录完
        if (num != null && num > 0) {
            // 实际容量
            Long capacity = rackVo.getCapacity();
            if (capacity == null || capacity >= rackVo.getMaxCapacity()) {
                throw new CapacityOutOfBoundException("该货架容量已满");
            }
            // 可容纳量
            long cap = rackVo.getMaxCapacity() - capacity;
            if (cap < materialVo.getCapacityRatio()) {
                throw new CapacityOutOfBoundException("该货架容量不足，请选择其他货架");
            }
            long saveNum = num * materialVo.getCapacityRatio() > cap ? cap / materialVo.getCapacityRatio() : num;
            // 剩余入库数
            enterMaterialVo = rackService.stored(rackId, enter.getRecordId(), materialId, saveNum, operator);
            enter.reduceMatNumber(materialId, saveNum);
            enterMaterialVo.setRemain(enterMaterialVo.getRemain() + (num - saveNum));
            log.info("[RecordService] 入库记录[{}]入库物料[{}]成功, 入库数量: {}, 剩余待入库数: {}", enter.getRecordId(),
                    materialId, saveNum, enterMaterialVo.getRemain() + (num - saveNum));
        }
        // 全部存入后自动更新完成状态
        if (enterMaterialVo != null && enterMaterialVo.getRemain() != null && enterMaterialVo.getRemain() == 0) {
            if (enter.getNumberMap().values().stream().mapToLong(l -> l).sum() == 0) {
                enter.setStatus(Registration.Status.DONE.getStatus());
            }
        }
        if (enterMaterialVo == null) {
            throw new MaterialManagerException("该记录已入库该物料或未查询到该登记物料");
        }
        enter.setUpdateBy(operator);
        recordRepository.save(enter);
        return enterMaterialVo;
    }

    private void enterSaveStateCheck(EnterRecordVo enter) {
        // 是否通过审核
        if (!RecordVo.PASS.equals(enter.getApplyApproval())) {
            throw new StateCheckException("该入库记录执行审核尚未通过");
        }
        // 状态判断
        enter.isWorked(EnterRecordVo.Status.Written);
    }

    /**
     * 出库记录 物料出库执行
     * 核销待核销物料数据
     * @param proof     出库凭证
     * @param recordId   记录Id
     * @param materialId 物料id
     * @param qrCode     物料二维码
     * @param number     数量
     */
    @Override
    public void outRecord(String proof, String recordId, String materialId, String qrCode,
                          Long number, String operator) {
        DataUtil.checkNulls(recordId, proof, materialId, qrCode, number);
        // 出库记录核销代取
        OuterRecordVo outer = (OuterRecordVo) recordFactory.get(recordId, Registration.OUTER);
        Assert.notNull(outer, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        // 判断记录状态
        if (!OuterRecordVo.Status.Worked.getStatus().equals(outer.getStatus())) {
            throw new StateCheckException("该出库记录已完成或尚未审核分配物料");
        }
        // 判断凭证
        if (!verifyProof(proof, outer)) {
            throw new MaterialManagerException("出库凭证验证失败");
        }
        // 检查值
        // 获取对应<qrcode, 数量>映射
        HashMap<String, Long> numberMap = outer.getMaterialMap().get(materialId);
        if (numberMap == null || numberMap.isEmpty()) {
            throw new ArgumentException("未查询到该物料相关记录");
        }
        // 待取总数
        Long num = numberMap.get(qrCode);
        if (num == null || num <= 0) {
            throw new MaterialManagerException("该物料已全部出库");
        }
        // 出库处理
        MaterialInfo info = materialInfoFactory.getByQrCode(qrCode);
        if (info != null && info.getNumber() != null) {
            if (info.getNumber() < number) {
                throw new CapacityOutOfBoundException("该物料货架库存不足");
            }
            // 货架临时队列验证
            RackVo rackVo = rackFactory.getById(info.getRackId());
            HashMap<String, Long> temporaryQueue = rackVo.getTemporaryQueue();
            if (temporaryQueue == null || temporaryQueue.isEmpty()) {
                throw new CapacityOutOfBoundException("该处货架出库队列为空");
            }
            Long temporaryNumber = temporaryQueue.get(qrCode);
            if (temporaryNumber == null || temporaryNumber < number) {
                throw new CapacityOutOfBoundException("该处货架出库队列对应物料出库数量不足");
            }

            // 记录待取物料更新
            outer.deductMatNumber(materialId, qrCode, number);
            // 货架出库
            rackService.takeOut(qrCode, number, operator);
            // 是否全部取出
            long sum = outer.getMaterialMap().values().stream()
                    .mapToLong(qrMap -> qrMap.values().stream().
                            filter(Objects::nonNull).mapToLong(l -> l).sum())
                    .sum();
            if (sum == 0) {
                outer.setStatus(Registration.Status.DONE.getStatus());
            }
            outer.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
            if (Objects.equals(Registration.Status.DONE.getStatus(), outer.getStatus())
                    && StringUtil.hasText(outer.getSm2key())) {
                outer.setSm2key("");
                outer.setProof("");
            }
            recordRepository.save(outer);
            log.info("[RecordService] 出库记录[{}]: 出库物料[{}]成功:, 出库数量:{}", outer.getRecordId(), materialId, number);
        }
    }

    /**
     * 出库记录 物料出库执行
     * 通过出库记录的出库凭证及物料二维码信息核销物料
     * 核销成功删除出库记录对应信息先对减少
     * @param recordId  出库记录Id
     * @param proof  出库凭证
     * @param qrCode 二维码值
     */
    @Override
    public void outRecord(String recordId, String proof, String qrCode, String operator) {
        DataUtil.checkNumbers(recordId);
        OuterRecordVo outer = OuterRecordVo.getOuter(recordFactory.get(recordId, Registration.OUTER));
        // 优先检查是否存在且正确
        if (!verifyQrCode(outer, qrCode)) {
            throw new MaterialManagerException("物料二维码验证失败");
        }
        String[] datas = MaterialInfo.decodeQR(qrCode);
        // 获取待取物料中待取数量信息
        HashMap<String, Long> map = outer.getMaterialMap().get(datas[1]);
        if (map == null || map.isEmpty()) {
            throw new MaterialManagerException("未查询到该物料相关记录");
        }
        outRecord(proof, recordId, datas[1], qrCode, map.get(qrCode), operator);
    }


    /**
     * 验证二维码数据
     * 1.记录中是否存在该物料
     * 2.对应物料是否已记录
     * @param outer  出库记录
     * @param qrcode 二维码数据
     * @return boolean
     */
    @Override
    public boolean verifyQrCode(OuterRecordVo outer, String qrcode) {
        DataUtil.checkNulls(outer, qrcode);
        try {
            String[] split = MaterialInfo.decodeQR(qrcode);
            if (materialFactory.getById(split[1]) == null) {
                return false;
            }
            HashMap<String, Long> map = outer.getMaterialMap().get(split[1]);
            if (map == null || map.size() <= 0) {
                return false;
            }
            return map.get(qrcode) != null;
        } catch (RuntimeException e) {
            log.warn("[RecordService]  物料二维码解析失败, 解析数据: {}", qrcode);
            return false;
        }
    }

    /**
     * 生成出库凭证
     * @param outerRecordVo vo
     * @return proof
     */
    private String generateProof(OuterRecordVo outerRecordVo) {
        if (outerRecordVo.getStatus() < Registration.Status.INITIAL.getStatus()
                || outerRecordVo.getStatus() >= Registration.Status.DONE.getStatus()) {
            throw new MaterialManagerException("该记录已结束");
        }
        String sm2key = outerRecordVo.getSm2key();
        Sm2Utils.keys key = null;
        if (!StringUtil.isEmpty(sm2key)) {
            key = MongoDBService.findOne(Sm2Utils.keys.class,
                    Query.query(Criteria.where("_id").is(sm2key)), "keys");
        }
        if (key == null) {
            key = Sm2Utils.getKeyStr();
            MongoDBService.save(key, "keys");
            outerRecordVo.setSm2key(key.getId());
        }
        String encode = Sm2Utils.encode(outerRecordVo.getRecordId() + "", key.getSM2());
        outerRecordVo.setProof(encode);
        MongoDBService.save(outerRecordVo);
        log.info("[RecordService] 出库记录[{}]: 生成出库凭证成功", outerRecordVo.getRecordId());
        return encode;
    }

    /**
     * 验证出库凭证
     * @param proof     出库凭证
     * @param vo        出库记录
     * @return boolean
     * 验证成功则返回true
     */
    @Override
    public boolean verifyProof(String proof, OuterRecordVo vo) {
        DataUtil.checkNulls(proof, vo, vo.getRecordId());
        Sm2Utils.keys one = MongoDBService.findOneById(Sm2Utils.keys.class, vo.getSm2key());
        try {
            if (one != null && StringUtil.hasText(vo.getProof())) {
                String decode = Sm2Utils.decode(proof, one.getSM2());
                return StringUtil.hasText(decode)
                        && vo.getProof().equals(proof)
                        && decode.equals(Convert.toStr(vo.getRecordId()));
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    // ================
    //      查询操作
    // ================

    /**
     * 获取出库凭证
     * 当出库凭证不存在时生成凭证
     * @param recordId vo
     * @return 出库凭证
     */
    @Override
    public String getProof(String recordId) {
        DataUtil.checkNulls(recordId);
        OuterRecordVo outer = OuterRecordVo.getOuter(recordFactory.get(recordId, Registration.OUTER));
        Assert.notNull(outer, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        if (!OuterRecordVo.Status.Worked.getStatus().equals(outer.getStatus())) {
            throw new StateCheckException("该记录状态不是出库中, 无法获取出库凭证");
        }
        if (StringUtil.isEmpty(outer.getProof())) {
            generateProof(outer);
        }
        String proof = outer.getProof();
        if (StringUtil.isEmpty(proof)) {
            return generateProof(outer);
        }
        return outer.getProof();
    }


    /**
     * 获取责任人
     * @param recordId 记录ID
     * @return 最后修改者
     */
    @Override
    public String getVoucher(String recordId) {
        RecordVo vo = recordFactory.getById(recordId);
        Assert.notNull(vo, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        return vo.getCreateBy();
    }

    /**
     * 获取出库记录未出库的对应物料存储地址
     * @param recordId outerId
     * @return 物料地址
     */
    @Override
    public Map<String, List<String>> getLocations(String recordId) {
        DataUtil.checkNulls(recordId);
        OuterRecordVo outer = OuterRecordVo.getOuter(recordFactory.get(recordId, Registration.OUTER));
        Assert.notNull(outer, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        Map<String, List<String>> map = new HashMap<>(8);
        if (outer.getMaterialMap() != null && outer.getMaterialMap().size() > 0) {
            outer.getMaterialMap().forEach((materialId, qrcodeMap) -> {
                if (qrcodeMap != null && qrcodeMap.size() > 0) {
                    List<String> collect = qrcodeMap.keySet().stream()
                            .filter(qrcode -> qrcodeMap.get(qrcode) != null && qrcodeMap.get(qrcode) > 0)
                            .map(qrcode -> materialInfoFactory.getByQrCode(qrcode))
                            .map(MaterialInfo::getLocation)
                            .collect(Collectors.toList());
                    map.put(materialId, collect);
                }
            });
        }
        return map;
    }

    /**
     * 获取出库记录所有待出库的物料的二维码值
     * @param recordId 出库记录id
     * @return list 二维码集合
     */
    @Override
    public Map<String, List<String>> getAllQrCodes(String recordId) {
        DataUtil.checkNulls(recordId);
        OuterRecordVo record = (OuterRecordVo) recordFactory.get(recordId, Registration.OUTER);
        Assert.notNull(record, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        Map<String, List<String>> map = new HashMap<>(16);
        //遍历映射
        HashMap<String, HashMap<String, Long>> materialMap = record.getMaterialMap();
        if (materialMap.size() > 0) {
            materialMap.forEach((materialId, qrMap) -> {
                List<String> collect = qrMap.keySet().stream()
                        .filter(qrcode -> qrMap.get(qrcode) != null && qrMap.get(qrcode) > 0)
                        .collect(Collectors.toList());
                map.put(materialId, collect);
            });
        }
        return map;
    }

    /**
     * 根据出库记录获取对应物料二维码值
     * @param recordId   出库记录id
     * @param materialId 物料id
     * @return list
     */

    @Override
    public List<String> getQrCodes(String recordId, String materialId) {
        DataUtil.checkNulls(recordId);
        OuterRecordVo record = (OuterRecordVo) recordFactory.get(recordId, Registration.OUTER);
        Assert.notNull(record, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        return new ArrayList<>(record.getMaterialMap().get(materialId).keySet());
    }

    /**
     * 计算记录中物料总价
     * @param vo vo
     * @return prices
     */
    @Override
    public Double getTotalValue(RecordVo vo) {
        DataUtil.checkNulls(vo);
        return recordRepository.getTotalValue(vo);
    }


    /**
     * 查询出入库记录状态
     * @param recordId 记录ID
     * @return 记录装填
     */
    @Override
    public String queryStatue(String recordId) {
        DataUtil.checkNumbers(recordId);
        RecordVo vo = recordFactory.getById(recordId);
        Assert.notNull(vo, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        return Registration.ENTER == vo.getSign() ? EnterRecordVo.getEnter(vo).getRecordStatus()
                : OuterRecordVo.getOuter(vo).getRecordStatus();
    }

    /**
     * 出入库记录单值查询
     * 入库记录标识: enter | 0 <br/>
     * 出库记录标识: outer | 1 <br/>
     * @param recordId  记录ID
     * @param sign      记录类型
     * @return vo
     */
    @Override
    public RecordVo queryOne(String recordId, int sign) {
        DataUtil.checkNumbers(recordId);
        return recordFactory.get(recordId, sign);
    }

    /**
     * 多值分页查询 <br/>
     * 入库记录标识: enter | 0 <br/>
     * 出库记录标识: outer | 1 <br/>
     * @param sign 记录类型
     * @param size 页大小
     * @param page 页序号
     * @param arg1 条件参数一
     * @param arg2 条件参数二
     * @return map
     */
    @Override
    public PageObject<?> queryList(int sign, Integer size, Integer page, String arg1, String arg2) {
        recordFactory.syncData();
        PageImpl<?> voPage;
        if (StringUtil.hasText(arg1) && StringUtil.hasText(arg2)) {
            Criteria criteria = Criteria.where(arg1).is(arg2);
            if ("createBy".equals(arg1)) {
                criteria = Criteria.where(arg1).regex(".*?" + arg2 + ".*?");
            }
            if ("recordId".equals(arg1)) {
                criteria = Criteria.where(arg1).regex(".*?" + arg2 + ".*?");
            }
            if (sign == Registration.ENTER) {
                voPage = MongoDBService.page(EnterRecordVo.class, criteria, size, page);
            } else {
                voPage = MongoDBService.page(OuterRecordVo.class, criteria, size, page);
            }
            return PageUtil.getPage(voPage);
        }
        if (sign == Registration.ENTER) {
            voPage = MongoDBService.page(EnterRecordVo.class, size, page);
        } else {
            voPage = MongoDBService.page(OuterRecordVo.class, size, page);
        }
        return PageUtil.getPage(voPage);
    }


    /**
     * 修改出入库信息
     * @param registration  修改实体
     * @return boolean
     */
    @Override
    public boolean updateRecord(Registration registration, String operator) {
        DataUtil.checkNulls(registration, registration.getRegId(), registration.getRegSign());
        RecordVo vo = recordFactory.getById(registration.getRegId());
        Assert.notNull(vo, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        vo.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
        if (vo.getSign() == Registration.ENTER) {
            EnterRecordVo enter = EnterRecordVo.getEnter(vo);
            if (registration.getNumber() != null) {
                enter.setNumber(registration.getNumber());
            }
            if (registration.getValue() != null) {
                enter.setValue(registration.getValue());
            }
            recordFactory.save(enter);
            return true;
        } else if (vo.getSign() == Registration.OUTER) {
            OuterRecordVo outer = OuterRecordVo.getOuter(vo);
            if (registration.getNumber() != null) {
                outer.setNumber(registration.getNumber());
            }
            if (registration.getValue() != null) {
                outer.setValue(registration.getValue());
            }
            recordFactory.save(outer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除出入库实体
     * @param recordId  出入库ID
     * @param operator  操作人员
     * @return boollean
     */
    @Override
    public boolean deleteRecord(String recordId, String operator) {
        DataUtil.checkNumbers(recordId);
        RecordVo vo = recordFactory.getById(recordId);
        Assert.notNull(vo, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        vo.setUpdateBy(operator);
        recordRepository.save(vo);
        recordRepository.delete(vo);
        return true;
    }

    /**
     * 查询出库物料清单
     * @param recordId 出入库ID
     */
    @Override
    public Map<String, Map<Long, String>> queryOuterMaterialList(String recordId) {
        DataUtil.checkNulls(recordId);
        OuterRecordVo outer = OuterRecordVo.getOuter(recordFactory.get(recordId, Registration.OUTER));
        Assert.notNull(outer, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        HashMap<String, Map<Long, String>> map = new HashMap<>();
        HashMap<String, HashMap<String, Long>> materialMap = outer.getMaterialMap();
        if (materialMap.size() > 0) {
            materialMap.forEach((Key, qrMap) -> {
                Map<Long, String> locationMap = new HashMap<>();
                qrMap.forEach((qrcode, number) -> {
                    MaterialInfo info = materialInfoFactory.getByQrCode(qrcode);
                    if (info != null) {
                        locationMap.put(number, info.getLocation());
                    }
                    map.put(Key, locationMap);
                });
            });
        }
        return map;
    }

    /**
     * 查询入库记录待入库物料清单
     *
     * @param recordId 出入库编号
     */
    @Override
    public List<Map<String, Object>> queryEnterMaterialList(String recordId) {
        DataUtil.checkNumbers(recordId);
        EnterRecordVo enter = EnterRecordVo.getEnter(recordFactory.getById(recordId));
        Assert.notNull(enter, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        if (!Registration.Status.WORKED_SECOND.getStatus().equals(enter.getStatus())) {
            throw new MaterialManagerException("该记录尚未审批或已入库完毕，无法查询");
        }
        return getDataList(enter.getNumberMap());
    }


    /**
     * 查询入库记录已登记物料清单
     * @param recordId 出入库编号
     */
    @Override
    public List<Map<String, Object>> queryEnterBindList(String recordId) {
        DataUtil.checkNumbers(recordId);
        EnterRecordVo enter = EnterRecordVo.getEnter(recordFactory.getById(recordId));
        Assert.notNull(enter, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        if (Registration.Status.INITIAL.getStatus().equals(enter.getStatus())) {
            throw new MaterialManagerException("该记录尚未审批，无法查询");
        }
        return getDataList(enter.getMatNumber());
    }


    private List<Map<String, Object>> getDataList(Map<String, Long> map) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        if (map != null && map.size() > 0) {
            map.forEach((id, number) -> {
                MaterialVo materialVo = materialFactory.getById(id);
                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put("materialId", id);
                messageMap.put("number", number);
                if (materialVo != null) {
                    messageMap.put("name", materialVo.getMaterialName());
                    messageMap.put("nameZh", materialVo.getMaterialNameZh());
                }
                list.add(messageMap);
            });
        }
        return list;
    }


    /**
     * 获取入库记录状态map
     */
    private Map<String, String> getEnterRecordStateMap(List<EnterRecordVo> list) {
        Map<String, String> map = new HashMap<>();
        if (list.size() > 0) {
            EnterRecordVo enter;
            for (RecordVo vo : list) {
                if (vo != null) {
                    enter = EnterRecordVo.getEnter(vo);
                    map.put(enter.getRecordId(), enter.getRecordStatus());
                }
            }
        }
        return map;
    }

    /**
     * 获取出库记录状态map
     */
    private Map<String, String> getOuterRecordStateMap(List<OuterRecordVo> list) {
        Map<String, String> map = new HashMap<>();
        if (list.size() > 0) {
            OuterRecordVo outer;
            for (RecordVo vo : list) {
                if (vo != null) {
                    outer = OuterRecordVo.getOuter(vo);
                    map.put(outer.getRecordId(), outer.getRecordStatus());
                }
            }
        }
        return map;
    }

    /**
     * 获取用户信息
     * @param accountId 用户ID
     * @return account
     */
    private SysAccount getAccount(String accountId) {
        DataUtil.checkNumbers(accountId);
        R<?> resultR = feignSystemService.queryOne("account", "id", accountId, SecurityConstants.INNER);
        if (resultR == null || resultR.getCode() != HttpStatus.SUCCESS) {
            throw new MaterialManagerException("系统资源服务响应异常");
        }
        SysAccount sysAccount = Convert.convert(SysAccount.class, resultR.getData());
        Assert.notNull(sysAccount, "用户信息不存在");
        return sysAccount;
    }


}
