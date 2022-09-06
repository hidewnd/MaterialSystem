import request from '/@/utils/request';

/**
 * 物料管理服务API
 * 货架
 */
export function RackUpdateAPI() {
	return {
		// 新增
		insert: (params: object) => {
			return request({
				url: '/manager/mat/rack/insert',
				method: 'post',
				data: params,
			});
		},
		// 更新
		update: (params: object) => {
			return request({
				url: '/manager/mat/rack/update',
				method: 'post',
				data: params,
			});
		},
		// 删除
		delete: (params: object) => {
			return request({
				url: '/manager/mat/rack/delete',
				method: 'post',
				data: params,
			});
		},
		// 货架入库物料
		materialStored: (params: string) => {
			return request({
				url: `/manager/mat/rack/stored?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 货架出库物料
		materialRemove: (params: string) => {
			return request({
				url: `/manager/mat/rack/takeout?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}

export function RackQueryAPI() {
	return {
		// 单值查询
		queryOne: (params: string) => {
			return request({
				url: `/manager/mat/rack/query/one?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 多值分页查询
		queryPage: (params: string) => {
			return request({
				url: `/manager/mat/rack/query/page?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}
