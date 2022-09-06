import request from '/@/utils/request';

interface queryOneParam {
	name: string;
	type: string;
	arg1: string;
}
/*
 resourceSystemApi().queryListByPage(qs.stringify({name:'account'})).then((res)=>{
  console.log(res)
})
*/
export function ResourceSystemApi() {
	return {
		// 分页查询
		queryPage: (params?: string) => {
			return request({
				url: `/sys/query/page?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			});
		},
		// 单值查询
		queryOne: (params?: queryOneParam) => {
			return request({
				url: `/sys/query/one/${params?.name}/${params?.type}/${params?.arg1}`,
				method: 'get',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			});
		},
		// 多数据查询
		queryList: (params?: string) => {
			return request({
				url: `/sys/query/list/${params}`,
				method: 'get',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			});
		},
	};
}

// 系统用户API
export function SystemAccountApi() {
	return {
		// 用户注册
		register: (params?: string) => {
			return request({
				url: `/sys/insert/register?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 用户更新
		updateAccount: (params?: string) => {
			return request({
				url: `/sys/update/account?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 重置用户密码
		resortPassword: (params?: string) => {
			return request({
				url: `/sys/update/resortPassword?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 用户详情修改
		updateAccountInfo: (params?: string) => {
			return request({
				url: `/sys/update/account/info?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 用户绑定单个角色
		bindRole: (params?: string) => {
			return request({
				url: `/sys/bind/role/one?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},

		// 用户绑定多个角色
		bindRoleByList: (params?: object) => {
			return request({
				url: '/sys/bind/role/list',
				method: 'post',
				data: params,
			});
		},

		// 用户取消绑定单个角色
		removeBindRole: (params?: string) => {
			return request({
				url: `/sys/removeBind/role/one?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 用户取消绑定多个角色
		removeRoleByList: (params?: object) => {
			return request({
				url: '/sys/removeBind/role/list',
				method: 'post',
				data: params,
			});
		},

		// 查询用户信息
		queryUserInfo: () => {
			return request({
				url: '/sys/query/info',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
	};
}
// 系统角色API
export function SystemRoleApi() {
	return {
		// 角色添加
		insertRole: (params?: string) => {
			return request({
				url: `/sys/insert/role?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 角色修改
		setRole: (params?: string) => {
			return request({
				url: `/sys/update/role?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 角色绑定单个权限
		bindMenu: (params?: string) => {
			return request({
				url: `/sys/bind/menu/one?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 角色绑定多个权限
		bindMenuByList: (params?: object) => {
			return request({
				url: `/sys/bind/menu/list`,
				method: 'post',
				data: params,
			});
		},
		// 角色取消绑定单个权限
		removeBindMenu: (params?: string) => {
			return request({
				url: `/sys/removeBind/menu/one?${params}`,
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				method: 'post',
			});
		},
		// 角色取消绑定多个权限
		removeMenuByList: (params?: object) => {
			return request({
				url: '/sys/removeBind/menu/list',
				method: 'post',
				data: params,
			});
		},
	};
}
// 系统权限API
export function SystemMenuApi() {
	return {
		// 权限添加
		insertMenu: (params?: string) => {
			return request({
				url: `/sys/insert/menu?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
		// 权限修改
		setMeu: (params?: string) => {
			return request({
				url: `/sys/update/menu?${params}`,
				method: 'post',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
			});
		},
	};
}
