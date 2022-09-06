package com.lyne.module.manager.material.infrastructure.factories;

import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;

/**
 * 领域实体 构造器
 * 抽象类
 *
 * @author lyne
 */
public abstract class AbstractFactory<T, E> {
    /**
     * 临时性存储
     *
     * @param t t
     */
    public void save(T t) {
        MongoDBService.save(t);
    }

    /**
     * 清除临时存储
     *
     * @param t t
     */
    public void delete(T t) {
        MongoDBService.remove(t);
    }

    /**
     * 构造领域实体
     *
     * @param t t
     */
    public T get(E t) {
        throw new MaterialManagerException("not implements");
    }

    /**
     * 通过主键Id查询
     *
     * @param id 实体主键
     */
    public T getById(String id) {
        throw new MaterialManagerException("not implements");
    }

    /**
     * 同步数据
     */
    public abstract void syncData();

}
