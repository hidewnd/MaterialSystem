package com.lyne.module.material.service;

import com.lyne.common.core.base.R;

/**
 *
 * @author lyne
 */
public interface QueryService {

    R<?> queryOne(String name, String type, String arg1, String arg2);

    R<?> queryList(String name, String type, String column, String arg1, String arg2);

    R<?> queryPage(String name, Integer size, Integer page);

}
