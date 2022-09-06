import request from '/@/utils/request';

// 物料资源API
export function ResourceMaterialApi() {
	return {
		// 查询多值库存
		queryOne: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/query/one?${params}`,
				method: 'get',
			});
		},
		// 查询多值库存
		queryList: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/query/list?${params}`,
				method: 'get',
			});
		},
		// 多数据分页查询接口
		queryPage: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/query/page?${params}`,
				method: 'get',
			});
		},
		// 查询单值库存
		queryStockByOne: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/query/one/stock?${params}`,
				method: 'post',
			});
		},

		// 查询库存列表
		queryStockByList: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/query/list/stock?${params}`,
				method: 'post',
			});
		},
		// 查询库存列表
		queryStockByPage: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/query/list/stock?${params}`,
				method: 'get',
			});
		},
	};
}

// 物料API
export function MaterialApi() {
	return {
		// 新增或修改物料
		saveMaterial: (params?: object) => {
			return request({
				url: '/mat/save/material',
				method: 'post',
				data: params,
			});
		},
		// 删除
		deleteMaterial: (params?: object) => {
			return request({
				url: '/mat/delete/material',
				method: 'post',
				data: params,
			});
		},
	};
}

// 物料类型API
export function MaterialTypeApi() {
	return {
		// 新增或修改物料
		saveMaterial: (params?: object) => {
			return request({
				url: '/mat/save/material/type',
				method: 'post',
				data: params,
			});
		},
		// 删除物料类型
		deleteMaterial: (params?: object) => {
			return request({
				url: '/mat/delete/material/type',
				method: 'post',
				data: params,
			});
		},
	};
}

// 货架API
export function RackApi() {
	return {
		// 新增或修改货架
		saveRack: (params?: object) => {
			return request({
				url: '/mat/save/rack',
				method: 'post',
				data: params,
			});
		},
		// 删除货架
		deleteRack: (params?: object) => {
			return request({
				url: '/mat/delete/rack',
				method: 'post',
				data: params,
			});
		},
		// 货架存入物料
		rackBind: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/rack/store?${params}`,
				method: 'post',
			});
		},
		// 货架取出物料
		rackTakeOut: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/rack/takeout?${params}`,
				method: 'post',
			});
		},
	};
}

// 仓库API
export function DepotApi() {
	return {
		// 新增或修改仓库
		saveRack: (params?: object) => {
			return request({
				url: '/mat/save/depot',
				method: 'post',
				params,
			});
		},
		// 删除仓库
		deleteRack: (params?: object) => {
			return request({
				url: '/mat/delete/depot',
				method: 'post',
				params,
			});
		},
		// 仓库绑定货架
		depotBindRack: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/depot/bind/rack?${params}`,
				method: 'post',
			});
		},
		// 仓库解除绑定货架
		depotRemoveRack: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/depot/removeBind/rack?${params}`,
				method: 'post',
			});
		},
	};
}

// 出入库记录API
export function RecordApi() {
	return {
		// 新增或修改出入库记录
		saveRecord: (params?: object) => {
			return request({
				url: '/mat/save/record',
				method: 'post',
				params,
			});
		},
		// 删除出入库记录
		deleteRecord: (params?: object) => {
			return request({
				url: '/mat/delete/record',
				method: 'post',
				params,
			});
		},
		// 出入库记录绑定物料
		recordBind: (params?: object) => {
			return request({
				url: '/mat/record/bind/material',
				method: 'post',
				params,
			});
		},
		// 出入库记录修改绑定物料
		updateRecordBind: (params?: object) => {
			return request({
				url: '/mat/record/updateBind/material',
				method: 'post',
				params,
			});
		},
		// 出入库记录解除绑定物料
		removeRecordBind: (params?: string) => {
			return request({
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				url: `/mat/record/removeBind/material?${params}`,
				method: 'post',
			});
		},
	};
}
