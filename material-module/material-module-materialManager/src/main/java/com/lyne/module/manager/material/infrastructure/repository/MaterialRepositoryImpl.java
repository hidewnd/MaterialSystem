package com.lyne.module.manager.material.infrastructure.repository;

import com.alibaba.fastjson.JSONObject;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.repository.MaterialRepository;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialInfoFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

/**
 * 持久层
 * 物料信息
 *
 * @author lyne
 */
@Component
public class MaterialRepositoryImpl implements MaterialRepository {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private MaterialFactory materialFactory;
    @Resource
    private MaterialInfoFactory materialInfoFactory;
    @Resource
    private FeignMaterialService feignMaterialService;

    /**
     * 异步添加一个 material
     * @param material material
     */
    @Override
    public void insert(Material material) {
        MqMessage<Material> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_INSERT, material);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL, message);
    }

    /**
     * 同步添加一个 material
     * @param material material
     * @return vo
     */
    @Override
    public MaterialVo insertSync(Material material) {
        if (material != null) {
            R<String> res = feignMaterialService.saveMaterial(material, SecurityConstants.INNER);
            if (res != null && res.getCode() == HttpStatus.SUCCESS) {
                return materialFactory.getById(res.getData());
            }
        }
        return null;
    }

    /**
     * 持久化保存 material
     * @param vo vo
     */

    @Override
    public void save(MaterialVo vo) {
        DataUtil.checkNulls(vo);
        // 数据校验
        vo.dataCheck();
        MongoDBService.save(vo);
        Material material = materialFactory.convertMaterial(vo);
        MqMessage<Material> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_SAVE, material);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL, message);
    }

    /**
     * 持久化删除
     * @param vo vo
     */
    @Override
    public void delete(MaterialVo vo) {
        DataUtil.checkNulls(vo);
        Material material = materialFactory.convertMaterial(vo);
        MqMessage<Material> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_DELETE, material);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL, message);
        MongoDBService.remove(vo);
    }


    /**
     * 持久化物料入库信息
     *
     * @param info material Info
     */
    @Async
    @Override
    public void saveInfo(MaterialInfo info) {
        DataUtil.checkNulls(info);
        MongoDBService.save(info);
        ArrayList<Material> list = new ArrayList<>();
        list.add(materialInfoFactory.convertToMaterial(info));
        MqMessage<String> message = new MqMessage<>(MqMessage.SENDER_LIST, MqMessage.OPERATE_SAVE,
                JSONObject.toJSONString(list));
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_RELATION_MATERIAL, message);
    }

    /**
     * 批量持久化保存物料库存
     * @param list info
     */
    @Async
    @Override
    public void saveInfo(List<MaterialInfo> list) {
        DataUtil.checkNulls(list);
        if (list != null && list.size() > 0) {
            ArrayList<Material> materials = new ArrayList<>();
            for (MaterialInfo info : list) {
                MongoDBService.save(info);
                materials.add(materialInfoFactory.convertToMaterial(info));
            }
            MqMessage<String> message = new MqMessage<>(MqMessage.SENDER_LIST, MqMessage.OPERATE_SAVE,
                    JSONObject.toJSONString(materials));
            rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_RELATION_MATERIAL, message);
        }

    }

    /**
     * 删除物料详情
     * 同步删除物料入库信息
     * @param info info
     */
    @Override
    public void deleteInfo(MaterialInfo info) {
        DataUtil.checkNulls(info);
        Material material = materialInfoFactory.convertToMaterial(info);
        MqMessage<String> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_DELETE, JSONObject.toJSONString(material));
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_RELATION_MATERIAL, message);
        MongoDBService.remove(info);
    }

}
