import request from '/@/utils/request';

/**
 * 物料管理服务API
 * 数据统计接口
 */

export function StatisticAPI() {
	return {
		// 查询库存统计
		queryStockTotal: () => {
			return request({
				url: `/manager/mat/statistics/total/stock`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询物料类型库存统计
		queryTypeStockTotal: () => {
			return request({
				url: `/manager/mat/statistics/total/type`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询出入库记录中物料占比
		queryRecordTypeTotal: () => {
			return request({
				url: `/manager/mat/statistics/total/type/record`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询出入库记录统计
		queryRecordTotal: () => {
			return request({
				url: `/manager/mat/statistics/total/record`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询仓库统计
		queryDepotTotal: () => {
			return request({
				url: `/manager/mat/statistics/total/depot`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 查询物料统计
		queryMaterialTotal: () => {
			return request({
				url: `/manager/mat/statistics/total/material`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}
