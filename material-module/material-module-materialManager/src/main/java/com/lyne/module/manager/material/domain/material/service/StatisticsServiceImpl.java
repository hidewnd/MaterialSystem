package com.lyne.module.manager.material.domain.material.service;

import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.types.Registration;
import com.lyne.module.manager.material.application.service.MaterialService;
import com.lyne.module.manager.material.application.service.RackService;
import com.lyne.module.manager.material.application.service.StatisticsService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Resource;

/**
 *
 * @author lyne
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Resource
    private MaterialService materialService;
    @Resource
    private RackService rackService;

    /**
     * 获取库存统计
     */
    @Override
    public Map<String, Long> getStatisticsTotal() {
        HashMap<String, Long> map = new HashMap<>();
        List<MaterialInfo> list = MongoDBService.findAll(MaterialInfo.class);
        map.put("materialTotal", list.stream().mapToLong(MaterialInfo::getNumber).sum());
        long temporary = MongoDBService.findAll(RackVo.class).stream()
                .map(RackVo::getTemporaryQueue)
                .filter(Objects::nonNull)
                .map(HashMap::values)
                .flatMap(Collection::stream)
                .mapToLong(along -> along).sum();
        map.put("temporaryOutTotal", temporary);
        List<EnterRecordVo> enterList = MongoDBService.findAll(EnterRecordVo.class);
        // 待入库物料总数
        long temporaryEnterTotal = 0;
        // 已入库物料总数
        long applyEnterTotal = 0;
        for (EnterRecordVo enter : enterList) {
            if (enter.getStatus() < Registration.Status.WORKED.getStatus()) {
                continue;
            }
            Map<String, Long> matNumber = enter.getMatNumber(); // 登记物料数
            Map<String, Long> numberMap = enter.getNumberMap(); // 待入库物料数
            long temporaryEnter = 0;
            if (numberMap.size() > 0) {
                temporaryEnter += numberMap.values().stream().mapToLong(along -> along).sum();
            }
            if (matNumber.size() > 0) {
                long sum = matNumber.values().stream().mapToLong(along -> along).sum();
                sum -= temporaryEnter;
                applyEnterTotal += sum;
            }
            temporaryEnterTotal += temporaryEnter;
        }
        map.put("temporaryEnterTotal", temporaryEnterTotal);
        map.put("applyEnterTotal", applyEnterTotal);

        List<OuterRecordVo> outerList = MongoDBService.findAll(OuterRecordVo.class);
        long applyOutTotal = 0;
        for (OuterRecordVo outer : outerList) {
            if (outer.getStatus() < Registration.Status.WORKED.getStatus()) {
                continue;
            }
            Map<String, Long> materialNumberMap = outer.getMaterialNumberMap();
            if (materialNumberMap.size() <= 0) {
                continue;
            }
            applyOutTotal += materialNumberMap.values().stream().mapToLong(along -> along).sum();
        }
        applyOutTotal -= temporary;
        map.put("applyOutTotal", applyOutTotal);
        return map;
    }

    /**
     * 统计库存物料类型占比
     */
    @Override
    public Map<String, Object> getStockTypeTotal() {
        Map<String, Object> map = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<Long> numberList = new ArrayList<>();
        List<MaterialTypeVo> typeList = MongoDBService.findAll(MaterialTypeVo.class);
        for (MaterialTypeVo typeVo : typeList) {
            nameList.add(typeVo.getTypeNameZh());
            long total = 0;
            List<MaterialVo> list = MongoDBService.find(MaterialVo.class,
                    Query.query(Criteria.where("typeId").is(typeVo.getTypeId())));
            if (list.size() > 0) {
                for (MaterialVo materialVo : list) {
                    total += materialService.queryCapacity(materialVo.getMaterialId());
                }
            }
            numberList.add(total);
        }
        map.put("typeName", nameList);
        map.put("number", numberList);
        return map;
    }

    /**
     * 统计出入库物料类型占比
     */
    @Override
    public Map<String, Object> getRecordTypeTotal() {
        HashMap<String, Object> map = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<Long> outerNumber = new ArrayList<>();
        List<Long> enterNumber = new ArrayList<>();
        List<MaterialVo> materialVoList = MongoDBService.findAll(MaterialVo.class);
        for (MaterialVo material : materialVoList) {
            nameList.add(material.getMaterialNameZh());
            long enterTotal = 0;
            long outerTotal = 0;
            List<EnterRecordVo> enterList = MongoDBService.find(EnterRecordVo.class,
                    Query.query(Criteria.where("status").is(4)));
            for (EnterRecordVo enter : enterList) {
                Long aLong = enter.getMatNumber().get(material.getMaterialId());
                if (aLong != null) {
                    enterTotal += aLong;
                }
            }
            List<OuterRecordVo> OuterList = MongoDBService.find(OuterRecordVo.class,
                    Query.query(Criteria.where("status").is(4)));
            for (OuterRecordVo outer : OuterList) {
                Long aLong = outer.getMaterialNumberMap().get(material.getMaterialId());
                if (aLong != null) {
                    outerTotal += aLong;
                }
            }
            outerNumber.add(outerTotal);
            enterNumber.add(enterTotal);
        }
        map.put("typeName", nameList);
        map.put("outerNumber", outerNumber);
        map.put("enterNumber", enterNumber);
        return map;
    }

    /**
     * 统计出入库总数
     * 待审批出入库记录总数
     * 入库记录总数
     * 出库记录总数
     * 待审批入库记录总数
     * 待审批出库记录总数
     */
    @Override
    public Map<String, Object> getRecordTotal() {
        HashMap<String, Object> map = new HashMap<>();
        List<EnterRecordVo> enterList = MongoDBService.findAll(EnterRecordVo.class);
        List<OuterRecordVo> outerList = MongoDBService.findAll(OuterRecordVo.class);
        map.put("enterSize", enterList.size());
        map.put("outerSize", outerList.size());
        map.put("total", enterList.size() + outerList.size());
        int enterNoApprove = 0;
        int outerNoApprove = 0;
        for (EnterRecordVo enter : enterList) {
            if (Registration.Status.INITIAL.getStatus().equals(enter.getStatus())
                    || EnterRecordVo.Status.Review.getStatus().equals(enter.getStatus())) {
                enterNoApprove += 1;
            }
        }
        for (OuterRecordVo outer : outerList) {
            if (Registration.Status.INITIAL.getStatus().equals(outer.getStatus()) ||
                    OuterRecordVo.Status.Review.getStatus().equals(outer.getStatus())
                    || OuterRecordVo.Status.Distribution.getStatus().equals(outer.getStatus())) {
                outerNoApprove += 1;
            }
        }
        map.put("enterNoApprove", enterNoApprove);
        map.put("outerNoApprove", outerNoApprove);
        return map;
    }

    /**
     * 仓库名
     * 各统计仓库最大容量
     * 各仓库容量阈值
     * 各仓库当前容量
     */
    @Override
    public Map<String, Object> getDepotTotal() {
        HashMap<String, Object> map = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<String> nameZhList = new ArrayList<>();
        List<Long> maxCapacityList = new ArrayList<>();
        List<Long> thresholdList = new ArrayList<>();
        List<Long> capacityList = new ArrayList<>();
        List<DepotVo> depotList = MongoDBService.findAll(DepotVo.class);
        for (DepotVo depot : depotList) {
            nameList.add(depot.getDepotName());
            nameZhList.add(depot.getDepotNameZh());
            thresholdList.add(depot.getThreshold());
            maxCapacityList.add(depot.getMaxCapacity());
            long capacity = 0;
            List<String> list = depot.getRackList();
            if (list.size() > 0) {
                capacity += list.stream()
                        .mapToLong(aLong -> rackService.getPracticalCapacity(aLong))
                        .sum();
            }
            capacityList.add(capacity);
        }
        map.put("nameList", nameList);
        map.put("nameZhList", nameZhList);
        map.put("maxCapacityList", maxCapacityList);
        map.put("thresholdList", thresholdList);
        map.put("capacityList", capacityList);
        return map;
    }

    /**
     * 统计物料容量统计
     * 统计出入库占比
     */
    @Override
    public Map<String, Object> getMaterialTotal() {
        HashMap<String, Object> map = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<String> nameZhList = new ArrayList<>();
        List<Long> capacityList = new ArrayList<>();
        List<Long> rangeList = new ArrayList<>();
        List<MaterialVo> materialList = MongoDBService.findAll(MaterialVo.class);
        HashMap<String, Long> recordRange = new HashMap<>();
        List<EnterRecordVo> entereList = MongoDBService.find(EnterRecordVo.class,
                Query.query(Criteria.where("status").is(Registration.Status.DONE.getStatus())));
        for (EnterRecordVo enter : entereList) {
            Map<String, Long> number = enter.getMatNumber();
            if (number.isEmpty()) {
                continue;
            }
            number.forEach((key, value) -> {
                if (key != null && value != null) {
                    recordRange.merge(key, value, Long::sum);
                }
            });
        }
        List<OuterRecordVo> outerList = MongoDBService.find(OuterRecordVo.class,
                Query.query(Criteria.where("status").is(Registration.Status.DONE.getStatus())));
        for (OuterRecordVo outer : outerList) {
            Map<String, Long> number = outer.getMaterialNumberMap();
            if (number.isEmpty()) {
                continue;
            }
            number.forEach((key, value) -> {
                if (key != null && value != null) {
                    recordRange.merge(key, value, Long::sum);
                }
            });
        }
        for (MaterialVo materialVo : materialList) {
            nameList.add(materialVo.getMaterialName());
            nameZhList.add(materialVo.getMaterialNameZh());
            capacityList.add(materialService.queryCapacity(materialVo.getMaterialId()));
            Long aLong = recordRange.get(materialVo.getMaterialId());
            rangeList.add(aLong == null ? 0 : aLong);
        }
        map.put("nameList", nameList);
        map.put("nameZhList", nameZhList);
        map.put("capacityList", capacityList);
        map.put("rangeList", rangeList);

        return map;
    }

}
