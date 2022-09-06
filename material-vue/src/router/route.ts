import { RouteRecordRaw } from 'vue-router';

/**
 * 路由meta对象参数说明
 * meta: {
 *      title:          菜单栏及 tagsView 栏、菜单搜索名称（国际化）
 *      isLink：        是否超链接菜单，开启外链条件，`1、isLink:true 2、链接地址不为空`
 *      isHide：        是否隐藏此路由
 *      isKeepAlive：   是否缓存组件状态
 *      isAffix：       是否固定在 tagsView 栏上
 *      isIframe：      是否内嵌窗口，，开启条件，`1、isIframe:true 2、链接地址不为空`
 *      roles：         当前路由权限标识，取角色管理。控制路由显示、隐藏。超级管理员：root 普通角色：common
 *      icon：          菜单、tagsView 图标，阿里：加 `iconfont xxx`，fontawesome：加 `fa xxx`
 * }
 */

/**
 * 定义动态路由
 * @description 未开启 isRequestRoutes 为 true 时使用（前端控制路由），开启时第一个顶级 children 的路由将被替换成接口请求回来的路由数据
 * @description 各字段请查看 `/@/views/system/menu/component/addMenu.vue 下的 ruleForm`
 * @returns 返回路由菜单数据
 */
export const dynamicRoutes: Array<RouteRecordRaw> = [
	{
		path: '/',
		name: 'root',
		component: () => import('/@/layout/index.vue'),
		redirect: '/home',
		meta: {
			isKeepAlive: true,
		},
		children: [
			{
				path: '/home',
				name: 'home', // 首页
				component: () => import('/@/views/home/index.vue'),
				meta: {
					title: 'message.router.home',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: true,
					isIframe: false,
					roles: ['common'],
					icon: 'iconfont icon-shouye',
				},
			},
			{
				path: '/resources',
				name: 'resourcesManger', // 资源管理
				component: () => import('/@/layout/routerView/parent.vue'),
				redirect: '/resources/system',
				meta: {
					title: 'message.router.resources',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['root', 'financial', 'warehouse'],
					icon: 'iconfont icon-xitongshezhi',
				},
				children: [
					{
						path: '/resources/system',
						name: 'systemResources', // 系统资源管理
						component: () => import('/@/layout/routerView/parent.vue'),
						redirect: '/resources/system/account',
						meta: {
							title: 'message.router.systemResources',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root', 'financial'],
							icon: 'iconfont icon-caidan',
						},
						children: [
							{
								path: '/resources/system/account',
								name: 'systemAccount', // 用户管理
								component: () => import('/@/views/resources/system/systemAccount/index.vue'),
								meta: {
									title: 'message.router.systemAccount',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'financial'],
									icon: 'iconfont icon-caidan',
								},
							},
							{
								path: '/resources/system/role',
								name: 'ResourcesSystemRole', // 角色管理
								component: () => import('/@/views/resources/system/systemRole/index.vue'),
								meta: {
									title: 'message.router.systemRole',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root'],
									icon: 'iconfont icon-caidan',
								},
							},
							{
								path: '/resources/system/menu',
								name: 'systemMenu', // 权限管理
								component: () => import('/@/views/resources/system/systemMenu/index.vue'),
								meta: {
									title: 'message.router.systemMenu',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root'],
									icon: 'iconfont icon-caidan',
								},
							},
						],
					},
					{
						path: '/resources/material',
						name: 'materialResources', // 物料资源管理
						component: () => import('/@/layout/routerView/parent.vue'),
						redirect: '/resources/material/material',
						meta: {
							title: 'message.router.materialResources',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root', 'warehouse'],
							icon: 'ele-ColdDrink',
						},
						children: [
							{
								path: '/resources/material/material',
								name: 'ResourcesMaterialMaterial', // 物料管理
								component: () => import('/@/views/resources/material/materialMaterial/index.vue'),
								meta: {
									title: 'message.router.materialMaterial',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-caidan',
								},
							},
							{
								path: '/resources/material/material/type',
								name: 'ResourcesMaterialMaterialType', // 物料类型管理
								component: () => import('/@/views/resources/material/materialMaterialType/index.vue'),
								meta: {
									title: 'message.router.materialMaterialType',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-caidan',
								},
							},
							{
								path: '/resources/material/rack',
								name: 'ResourcesMaterialRack', // 货架管理
								component: () => import('/@/views/resources/material/materialRack/index.vue'),
								meta: {
									title: 'message.router.materialRack',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-caidan',
								},
							},
							{
								path: '/resources/material/depot',
								name: 'ResourcesMaterialDepot', // 仓库管理
								component: () => import('/@/views/resources/material/materialDepot/index.vue'),
								meta: {
									title: 'message.router.materialDepot',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-caidan',
								},
							},
						],
					},
				],
			},
			{
				path: '/record',
				name: 'recordManager', // 出入库管理
				component: () => import('/@/layout/routerView/parent.vue'),
				redirect: '/record/enter',
				meta: {
					title: 'message.router.recordManager',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['root', 'financial', 'warehouse','common'],
					icon: 'iconfont icon-xitongshezhi',
				},
				children: [
					{
						path: '/record/enter',
						name: 'enterManger', // 入库管理
						component: () => import('/@/layout/routerView/parent.vue'),
						redirect: '/record/enter/query',
						meta: {
							title: 'message.router.enterManger',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['common'],
							icon: 'iconfont icon-xitongshezhi',
						},
						children: [
							{
								path: '/record/enter/query',
								name: 'RecordEnterQuery', // 记录查询
								component: () => import('/@/views/record/enterRecord/enterQuery/index.vue'),
								meta: {
									title: 'message.router.recordQuery',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse', 'financial'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
							{
								path: '/record/enter/apply',
								name: 'RecordEnterApply', // 入库申请
								component: () => import('/@/views/record/enterRecord/enterApply/index.vue'),
								meta: {
									title: 'message.router.enterApply',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['common'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
							{
								path: '/record/enter/bind',
								name: 'RecordEnterBind', // 入库登记
								component: () => import('/@/views/record/enterRecord/enterBind/index.vue'),
								meta: {
									title: 'message.router.enterBind',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
							{
								path: '/record/enter/bill',
								name: 'recordEnterBill', // 入库清单查询
								component: () => import('/@/views/record/enterRecord/enterBill/index.vue'),
								meta: {
									title: 'message.router.outerMaterialBill',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse', 'financial', 'common'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
						],
					},
					{
						path: '/record/outer',
						name: 'outerManger', // 出库管理
						component: () => import('/@/layout/routerView/parent.vue'),
						redirect: '/record/outer/query',
						meta: {
							title: 'message.router.outerManger',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['common'],
							icon: 'iconfont icon-xitongshezhi',
						},
						children: [
							{
								path: '/record/outer/query',
								name: 'RecordOuterQuery', // 记录查询
								component: () => import('/@/views/record/outerRecord/outerQuery/index.vue'),
								meta: {
									title: 'message.router.recordQuery',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse', 'financial'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
							{
								path: '/record/outer/apply',
								name: 'RecordOuterApply', // 出库申请
								component: () => import('/@/views/record/outerRecord/outerApply/index.vue'),
								meta: {
									title: 'message.router.outerApply',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['common'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
							{
								path: '/record/outer/bill',
								name: 'RecordOuterMaterialBill', // 清单查询
								component: () => import('/@/views/record/outerRecord/outerMaterialBill/index.vue'),
								meta: {
									title: 'message.router.outerMaterialBill',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['common'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
						],
					},
					{
						path: '/record/status',
						name: 'RecordQueryStatus', // 记录状态查询
						component: () => import('/@/views/record/queryStatus/index.vue'),
						meta: {
							title: 'message.router.recordQueryStatus',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['common'],
							icon: 'iconfont icon-xitongshezhi',
						},
					},
					{
						path: '/record/approve',
						name: 'recordApprove', // 出入库审批
						component: () => import('/@/views/record/approve/index.vue'),
						meta: {
							title: 'message.router.recordApprove',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root', 'warehouse', 'financial'],
							icon: 'iconfont icon-xitongshezhi',
						},
					},
				],
			},
			{
				path: '/warehouse',
				name: 'warehourceManger', // 仓储管理
				component: () => import('/@/layout/routerView/parent.vue'),
				redirect: '/warehouse/inventory',
				meta: {
					title: 'message.router.warehouseManger',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['common'],
					icon: 'iconfont icon-xitongshezhi',
				},
				children: [
					{
						path: '/warehouse/depot',
						name: 'depotManger', // 仓库管理
						component: () => import('/@/layout/routerView/parent.vue'),
						redirect: '/warehouse/depot/transfer',
						meta: {
							title: 'message.router.depotManger',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root', 'warehouse'],
							icon: 'iconfont icon-xitongshezhi',
						},
						children: [
							{
								path: '/warehouse/depot/transfer',
								name: 'WarehouseDepotTransfer', // 货架转移
								component: () => import('/@/views/warehouse/depotManager/rackTransfer/index.vue'),
								meta: {
									title: 'message.router.rackTransfer',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
						],
					},
					{
						path: '/warehouse/rack',
						name: 'rackManger', // 货架管理
						component: () => import('/@/layout/routerView/parent.vue'),
						redirect: '/warehouse/rack/stored',
						meta: {
							title: 'message.router.rackManger',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root', 'warehouse'],
							icon: 'iconfont icon-xitongshezhi',
						},
						children: [
							{
								path: '/warehouse/rack/outOrStored',
								name: 'WarehouseRackOutOrStored', // 物料出入库
								component: () => import('/@/views/warehouse/rackManager/rackOutOrStored/index.vue'),
								meta: {
									title: 'message.router.rackOutOrStored',
									isLink: '',
									isHide: false,
									isKeepAlive: true,
									isAffix: false,
									isIframe: false,
									roles: ['root', 'warehouse'],
									icon: 'iconfont icon-xitongshezhi',
								},
							},
						],
					},
					{
						path: '/warehouse/inventory',
						name: 'WarehouseInventory', // 库存查询
						component: () => import('/@/views/warehouse/inventory/index.vue'),
						meta: {
							title: 'message.router.inventory',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['common'],
							icon: 'iconfont icon-xitongshezhi',
						},
					},
				],
			},
			{
				path: '/authority',
				name: 'authorityManger', // 权限管理
				component: () => import('/@/layout/routerView/parent.vue'),
				redirect: '/authority/role',
				meta: {
					title: 'message.router.authority',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['root'],
					icon: 'iconfont icon-xitongshezhi',
				},
				children: [
					{
						path: '/authority/role',
						name: 'AuthorityRoleAssignment', // 角色分发
						component: () => import('/@/views/authority/roleAssignment/index.vue'),
						meta: {
							title: 'message.router.roleAssignment',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root'],
							icon: 'iconfont icon-xitongshezhi',
						},
					},
					{
						path: '/authority/menu',
						name: 'AuthorityMenuAssignment', // 权限分发
						component: () => import('/@/views/authority/menuAssignment/index.vue'),
						meta: {
							title: 'message.router.menuAssignment',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root'],
							icon: 'iconfont icon-xitongshezhi',
						},
					},
					{
						path: '/authority/restartpwd',
						name: 'AuthorityRestartPasswprd', // 密码重置
						component: () => import('/@/views/authority/restartPassword/index.vue'),
						meta: {
							title: 'message.router.restartPassword',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root'],
							icon: 'iconfont icon-xitongshezhi',
						},
					},
				],
			},
			{
				path: '/visualization',
				name: 'visualizationIndex', // 可视化视图
				component: () => import('/@/layout/routerView/parent.vue'),
				redirect: '/visualization/chart',
				meta: {
					title: 'message.router.visualization',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['root', 'financial'],
					icon: 'iconfont icon-gerenzhongxin',
				},
				children: [
					{
						path: '/visualization/chart',
						name: 'chartIndex', // 可视化视图
						component: () => import('/@/views/visualization/chart/index.vue'),
						meta: {
							title: 'message.router.recordStatistics',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['root', 'financial'],
							icon: 'iconfont icon-ico_shuju',
						},
					},
					// {
					// 	path: '/visualization/visualizing',
					// 	name: 'visualizingIndex', // 大屏可视化
					// 	component: () => import('/@/layout/routerView/link.vue'),
					// 	meta: {
					// 		title: 'message.router.visualizingIndex',
					// 		isLink: `#/visualizingDemo`,
					// 		isHide: false,
					// 		isKeepAlive: false,
					// 		isAffix: false,
					// 		isIframe: false,
					// 		roles: ['root', 'financial'],
					// 		icon: 'iconfont icon-caozuo-wailian',
					// 	},
					// },
				],
			},
			{
				path: '/personal',
				name: 'personal', // 个人中心
				component: () => import('/@/views/personal/index.vue'),
				meta: {
					title: 'message.router.personal',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['common'],
					icon: 'iconfont icon-gerenzhongxin',
				},
			},
			{
				path: '/system',
				name: 'SystemManager', // 服务管理
				component: () => import('/@/layout/routerView/parent.vue'),
				redirect: '/system/nacos',
				meta: {
					title: 'message.router.systemnManager',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['root'],
					icon: 'iconfont icon-gerenzhongxin',
				},
				children: [
					{
						path: '/system/nacos',
						name: 'NacosView',
						component: () => import('/@/layout/routerView/link.vue'),
						meta: {
							title: 'message.router.nacos',
							isLink: 'http://hidewnd.cn:8848/nacos/index.html',
							isHide: false,
							isKeepAlive: false,
							isAffix: false,
							isIframe: false,
							roles: ['root'],
							icon: 'iconfont icon-neiqianshujuchucun',
						},
					},
					{
						path: '/system/rabbitmq',
						name: 'RabbitMqView',
						component: () => import('/@/layout/routerView/link.vue'),
						meta: {
							title: 'message.router.rabbitMq',
							isLink: 'http://192.168.128.128:15672/#/',
							isHide: false,
							isKeepAlive: false,
							isAffix: false,
							isIframe: false,
							roles: ['root'],
							icon: 'iconfont icon-neiqianshujuchucun',
						},
					},
					{
						path: '/system/sentinel;',
						name: 'SenteninelVieww',
						component: () => import('/@/layout/routerView/link.vue'),
						meta: {
							title: 'message.router.sentinel',
							isLink: 'http://192.168.128.128:9999/',
							isHide: false,
							isKeepAlive: false,
							isAffix: false,
							isIframe: false,
							roles: ['root'],
							icon: 'iconfont icon-neiqianshujuchucun',
						},
					},
					// {
					// 	path: '/system/druid/system',
					// 	name: 'SystemDruidView',
					// 	component: () => import('/@/layout/routerView/link.vue'),
					// 	meta: {
					// 		title: 'message.router.systemDruid',
					// 		isLink: 'http://192.168.128.128:7050/druid/index.html',
					// 		isHide: false,
					// 		isKeepAlive: false,
					// 		isAffix: false,
					// 		isIframe: false,
					// 		roles: ['root'],
					// 		icon: 'iconfont icon-neiqianshujuchucun',
					// 	},
					// },
					// {
					// 	path: '/system/druid/material',
					// 	name: 'MaterialDruidView',
					// 	component: () => import('/@/layout/routerView/link.vue'),
					// 	meta: {
					// 		title: 'message.router.materialDruid',
					// 		isLink: 'http://192.168.128.128:7040/druid/index.html',
					// 		isHide: false,
					// 		isKeepAlive: false,
					// 		isAffix: false,
					// 		isIframe: false,
					// 		roles: ['root'],
					// 		icon: 'iconfont icon-neiqianshujuchucun',
					// 	},
					// },
				],
			},
			// {
			// 	path: '/workflow',
			// 	name: 'SystemManager', // 工作流
			// 	component: () => import('/@/layout/routerView/parent.vue'),
			// 	redirect: '/workflow/setting',
			// 	meta: {
			// 		title: 'message.router.workflow',
			// 		isLink: '',
			// 		isHide: false,
			// 		isKeepAlive: true,
			// 		isAffix: false,
			// 		isIframe: false,
			// 		roles: ['root'],
			// 		icon: 'iconfont icon-gerenzhongxin',
			// 	},
			// 	children : [
			// 		{
			// 			path: '/workflow/setting',
			// 			name: 'pagesWorkflow',
			// 			component: () => import('/@/views/workflow/index.vue'),
			// 			meta: {
			// 				title: 'message.router.workflowSetting',
			// 				isLink: '',
			// 				isHide: false,
			// 				isKeepAlive: true,
			// 				isAffix: false,
			// 				isIframe: false,
			// 				roles: ['root'],
			// 				icon: 'ele-Connection',
			// 			},
			// 		},
			// 	]
			// },
		],
	},
];

/**
 * 定义静态路由
 * @description 前端控制直接改 dynamicRoutes 中的路由，后端控制不需要修改，请求接口路由数据时，会覆盖 dynamicRoutes 第一个顶级 children 的内容（全屏，不包含 layout 中的路由出口）
 * @returns 返回路由菜单数据
 */
export const staticRoutes: Array<RouteRecordRaw> = [
	{
		path: '/login',
		name: 'login',
		component: () => import('/@/views/login/index.vue'),
		meta: {
			title: '登录',
		},
	},
	{
		path: '/404',
		name: 'notFound',
		component: () => import('/@/views/error/404.vue'),
		meta: {
			title: 'message.staticRoutes.notFound',
		},
	},
	{
		path: '/401',
		name: 'noPower',
		component: () => import('/@/views/error/401.vue'),
		meta: {
			title: 'message.staticRoutes.noPower',
		},
	},
	/**
	 * 提示：写在这里的为全屏界面，不建议写在这里
	 * 请写在 `dynamicRoutes` 路由数组中
	 */
	{
		path: '/visualizingDemo',
		name: 'visualizingDemo',
		component: () => import('/@/views/visualization/visualizing/demo1.vue'),
		meta: {
			title: 'message.router.visualizingIndex',
		},
	},
];
