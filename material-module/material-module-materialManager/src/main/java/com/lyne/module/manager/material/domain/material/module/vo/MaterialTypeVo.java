package com.lyne.module.manager.material.domain.material.module.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 领域实体
 * 物料类别
 *
 * @author lyne
 */
@Data
@Document("type_vo")
@AllArgsConstructor
public class MaterialTypeVo implements Serializable {
    @Id
    private String typeId;            // 物料类型ID
    private String parentId;          // 父级类型ID
    private String typeName;        // 物料类型名
    private String typeNameZh;      // 物料类型中文

    private Integer status;         // 当前状态

    private String remark;          // 备注

    private String createBy;

    private String updateBy;

    private String createDate;
    private String updateDate;

    public MaterialTypeVo() {
    }

    public MaterialTypeVo(String typeId, String parent, String typeName, String typeNameZh) {
        this.typeId = typeId;
        this.parentId = parent;
        this.typeName = typeName;
        this.typeNameZh = typeNameZh;
        this.status = 0;
    }
    public String getCreateBy() {
        if (this.createBy == null || this.createBy.length() == 0) {
            this.createBy = "system";
        }
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy =  createBy == null || createBy.isEmpty() ? "system" : createBy;
    }

    public String getUpdateBy() {
        if (this.updateBy == null || this.updateBy.length() == 0) {
            this.updateBy = "system";
        }
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy =  updateBy == null || updateBy.isEmpty() ? "system" : updateBy;
    }


    public void dataCheck() {
        if (getStatus() == null) {
            this.setStatus(0);
        }
        if (getTypeNameZh() == null) {
            this.setTypeNameZh(getTypeName());
        }
    }
}
