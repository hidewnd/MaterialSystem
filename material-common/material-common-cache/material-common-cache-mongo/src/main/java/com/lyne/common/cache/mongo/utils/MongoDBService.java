package com.lyne.common.cache.mongo.utils;

import cn.hutool.core.util.NumberUtil;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * MongoDB简单工具集合
 *
 * @author lyne
 */
@Component
public class MongoDBService {
    @Resource
    private MongoTemplate mongoTemplate;

    public static MongoDBService mongoDBUtil;

    @PostConstruct
    public void init() {
        mongoDBUtil = this;
        mongoDBUtil.mongoTemplate = this.mongoTemplate;
    }

    public static MongoTemplate getMongoTemplate() {
        return mongoDBUtil.mongoTemplate;
    }

    /*--------------------*/
    /*-------保存操作-------*/
    /*--------------------*/

    /**
     * 保存数据对象，集合为数据对象中@Document 注解所配置的collection
     *
     * @param obj 数据对象
     */
    public static void save(Object obj) {
        mongoDBUtil.mongoTemplate.save(obj);
    }

    /**
     * 指定集合保存数据对象
     *
     * @param obj            数据对象
     * @param collectionName 集合名
     */
    public static void save(Object obj, String collectionName) {
        mongoDBUtil.mongoTemplate.save(obj, collectionName);
    }

    /*--------------------*/
    /*-------删除操作-------*/
    /*--------------------*/

    /**
     * 根据数据对象中的id删除数据，集合为数据对象中@Document 注解所配置的collection
     *
     * @param obj 数据对象
     */
    public static void remove(Object obj) {
        mongoDBUtil.mongoTemplate.remove(obj);
    }

    /**
     * 指定集合 根据数据对象中的id删除数据
     *
     * @param obj            数据对象
     * @param collectionName 集合名
     */
    public static void remove(Object obj, String collectionName) {
        mongoDBUtil.mongoTemplate.remove(obj, collectionName);
    }

    /**
     * 根据key，value到指定集合删除数据
     *
     * @param key            键
     * @param value          值
     * @param collectionName 集合名
     */
    public static void removeById(String key, Object value, String collectionName) {
        Criteria criteria = Criteria.where(key).is(value);
        criteria.and(key).is(value);
        Query query = Query.query(criteria);
        mongoDBUtil.mongoTemplate.remove(query, collectionName);
    }

    /*-----------------------------------------*/
    /*-----------------修改操作-----------------*/
    /*----------------------------------------*/

    /**
     * 指定集合 修改数据，且仅修改找到的第一条数据
     *
     * @param accordingKey   查询 key
     * @param accordingValue 查询 value
     * @param map            修改内容
     * @param collectionName 集合名
     */
    public static void updateFirst(String accordingKey, Object accordingValue, Map<String, Object> map,
                                   String collectionName) {

        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Update update = new Update();
        if (!map.isEmpty()) {
            map.forEach(update::set);
        }
        MongoDBService.updateFirst(criteria, update, collectionName);
    }

    public static <T> void updateFirst(String accordingKey, Object accordingValue, Map<String, Object> map,
                                       Class<?> clazz) {

        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Update update = new Update();
        if (!map.isEmpty()) {
            map.forEach(update::set);
        }
        MongoDBService.updateFirst(criteria, update, clazz);
    }

    public static void updateFirst(Criteria criteria, Update update, String collectionName) {
        mongoDBUtil.mongoTemplate.updateFirst(Query.query(criteria), update, collectionName);
    }

    public static void updateFirst(Criteria criteria, Update update, Class<?> clazz) {
        mongoDBUtil.mongoTemplate.updateFirst(Query.query(criteria), update, clazz);
    }

    /**
     * 指定集合 修改数据，且修改所找到的所有数据
     *
     * @param accordingKey   查询 key
     * @param accordingValue 查询 value
     * @param map            修改内容
     * @param collectionName 集合名
     */
    public static void updateAll(String accordingKey, Object accordingValue, Map<String, Object> map,
                                 String collectionName) {

        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Update update = new Update();
        if (!map.isEmpty()) {
            map.forEach(update::set);
        }
        MongoDBService.updateAll(criteria, update, collectionName);
    }

