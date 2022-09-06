import request from '/@/utils/request';

/**
 * 物料管理服务API
 * 仓库
 */
export function DepotUpdateAPI() {
    return {
        // 新增
        insert: (params: object) => {
            return request({
                url: '/manager/mat/depot/insert',
                method: 'post',
                data: params,
            });
        },
        // 更新
        update: (params: object) => {
            return request({
                url: '/manager/mat/depot/update',
                method: 'post',
                data: params,
            });
        },
        // 删除
        delete: (params: object) => {
            return request({
                url: '/manager/mat/depot/delete',
                method: 'post',
                data: params,
            });
        },
        // 移动货架
        transferRack: (params: string) => {
            return request({
                url: `/manager/mat/depot/transfer?${params}`,
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
                method: 'post',
            });
        },
        // 设置容量阈值
        setThreshold: (params: string) => {
            return request({
                url: `/manager/mat/depot/set/threshold?${params}`,
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
                method: 'post',
            });
        },
    };
}

export function DepotQueryAPI() {
    return {
        // 单值查询
        queryOne: (params: string) => {
            return request({
                url: `/manager/mat/depot/query/one?${params}`,
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
                method: 'post',
            });
        },
        // 多值分页查询
        queryList: (params: string) => {
            return request({
                url: `/manager/mat/depot/query/page?${params}`,
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
                method: 'post',
            });
        },

    }
}