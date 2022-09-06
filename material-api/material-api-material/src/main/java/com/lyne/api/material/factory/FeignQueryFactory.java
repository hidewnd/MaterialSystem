package com.lyne.api.material.factory;

import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 熔断处理
 *
 * @author lyne
 */
public class FeignQueryFactory implements FallbackFactory<FeignMaterialService> {
    @Override
    public FeignMaterialService create(Throwable cause) {
        return new FeignMaterialService() {
            @Override
            public R<?> queryOne(String type, String way, String arg1, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryOne(String name, String type, String arg1, String arg2, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryStockByOne(String rackId, String recordId, String materialId, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryList(String type, String way, String column) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryList(String type, String way, String column, String arg1) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryList2(String type, String way, String arg1, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryList(String type, String way, String column, String arg1, String arg2) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryList(String type, String way, String column, String arg1, String arg2, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> queryStockByList(String rackId, String recordId, String materialId, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<String> saveRecord(Registration registration, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<String> saveMaterial(Material material, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<String> saveMaterialType(MaterialType type, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<String> saveDepot(MaterialDepot depot, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<String> saveRack(MaterialRack rack, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> rackStore(String rackId, String recordId, String materialId, Long number, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> rackTackOut(Long rackId, Long recordId, Long materialId, Long number, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<Long> recordBind(RegMaterial regMaterial, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<Long> updateRecordBind(RegMaterial regMaterial, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> removeRecordBind(Long regId, Long materialId, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> depotBindRack(Long depotId, Long rackId, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?> depotRemoveRack(Long depotId, Long rackId, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }
        };
    }
}
