package com.lyne.module.manager.material.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 出库记录Dto
 *
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OuterDto implements Serializable {
    /**
     * 出库记录Id
     */
    private String recordId;
    /**
     * 出库凭证
     */
    private String proof;
    /**
     * 物料二维码
     */
    private String qrcode;

}
