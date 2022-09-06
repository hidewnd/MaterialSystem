import request from '/@/utils/request';

/**
 * 物料管理服务API
 * 物料
 */
export function MaterialUpdateAPI() {
	return {
		// 物料信息新增
		insertMaterial: (params: object) => {
			return request({
				url: '/manager/mat/material/insert',
				method: 'post',
				data: params,
			});
		},
		// 物料类型信息新增
		insertType: (params: object) => {
			return request({
				url: '/manager/mat/material/type/insert',
				method: 'post',
				data: params,
			});
		},
		// 物料信息更新
		updateMaterial: (params: object) => {
			return request({
				url: '/manager/mat/material/update',
				method: 'post',
				data: params,
			});
		},
		// 物料类型信息更新
		updateMaterialType: (params: object) => {
			return request({
				url: '/manager/mat/material/type/update',
				method: 'post',
				data: params,
			});
		},
		// 物料信息删除
		deleteMaterial: (params: object) => {
			return request({
				url: '/manager/mat/material/delete',
				method: 'post',
				data: params,
			});
		},
		// 物料类型信息删除
		deleteMaterialType: (params: object) => {
			return request({
				url: '/manager/mat/material/type/delete',
				method: 'post',
				data: params,
			});
		},
	};
}

export function MaterialQueryAPI() {
	return {
		// 获取记录中物料状态
		queryStatus: (params: string) => {
			return request({
				url: `/manager/mat/material/status?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 获取物料位置
		queryLocation: (params: string) => {
			return request({
				url: `/manager/mat/material/location?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 物料相关单值查询
		queryOne: (params: string) => {
			return request({
				url: `/manager/mat/material/query/one?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询货架中物料详情
		queryList: (params: string) => {
			return request({
				url: `/manager/mat/material/query/list?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 物料相关分页查询
		queryPage: (params: string) => {
			return request({
				url: `/manager/mat/material/query/page?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询货架中物料详情
		queryOneInfo: (params: string) => {
			return request({
				url: `/manager/mat/material/query/info/one?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询物料详情列表
		queryInfoList: (params: string) => {
			return request({
				url: `/manager/mat/material/query/info/list?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询物料详情列表
		queryInfoListByArg: (params: object) => {
			return request({
				url: `/manager/mat/material/query/info/list/arg`,
				method: 'post',
				data: params,
			});
		},
		// 查询物料库存
		queryCapacity: (params: string) => {
			return request({
				url: `/manager/mat/material/query/capacity?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询所有物料库存
		queryAllCapacity: () => {
			return request({
				url: `/manager/mat/material/query/capacity/all`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}
