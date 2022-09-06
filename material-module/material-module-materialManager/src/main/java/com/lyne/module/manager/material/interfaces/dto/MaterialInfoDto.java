package com.lyne.module.manager.material.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialInfoDto {
    private String depotId;
    private String rackId;
    private String materialId;
    private Integer page;
    private Integer size;
}
