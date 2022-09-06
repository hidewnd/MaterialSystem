import request from '/@/utils/request';

/**
 * 登录api接口集合
 * @method signIn 用户登录
 * @method signOut 用户退出登录
 */
export function LoginApi() {
	return {
		login: (params: object) => {
			return request({
				url: '/auth/token/login',
				method: 'post',
				data: params,
			});
		},
		logout: (params: object) => {
			return request({
				url: '/auth/token/logout',
				method: 'post',
				data: params,
			});
		},
		refresh: (params: object) => {
			return request({
				url: '/auth/token/refresh',
				method: 'post',
				data: params,
			});
		},
	};
}
