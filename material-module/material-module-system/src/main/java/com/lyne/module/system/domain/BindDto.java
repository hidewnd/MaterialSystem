package com.lyne.module.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BindDto implements Serializable {

    private String id;
    private List<String> bindList;
}
