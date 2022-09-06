package com.lyne.module.manager.material.interfaces.facade;

import com.lyne.common.core.types.MaterialDepot;
import com.lyne.module.manager.material.interfaces.dto.DepotDto;
import org.springframework.stereotype.Component;

/**
 * @author lyne
 */
@Component
public class DepotFacade {

    public MaterialDepot convert(DepotDto dto) {
        MaterialDepot depot = new MaterialDepot();
        if (dto != null) {
            if (dto.getDepotId() != null) {
                depot.setDepotId(dto.getDepotId());
            }
            if (dto.getDepotName() != null) {
                depot.setDepotName(dto.getDepotName());
            }
            if (dto.getDepotNameZh() != null) {
                depot.setDepotNameZh(dto.getDepotNameZh());
            }
            if (dto.getDescription() != null) {
                depot.setDescription(dto.getDescription());
            }
            if (dto.getPlace() != null) {
                depot.setPlace(dto.getPlace());
            }
            if (dto.getThreshold() != null) {
                depot.setThreshold(dto.getThreshold());
            }
            if (dto.getMaxCapacity() != null) {
                depot.setMaxCapacity(dto.getMaxCapacity());
            }
            if (dto.getRemark() != null) {
                depot.setRemark(dto.getRemark());
            }
            if (dto.getStatus() != null) {
                depot.setStatus(dto.getStatus());
            }
        }
        return depot;
    }

}
