import request from '/@/utils/request';

/**
 * 登录api接口集合
 * @method signIn 用户登录
 * @method signOut 用户退出登录
 */
export function CommonApi() {
	return {
		/**
		 * 获取上传签名
		 */
		getSignature: (params?: object) => {
			return request({
				url: '/auth/token/signature',
				method: 'post',
				params,
			});
		},
		/**
		 * 导出物料信息
		 */
		downloadMaterial: () => {
			return request({
				url: '/manager/mat/material/export/excel',
				method: 'post',
				responseType: 'blob',
			});
		},
		/**
		 * 导出出入库记录信息
		 */
		downloadRecord: () => {
			return request({
				url: '/manager/mat/record/export/excel',
				method: 'post',
				responseType: 'blob',
			});
		},
	};
}
