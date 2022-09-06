import request from '/@/utils/request';

/**
 * 物料管理服务API
 * 出入库记录
 */

export function RecordCommonAPI() {
	return {
		// 出入库记录单值查询
		queryOne: (params: string) => {
			return request({
				url: `/manager/mat/record/query/one?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出入库记录分页查询
		queryPage: (params: string) => {
			return request({
				url: `/manager/mat/record/query/page?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出入库记录负责人查询
		getVoucher: (params: string) => {
			return request({
				url: `/manager/mat/record/query/voucher?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询出入库记录状态
		queryStatue: (params: string) => {
			return request({
				url: `/manager/mat/record/query/status?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询出入库申请单
		queryApproval: () => {
			return request({
				url: `/manager/mat/record/query/approval`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 关闭出入库记录
		closeRecord: (params: string) => {
			return request({
				url: `/manager/mat/record/close?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出入库记录申请审批
		approval: (params: string) => {
			return request({
				url: `/manager/mat/record/approval?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 更新
		update: (params: object) => {
			return request({
				url: '/manager/mat/record/update',
				method: 'post',
				data: params,
			});
		},
		// 删除
		delete: (params: object) => {
			return request({
				url: '/manager/mat/record/delete',
				method: 'post',
				data: params,
			});
		},
	};
}

export function EnterRecordAPI() {
	return {
		// 生成入库记录
		generalEnter: (params: object) => {
			return request({
				url: '/manager/mat/record/enter/insert',
				method: 'post',
				data: params,
			});
		},
		// 入库记录记录物料
		enterBind: (params: object) => {
			return request({
				url: `/manager/mat/record/enter/bind`,
				method: 'post',
				data: params,
			});
		},
		// 入库记录物料正式入库
		enterSave: (params: object) => {
			return request({
				url: '/manager/mat/record/enter/save',
				method: 'post',
				data: params,
			});
		},
		// 查询入库记录待入库物料清单
		queryEnterMaterialList: (params: string) => {
			return request({
				url: `/manager/mat/record/enter/list/waiting?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询入库记录已登记物料清单
		queryEnterBindList: (params: string) => {
			return request({
				url: `/manager/mat/record/enter/list/bind?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}

export function OuterRecordAPI() {
	return {
		// 生成出库记录
		generalOuter: (params: object) => {
			return request({
				url: '/manager/mat/record/outer/insert',
				method: 'post',
				data: params,
			});
		},
		// 出库记录核销物料
		outerRecord: (params: object) => {
			return request({
				url: '/manager/mat/record/outer/verify',
				method: 'post',
				data: params,
			});
		},
		// 出库记录获取出库凭证
		getOuterProof: (params: string) => {
			return request({
				url: `/manager/mat/record/outer/proof?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出库记录获取待取物料地址
		getOuterLocations: (params: string) => {
			return request({
				url: `/manager/mat/record/outer/locations?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出库记录获取所有物料二维码
		getAllQrCode: (params: string) => {
			return request({
				url: `/manager/mat/record/outer/qrcode/all?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出库记录获取物料二维码
		getQrCode: (params: string) => {
			return request({
				url: `/manager/mat/record/outer/qrcode?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 出库记录获取物料清单
		queryMaterialList: (params: string) => {
			return request({
				url: `/manager/mat/record/outer/matList?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}
