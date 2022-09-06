package com.lyne.module.manager.material.interfaces.facade;

import com.lyne.common.core.types.MaterialRack;
import com.lyne.module.manager.material.interfaces.dto.RackDto;
import org.springframework.stereotype.Component;

/**
 * @author lyne
 */
@Component
public class RackFacade {

    public MaterialRack convertRack(RackDto dto) {
        MaterialRack rack = new MaterialRack();
        if (dto != null) {
            if (dto.getDepotId() != null) {
                rack.setDepotId(dto.getDepotId());
            }
            if (dto.getRackId() != null) {
                rack.setRackId(dto.getRackId());
            }
            if (dto.getDescription() != null) {
                rack.setDescription(dto.getDescription());
            }
            if (dto.getMaxCapacity() != null) {
                rack.setMaxCapacity(dto.getMaxCapacity());
            }
            if (dto.getRemark() != null) {
                rack.setRemark(dto.getRemark());
            }
            if (dto.getStatus() != null) {
                rack.setStatus(dto.getStatus());
            }
        }
        return rack;
    }
}
