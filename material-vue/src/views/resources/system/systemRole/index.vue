<template>
	<div class="resources-system-role">
		<el-card shadow="never" :header="$t('message.router.systemRole')">
			<div class="resources-system-search mb15">
				<el-form :model="searchParam" size="default" label-width="90px">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label-width="auto">
							<el-input placeholder="请输入角色名称" style="max-width: 180px" v-model="searchParam.arg" clearable></el-input>
						</el-form-item>
					</el-col>
				</el-form>
				<el-button size="default" type="primary" class="ml10" @click="searchSubmit">
					<el-icon><ele-Search /></el-icon>
					查询
				</el-button>
				<el-button size="default" type="success" class="ml10" @click="onOpenAddPage">
					<el-icon><ele-FolderAdd /> </el-icon>
					新增角色
				</el-button>
				<el-button size="default" type="primary" class="ml10" @click="refreshTable">
					<el-icon> <ele-Refresh /> </el-icon>
					刷新
				</el-button>
			</div>

			<el-table :data="tableData.data" style="width: 100%">
				<el-table-column type="index" label="序号" width="60" />
				<el-table-column prop="roleId" label="角色编号" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="roleName" label="角色系统名称" show-overflow-tooltip></el-table-column>
				<el-table-column prop="roleNameZh" label="角色中文名称" show-overflow-tooltip></el-table-column>
				<el-table-column prop="roleSort" label="角色排序号" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="createBy" label="创建者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="createDate" label="创建时间" show-overflow-tooltip></el-table-column>
				<el-table-column prop="updateBy" label="更新者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="updateDate" label="更新时间" show-overflow-tooltip></el-table-column>
				<el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
				<el-table-column prop="stated" label="启用状态" show-overflow-tooltip>
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.stated === 0">启用</el-tag>
						<el-tag type="info" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="100">
					<template #default="scope">
						<el-button :disabled="tableData.disable.indexOf(scope.row.roleName) >= 0" size="small" type="text" @click="onOpenEditPage(scope.row)">
							修改
						</el-button>
						<el-button :disabled="tableData.disable.indexOf(scope.row.roleName) >= 0" size="small" type="text" @click="onRowDel(scope.row)">
							删除
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				@size-change="onHandleSizeChange"
				@current-change="onHandleCurrentChange"
				class="mt15"
				:pager-count="5"
				:page-sizes="[10, 20, 30]"
				v-model:current-page="tableData.param.page"
				background
				v-model:page-size="tableData.param.size"
				layout="total, sizes, prev, pager, next, jumper"
				:total="tableData.total"
			>
			</el-pagination>
		</el-card>
		<add-role ref="addRef" @refresh="refreshTable" />
		<edit-role ref="editRef" @refresh="refreshTable" />
	</div>
</template>

<script lang="ts">
import { defineComponent, onMounted, reactive, ref, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { verifyAndSpace } from '/@/utils/toolsValidate';
import { SysRole } from '/@/store/interface';
import AddRole from './component/AddRole.vue';
import EditRole from './component/EditRole.vue';
import { ResourceSystemApi, SystemRoleApi } from '/@/api/resources/system';
import qs from 'qs';
import { Session } from '/@/utils/storage';
interface TableDataState {
	tableData: {
		data: Array<SysRole>;
		total: number;
		loading: boolean;
		param: {
			name: string;
			page: number;
			size: number;
		};
		disable: Array<string>;
	};
	searchParam: {
		name: string;
		type: string;
		column: string;
		arg: string;
	};
}
export default defineComponent({
	name: 'ResourcesSystemRole',
	components: { AddRole, EditRole },
	setup() {
		const addRef = ref();
		const editRef = ref();
		const state = reactive<TableDataState>({
			tableData: {
				data: [],
				total: 0,
				loading: false,
				param: {
					name: 'role',
					page: 1,
					size: 10,
				},
				disable: ['root', 'warehouse', 'financial'],
			},
			searchParam: {
				name: 'role',
				type: 'like',
				column: 'roleName',
				arg: '',
			},
		});

		// 打开添加页面
		function onOpenAddPage() {
			addRef.value.openDialog();
		}

		// 打开修改页面
		function onOpenEditPage(row: SysRole) {
			editRef.value.openDialog(row);
		}

		// 刷新
		function refreshTable() {
			initTableData();
		}

		// 查询点击
		function searchSubmit() {
			state.searchParam.arg = verifyAndSpace(state.searchParam.arg);
			if (state.searchParam.arg == '' || state.searchParam.arg == null || state.searchParam.arg == undefined) {
				ElMessage.warning('查询条件不能为空');
				return;
			}
			const param: string = `${state.searchParam.name}/${state.searchParam.type}/${state.searchParam.column}/${state.searchParam.arg}`;
			ResourceSystemApi()
				.queryList(param)
				.then((res) => {
					setTableDataByList(res.data);
					ElMessage.success('查询成功');
					state.searchParam.arg = '';
				});
		}

		// 行删除
		function onRowDel(row: SysRole) {
			ElMessageBox.confirm(`此操作将永久删除“${row.roleName}”角色？是否继续?`, '提示', {
				confirmButtonText: '确认',
				cancelButtonText: '取消',
				type: 'warning',
			})
				.then(() => {
					if (['root', 'warehouse', 'financial'].indexOf(row.roleName) >= 0) {
						ElMessage.error('该角色无法删除');
						return;
					}
					SystemRoleApi()
						.setRole('type=delete&roleId=' + row.roleId + '&roleName=' + row.roleName)
						.then((res) => {
							ElMessage.success('删除成功');
							refreshTable();
						});
				})
				.catch(() => {});
		}

		// 分页改变
		function onHandleCurrentChange(val: number) {
			state.tableData.param.page = val;
			state.tableData.loading = true;
			ResourceSystemApi()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
					state.tableData.loading = false;
				})
				.catch((res) => {
					state.tableData.loading = false;
				});
		}
		// 分页大小改变
		function onHandleSizeChange(val: number) {
			state.tableData.param.size = val;
			state.tableData.loading = true;
			ResourceSystemApi()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
					state.tableData.loading = false;
				})
				.catch((res) => {
					state.tableData.loading = false;
				});
		}

		// 初始化
		function initTableData() {
			state.tableData.loading = true;
			ResourceSystemApi()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
				});
			state.tableData.loading = false;
		}

		// 设置表格数据
		function setTableData(data: any) {
			let elements = JSON.stringify(data.element);
			elements = JSON.parse(elements);
			setTableDataByList(elements);
			state.tableData.total = data.total;
		}

		function setTableDataByList(data: any) {
			const array: Array<SysRole> = [];
			let elements = JSON.stringify(data);
			elements = JSON.parse(elements);
			for (let i = 0; i < elements.length; i++) {
				let element: any = elements[i];
				array.push({
					roleId: element.roleId,
					roleName: element.roleName,
					roleNameZh: element.roleNameZh,
					roleSort: element.roleSort,
					stated: element.stated,
					delFlag: element.delFlag,
					menus: [],
					createBy: element.createBy,
					createDate: element.createDate,
					updateBy: element.updateBy,
					updateDate: element.updateDate,
					remark: element.remark,
				});
			}
			state.tableData.data = array;
		}

		// 页面加载时
		onMounted(() => {
			initTableData();
		});

		return {
			addRef,
			editRef,
			onOpenAddPage,
			onOpenEditPage,
			refreshTable,
			searchSubmit,
			onRowDel,
			onHandleCurrentChange,
			onHandleSizeChange,
			...toRefs(state),
		};
	},
});
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';
.resources-system-role {
	position: relative;
}
</style>
