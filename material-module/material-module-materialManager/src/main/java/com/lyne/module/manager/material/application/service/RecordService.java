package com.lyne.module.manager.material.application.service;

import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Registration;
import com.lyne.module.manager.material.infrastructure.domain.EnterMaterialVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;

import java.util.List;
import java.util.Map;

/**
 * 领域服务
 *
 * @author lyne
 */
public interface RecordService {

    // ================
    //      修改操作
    // ================

    /**
     * 生成入库记录
     *
     * @param registration 初始数据
     * @return registrationId
     */
    String generateEnter(Registration registration);

    /**
     * 生成出库记录
     *
     * @param registration  初始数据
     * @param map           待取物料信息
     * @return registrationId
     */
    String generateOuter(Registration registration, Map<String, String> map);

    /**
     * 关闭记录
     *
     * @param recordId 记录id
     */
    void closeRecord(String recordId, String accountId);

    /**
     * 出入库申请审批
     * @param recordId      入库记录
     * @param result        审批结果  1 通过 | 2 不通过
     * @param operation     操作类型 execute 申请审批 | apply 执行审批
     * @param accountId     操作人 financial 财务主管 | warehouse 仓库管理
     * @return boolean
     */
    boolean approval(String recordId, Integer result, String operation, String accountId);

    /**
     * 入库记录记录物料
     * 绑定待入库物料数据
     * <p>非持久化操作</p>
     * @param recordId   记录Id
     * @param materialId 物料id
     * @param number     物料数量
     */
    void enterRecord(String recordId, String materialId, Long number, String operator);

    /**
     * 物料正式入库
     * 1. 将入库记录中的物料入库至货架
     * 2. 入库记录更新
     * 3. 入库成功生成或更新物料详情
     * 4. 依据降序存入,当货架容量不足时跳入下一个物料
     * @param recordId 入库记录ID
     */
    Map<String, List<String>> enterSaveRecord(String recordId, String operator);

    /**
     * 将指定的物料存入指定的货架中
     * 当货架容量不足时, 存入货架可容纳最大值, 并返回剩余未入库数量
     * <p>持久化操作</p>
     * @param recordId      入库记录Id
     * @param rackId        货架Id
     * @param materialId    物料Id
     * @return number       剩余值 全部存入时返回0
     */
    EnterMaterialVo enterSaveRecord(String recordId, String rackId, String materialId, String operator);

    EnterMaterialVo enterSaveRecord(String recordId, String rackId, String materialId, Long number, String operator);

    /**
     * 出库记录 物料出库执行
     * 核销待核销物料数据
     * @param proof     出库凭证
     * @param recordId   记录Id
     * @param materialId 物料id
     * @param qrcode     物料二维码
     * @param number     数量
     */
    void outRecord(String proof, String recordId, String materialId, String qrcode, Long number, String operator);

    /**
     * 出库记录 物料出库执行
     * 通过出库记录的出库凭证及物料二维码信息核销物料
     * 核销成功删除出库记录对应信息先对减少
     * @param recordId  出库记录Id
     * @param proof  出库凭证
     * @param qrCode 二维码值
     */
    void outRecord(String recordId, String proof, String qrCode, String operator);

    /**
     * 核对二维码数据
     * 1.记录中是否存在该物料
     * 2.对应物料是否已记录
     * @param outer  出库记录
     * @param qrcode 二维码数据
     * @return boolean
     */
    boolean verifyQrCode(OuterRecordVo outer, String qrcode);

    /**
     * 验证出库凭证
     * @param proof     出库凭证
     * @param vo        出库记录
     * @return boolean
     * 验证失败则返回true
     */
    boolean verifyProof(String proof, OuterRecordVo vo);


    // ================
    //      查询操作
    // ================

    /**
     * 获取出库凭证
     * 当出库凭证不存在时生成凭证
     * @param recordId vo
     * @return 出库凭证
     */
    String getProof(String recordId);

    /**
     * 获取责任人
     * @param recordId 记录id
     * @return str
     */
    String getVoucher(String recordId);

    /**
     * 获取出库记录未出库的对应物料存储地址
     * @param recordId outerId
     * @return materials location
     */
    Map<String, List<String>> getLocations(String recordId);

    /**
     * 获取出库记录所有待出库的物料的二维码值
     * @param recordId 出库记录id
     * @return list 二维码集合
     */
    Map<String, List<String>> getAllQrCodes(String recordId);

    /**
     * 根据出库记录获取对应物料二维码值
     *
     * @param recordId   出库记录id
     * @param materialId 物料id
     * @return list
     */
    List<String> getQrCodes(String recordId, String materialId);

    /**
     * 计算记录中物料总价
     * @param vo vo
     * @return prices
     */
    Double getTotalValue(RecordVo vo);

    /**
     * 查询出入库记录状态
     * @param recordId 记录ID
     * @return 记录装填
     */
    String queryStatue(String recordId);

    /**
     * 出入库记录单值查询
     * @param recordId  记录ID
     * @param sign      记录类型
     * @return vo
     */
    RecordVo queryOne(String recordId, int sign);

    /**
     * 多值分页查询 <br/>
     * 入库记录标识: enter | 0 <br/>
     * 出库记录标识: outer | 1 <br/>
     *
     * @param sign 记录类型
     * @param size 页大小
     * @param page 页序号
     * @param arg1 条件参数一
     * @param arg2 条件参数二
     * @return map
     */
    PageObject<?> queryList(int sign, Integer size, Integer page, String arg1, String arg2);

    /**
     * 查询出入库申请
     * 1. 查询创建者为自己的申请单
     * 2. 查询所拥有角色待审批的申请单
     * 返回出入库记录ID及状态
     * @param accountId 用户ID
     * @return map
     */
    Map<String, String> queryApproval(String accountId);

    /**
     * 修改出入库信息
     * @param registration  修改实体
     * @return boolean
     */
    boolean updateRecord(Registration registration, String operator);

    /**
     * 删除出入库实体
     * @param recordId  出入库ID
     * @param operator  操作人员
     * @return boollean
     */
    boolean deleteRecord(String recordId, String operator);

    /**
     * 查询出库物料清单
     * @param recordId 出入库ID
     */
    Map<String, Map<Long, String>> queryOuterMaterialList(String recordId);

    /**
     * 查询入库记录待入库物料清单
     * @param recordId 出入库编号
     */
    List<Map<String, Object>> queryEnterMaterialList(String recordId);

    /**
     * 查询入库记录已登记物料清单
     * @param recordId 出入库编号
     */
    List<Map<String, Object>> queryEnterBindList(String recordId);
}
