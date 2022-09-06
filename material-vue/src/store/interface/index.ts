// 接口类型声明

// 布局配置
// import exp from "constants";

export interface ThemeConfigState {
	themeConfig: {
		isDrawer: boolean;
		primary: string;
		topBar: string;
		topBarColor: string;
		isTopBarColorGradual: boolean;
		menuBar: string;
		menuBarColor: string;
		isMenuBarColorGradual: boolean;
		columnsMenuBar: string;
		columnsMenuBarColor: string;
		isColumnsMenuBarColorGradual: boolean;
		isCollapse: boolean;
		isUniqueOpened: boolean;
		isFixedHeader: boolean;
		isFixedHeaderChange: boolean;
		isClassicSplitMenu: boolean;
		isLockScreen: boolean;
		lockScreenTime: number;
		isShowLogo: boolean;
		isShowLogoChange: boolean;
		isBreadcrumb: boolean;
		isTagsview: boolean;
		isBreadcrumbIcon: boolean;
		isTagsviewIcon: boolean;
		isCacheTagsView: boolean;
		isSortableTagsView: boolean;
		isShareTagsView: boolean;
		isFooter: boolean;
		isGrayscale: boolean;
		isInvert: boolean;
		isIsDark: boolean;
		isWartermark: boolean;
		wartermarkText: string;
		tagsStyle: string;
		animation: string;
		columnsAsideStyle: string;
		columnsAsideLayout: string;
		layout: string;
		isRequestRoutes: boolean;
		globalTitle: string;
		globalViceTitle: string;
		globalI18n: string;
		globalComponentSize: string;
	};
}

// 路由列表
export interface RoutesListState {
	routesList: object[];
	isColumnsMenuHover: Boolean;
	isColumnsNavHover: Boolean;
}

// 路由缓存列表
export interface KeepAliveNamesState {
	keepAliveNames: string[];
}

// TagsView 路由列表
export interface TagsViewRoutesState {
	tagsViewRoutes: object[];
	isTagsViewCurrenFull: Boolean;
}

// 用户信息
export interface UserInfosState {
	userInfos: {
		authBtnList: string[];
		photo: string;
		roles: string[];
		time: number;
		userName: string;
		userId: string;
		password: string;
	};
}

// 后端返回原始路由(未处理时)
export interface RequestOldRoutesState {
	requestOldRoutes: object[];
}

// 主接口(顶级类型声明)
export interface RootStateTypes {
	themeConfig: ThemeConfigState;
	routesList: RoutesListState;
	keepAliveNames: KeepAliveNamesState;
	tagsViewRoutes: TagsViewRoutesState;
	userInfos: UserInfosState;
	requestOldRoutes: RequestOldRoutesState;
}

export interface  BaseEntity{
	createBy: string,
	createDate: string,
	updateBy: string,
	updateDate: string,
	remark: string,
}
// 用户信息接口
export interface SysAccount extends BaseEntity{
	accountId: string,
	username: string,
	password: string,
	status: number,
	delFlag: number,
	roles: Array<SysRole> | undefined,
}
// 用户详情
export interface SysAccountInfo extends BaseEntity{
	accountId: string,
	sex: string,
	age: number,
	phone: string,
	email:string,
	avatar: string,
	sign: string,
}
// 系统权限
export interface SysMenu extends BaseEntity{
	menuId: string ,
	parentId: string,
	name: string,
	url: string,
	perms: string,
	status: number,
	delFlag: number,
	parent: SysMenu | undefined,
}
// 系统权限
export interface SysRole extends BaseEntity{
	roleId: string,
	roleName: string,
	roleNameZh: string,
	roleSort: number,
	stated: number,
	delFlag: number,
	menus: Array<SysMenu> | undefined,
}
// 物料
export interface Material extends BaseEntity{
	materialId: string,
	typeId: string,
	materialName: string,
	materialNameZh: string,
	value: number,
	capacityRatio: number,
	message: string,
	status: number,
	delFlag: number,
}
// 物料类型
export interface MaterialType extends BaseEntity{
	typeId: string,
	parentId: string,
	typeName: string,
	typeNameZh: string,
	status: number,
	delFlag: number,
	childList: Array<MaterialType> | undefined,
}

// 仓库
export interface MaterialDepot extends BaseEntity{
	depotId: string,
	depotName: string,
	depotNameZh: string,
	place: string,
	description: string,
	maxCapacity: number,
	capacity: number,
	threshold: number,
	message: string,
	status: number,
	delFlag: number,
	rackList: Array<MaterialRack> | undefined,
}

// 货架
export interface MaterialRack extends BaseEntity{
	rackId: string,
	depotId: string,
	maxCapacity: number,
	description: string,
	status: number,
	delFlag: number,
	materialList: Array<Material>,
}

// 出入库记录
export interface Registration extends BaseEntity{
	regId: string,
	regSign: number,
	number: number,
	value: number,
	status: number,
	delFlag: number,
	materialList: Array<Material> | undefined,
}

// 入库记录
export interface EnterRecord extends BaseEntity{
	recordId: string,
	sign: number,
	number: number,
	value: number,
	status: number,
	delFlag: number,
	recordStatus: number,
	executeApproval: number,
	applyApproval: number,
	materialList: Array<Object>,
	matNumber: Array<Object>,
	numberMap: Array<Object>,
}

// 出库记录
export interface OuterRecord extends BaseEntity{
	recordId: string,
	sign: number,
	number: number,
	value: number,
	status: number,
	delFlag: number,
	proof: string,
	sm2key: string,
	recordStatus: number,
	executeApproval: number,
	applyApproval: number,
	materialMap: Array<Object>,
	materialNumberMap: Array<Object>,
	materialTotalNumber: Array<Object>,
}