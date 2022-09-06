package com.lyne.module.manager.material.interfaces.facade;

import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.interfaces.dto.RecordDto;
import org.springframework.stereotype.Component;

/**
 * 转换层
 *
 * @author lyne
 */
@Component
public class RecordFacade {

    public Registration convert(RecordDto dto) {
        Registration registration = new Registration();
        registration.setRegSign(0);
        if (dto != null) {
            if (dto.getRecordId() != null) {
                registration.setRegId(dto.getRecordId());
            }
            if (dto.getValue() != null) {
                registration.setValue(dto.getValue());
            }
            if (dto.getNumber() != null) {
                registration.setNumber(dto.getNumber());
            }
            if (dto.getSign() != null) {
                registration.setRegSign(dto.getSign());
            }
        }
        return registration;
    }

    public int getType(String type) {
        int status;
        String enterFlag = "enter";
        String outerFlag = "outer";
        String flag1 = "0";
        String flag2 = "1";
        if (StringUtil.isEmpty(type)) {
            status = Registration.ENTER;
        } else {
            if (enterFlag.equals(type) || flag1.equals(type)) {
                status = Registration.ENTER;
            } else if (outerFlag.equals(type) || flag2.equals(type)) {
                status = Registration.OUTER;
            } else {
                throw new ArgumentException("未知参数类型");
            }
        }
        return status;
    }
}
