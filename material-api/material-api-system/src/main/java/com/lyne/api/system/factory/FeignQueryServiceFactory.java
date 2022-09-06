package com.lyne.api.system.factory;

import com.lyne.api.system.service.FeignSystemService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 查询接口熔断处理
 *
 * @author lyne
 */
public class FeignQueryServiceFactory implements FallbackFactory<FeignSystemService> {

    @Override
    public FeignSystemService create(Throwable cause) {
        return new FeignSystemService() {
            @Override
            public R<?> queryOne(String name, String type, String values, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?>  queryList(String name, String type, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?>  queryList(String name, String type, String column, String variable1, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

            @Override
            public R<?>  queryList(String name, String type, String column, String variable1, String variable2, String source) {
                return R.fail(HttpStatus.SERVICE_DEGRADE, "请稍后再试");
            }

        };
    }
}
