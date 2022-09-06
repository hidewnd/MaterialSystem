package com.lyne.module.manager.material.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 入库数据实体
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterMaterialVo implements Serializable {
    private String rackId;
    private String recordId;
    private String materialId;
    private Long remain;
    private String qrcode;
}