    public static void updateAll(String accordingKey, Object accordingValue, Map<String, Object> map,
                                 Class<?> clazz) {

        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Update update = new Update();
        if (!map.isEmpty()) {
            map.forEach(update::set);
        }
        MongoDBService.updateAll(criteria, update, clazz);
    }

    public static void updateAll(Criteria criteria, Update update, String collectionName) {
        mongoDBUtil.mongoTemplate.updateMulti(Query.query(criteria), update, collectionName);
    }

    public static void updateAll(Criteria criteria, Update update, Class<?> clazz) {
        mongoDBUtil.mongoTemplate.updateMulti(Query.query(criteria), update, clazz);
    }

    /*-----------------------------------------*/
    /*-----------------查询操作-----------------*/
    /*----------------------------------------*/


    /**
     * 根据id查询数据
     *
     * @param clazz 数据类型
     * @param id    主键ID
     */
    public static <T> T findOneById(Class<T> clazz, Object id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return MongoDBService.findOne(clazz, query);
    }

    /**
     * 查询出所有结果集 集合为数据对象中 @Document 注解所配置的collection
     *
     * @param clazz 数据类型
     */
    public static <T> List<T> findAll(Class<T> clazz) {
        return mongoDBUtil.mongoTemplate.findAll(clazz);
    }

    /**
     * 查询出所有结果集 集合为数据对象中 @Document 注解所配置的collection
     *
     * @param clazz          数据类型
     * @param collectionName 集合名
     */
    public static <T> List<T> findAll(Class<T> clazz, String collectionName) {
        return mongoDBUtil.mongoTemplate.findAll(clazz, collectionName);
    }

    public static <T> List<T> find(Class<T> clazz, Query query) {
        return mongoDBUtil.mongoTemplate.find(query, clazz);
    }

    public static <T> List<T> find(Class<T> clazz, Query query, String collectionName) {
        return mongoDBUtil.mongoTemplate.find(query, clazz, collectionName);
    }

    public static <T> T findOne(Class<T> clazz, Query query) {
        return mongoDBUtil.mongoTemplate.findOne(query, clazz);
    }

    public static <T> T findOne(Class<T> clazz, Query query, String collectionName) {
        return mongoDBUtil.mongoTemplate.findOne(query, clazz, collectionName);
    }

    /*-----------------------------------------*/
    /*-----------------分页查询操作--------------*/
    /*----------------------------------------*/

    /**
     * 分页查询
     *
     * @param clazz  查询类 获取对应集合
     * @param size   每页大小
     * @param number 页号 第一页序号为1
     * @return page
     */
    public static <T> PageImpl<T> page(Class<T> clazz, Integer size, Integer number) {
        return page(clazz, new HashMap<>(), size, number);
    }

    /**
     * 分页查询
     *
     * @param clazz  查询类 获取对应集合
     * @param size   每页大小
     * @param map    查询条件 默认条件 status != -1(禁用)
     * @param number 页号 第一页序号为1
     * @return page
     */
    public static <T> PageImpl<T> page(Class<T> clazz, Map<String, Object> map, Integer size, Integer number) {
        Criteria criteria = new Criteria();
        if (map != null && map.size() > 0) {
            map.forEach((key, value) -> {
                if (value instanceof String && NumberUtil.isNumber((String) value)) {
                    value = NumberUtil.parseInt((String) value);
                }
                criteria.and(key).is(value);
            });
        }
        return page(clazz, criteria, size, number);
    }

    /**
     * @param clazz    查询类 获取对应集合 搭配@Document 使用
     * @param criteria 查询条件
     * @param size     每页大小
     * @param page   页号 第一页序号为1
     * @return page
     */
    public static <T> PageImpl<T> page(Class<T> clazz, Criteria criteria, Integer size, Integer page) {
        int defSize = size == null || size <= 0 ? 10 : size; // 每页大小
        int defPage = page == null || page < 0 ? 0 : page - 1; // 页面索引
        MongoTemplate mongoTemplate = MongoDBService.getMongoTemplate();
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        long count = mongoTemplate.count(query, clazz);
        PageRequest pageable = PageRequest.of(defPage, defSize);
        List<T> list = mongoTemplate.find(query.with(pageable), clazz);
        return new PageImpl<>(list, pageable, count);
    }

}
