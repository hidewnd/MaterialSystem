<template>
	<div class="system-account">
		<el-card shadow="hover" :header="$t('message.router.systemAccount')">
			<div class="system-user-search mb15">
				<el-form :model="searchParam" size="default" label-width="90px">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label-width="auto">
							<el-input placeholder="请输入用户名称" style="max-width: 180px" v-model="searchParam.arg" clearable></el-input>
						</el-form-item>
					</el-col>
				</el-form>
				<el-button size="default" type="primary" class="ml10" @click="searchSubmit">
					<el-icon><ele-Search /></el-icon>
					查询
				</el-button>
				<el-button size="default" type="success" class="ml10" @click="onOpenAddUser">
					<el-icon><ele-FolderAdd /> </el-icon>
					新增用户
				</el-button>
				<el-button size="default" type="primary" class="ml10" @click="refreshTable">
					<el-icon> <ele-Refresh /> </el-icon>
					刷新
				</el-button>
			</div>

			<el-table :data="tableData.data" style="width: 100%">
				<el-table-column type="index" label="序号" width="60" sortable />
				<el-table-column prop="accountId" label="账户编号" show-overflow-tooltip></el-table-column>
				<el-table-column prop="username" label="账户名称" show-overflow-tooltip></el-table-column>
				<el-table-column prop="createBy" label="创建者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="createDate" label="创建时间" show-overflow-tooltip></el-table-column>
				<el-table-column prop="updateBy" label="更新者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="updateDate" label="更新时间" show-overflow-tooltip></el-table-column>
				<el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
				<el-table-column prop="status" label="用户状态" show-overflow-tooltip>
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.status === 0">启用</el-tag>
						<el-tag type="info" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="100">
					<template #default="scope">
						<el-button :disabled="scope.row.username === 'root'" size="small" type="text" @click="onOpenEditUser(scope.row)"> 修改 </el-button>
						<el-button :disabled="scope.row.username === 'root'" size="small" type="text" @click="onRowDel(scope.row)"> 删除 </el-button>
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
		<AddUser ref="addUserRef" @refresh="refreshTable" />
		<EditUser ref="editUserRef" @refresh="refreshTable" />
	</div>
</template>

<script lang="ts">
import { defineComponent, onMounted, reactive, ref, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AddUser from './component/AddUser.vue';
import EditUser from './component/EditUser.vue';
import { ResourceSystemApi, SystemAccountApi } from '/@/api/resources/system';
import { SysAccount } from '/@/store/interface';
import qs from 'qs';
import { verifyAndSpace } from '/@/utils/toolsValidate';

interface TableDataState {
	tableData: {
		data: Array<SysAccount>;
		total: number;
		loading: boolean;
		param: {
			name: string;
			page: number;
			size: number;
		};
	};
	searchParam: {
		name: string;
		type: string;
		column: string;
		arg: string;
	};
}

export default defineComponent({
	name: 'systemAccount',
	components: { AddUser, EditUser },
	setup() {
		const addUserRef = ref();
		const editUserRef = ref();
		const state = reactive<TableDataState>({
			tableData: {
				data: [],
				total: 0,
				loading: false,
				param: {
					name: 'account',
					page: 1,
					size: 10,
				},
			},
			searchParam: {
				name: 'account',
				type: 'like',
				column: 'username',
				arg: '',
			},
		});

		function setTableData(data: any) {
			let elements = JSON.stringify(data.element);
			elements = JSON.parse(elements);
			setTableDataByList(elements);
			state.tableData.total = data.total;
		}

		function setTableDataByList(data: any) {
			const array: Array<SysAccount> = [];
			let elements = JSON.stringify(data);
			elements = JSON.parse(elements);
			if (elements) {
				for (let i = 0; i < elements.length; i++) {
					let element: any = elements[i];
					array.push({
						accountId: element.accountId,
						username: element.username,
						password: element.password,
						status: element.status,
						delFlag: element.delFlag,
						createBy: element.createBy,
						createDate: element.createDate,
						updateBy: element.updateBy,
						updateDate: element.updateDate,
						remark: element.remark,
						roles: element.roles,
					});
				}
				state.tableData.data = array;
			}
		}

		// 初始化表格数据
		function initTableData() {
			ResourceSystemApi()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
				});
		}
		// 打开新增用户弹窗
		function onOpenAddUser() {
			addUserRef.value.openDialog();
		}
		// 打开修改用户弹窗
		const onOpenEditUser = (row: SysAccount) => {
			editUserRef.value.openDialog(row);
		};
		// 删除用户
		const onRowDel = (row: SysAccount) => {
			ElMessageBox.confirm(`此操作将永久删除账户名称：“${row.username}”，是否继续?`, '提示', {
				confirmButtonText: '确认',
				cancelButtonText: '取消',
				type: 'warning',
			})
				.then(() => {
					SystemAccountApi()
						.updateAccount('type=delete&accountId=' + row.accountId + '&username=' + row.username)
						.then((res) => {
							ElMessage.success('删除成功');
							refreshTable();
						});
				})
				.catch(() => {});
		};
		// 分页改变
		const onHandleSizeChange = (val: number) => {
			state.tableData.param.size = val;
			ResourceSystemApi()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
				});
		};
		// 分页改变
		const onHandleCurrentChange = (val: number) => {
			state.tableData.param.page = val;
			ResourceSystemApi()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
				});
		};
		const refreshTable = () => {
			initTableData();
		};

		const searchSubmit = () => {
			state.searchParam.arg = verifyAndSpace(state.searchParam.arg);
			if (state.searchParam.arg == '' || state.searchParam.arg == null || state.searchParam.arg == undefined) {
				ElMessage.warning('查询条件不能为空');
				return;
			}
			const param: string = state.searchParam.name + '/' + state.searchParam.type + '/' + state.searchParam.column + '/' + state.searchParam.arg;
			ResourceSystemApi()
				.queryList(param)
				.then((res) => {
					setTableDataByList(res.data);
					ElMessage.success('查询成功');
					state.searchParam.arg = '';
				});
		};

		// 页面加载时
		onMounted(() => {
			initTableData();
		});

		return {
			addUserRef,
			editUserRef,
			setTableData,
			onOpenAddUser,
			onOpenEditUser,
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
