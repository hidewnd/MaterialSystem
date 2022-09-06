<template>
	<div class="resources-material-rack">
		<el-card shadow="never" :header="$t('message.router.materialRack')">
			<div class="resources-system-search mb15">
				<el-form :model="tableData.param" size="default" label-width="90px">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label-width="auto">
							<el-input placeholder="请输入货架编号" style="max-width: 180px" v-model="tableData.param.arg2" clearable></el-input>
						</el-form-item>
					</el-col>
				</el-form>
				<el-button size="default" type="primary" class="ml10" @click="searchSubmit">
					<el-icon><ele-Search /></el-icon>
					查询
				</el-button>
				<el-button size="default" type="success" class="ml10" @click="onOpenAddPage">
					<el-icon><ele-FolderAdd /> </el-icon>
					新增货架
				</el-button>
				<el-button size="default" type="primary" class="ml10" @click="refreshTable">
					<el-icon> <ele-Refresh /> </el-icon>
					刷新
				</el-button>
			</div>

			<el-table :data="tableData.data" style="width: 100%">
				<el-table-column type="index" label="序号" width="60" />
				<el-table-column prop="rackId" label="货架编号" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="depotId" label="所属仓库编号" show-overflow-tooltip></el-table-column>
				<el-table-column prop="maxCapacity" label="货架最大容量" show-overflow-tooltip></el-table-column>
				<el-table-column prop="description" label="货架描述" show-overflow-tooltip></el-table-column>
				<el-table-column prop="createBy" label="创建者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="updateBy" label="更新者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
				<el-table-column prop="status" label="启用状态" show-overflow-tooltip>
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.status === 0">启用</el-tag>
						<el-tag type="info" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="100">
					<template #default="scope">
						<el-button :disabled="tableData.disable.indexOf(scope.row.name) >= 0" size="small" type="text" @click="onOpenEditPage(scope.row)">
							修改
						</el-button>
						<el-button :disabled="tableData.disable.indexOf(scope.row.name) >= 0" size="small" type="text" @click="onRowDel(scope.row)">
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

		<add-rack ref="addRef" @refresh="refreshTable" />
		<edit-rack ref="editRef" @refresh="refreshTable" />
	</div>
</template>

<script lang="ts">
import { defineComponent, onMounted, onUpdated, reactive, ref, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { verifyAndSpace } from '/@/utils/toolsValidate';
import { MaterialDepot, MaterialRack } from '/@/store/interface';
import { RackQueryAPI, RackUpdateAPI } from '/@/api/manager/material/rack';
import AddRack from './component/ResourcesAddRack.vue';
import EditRack from './component/ResourcesEditRack.vue';
import qs from 'qs';

interface TableDataState {
	tableData: {
		data: Array<MaterialRack>;
		total: number;
		loading: boolean;
		param: {
			type: string;
			page: number;
			size: number;
			arg1: string;
			arg2: string;
		};
		disable: Array<string>;
	};
}

export default defineComponent({
	name: 'ResourcesMaterialRack',
	components: { AddRack, EditRack },
	setup() {
		const addRef = ref();
		const editRef = ref();
		const state = reactive<TableDataState>({
			tableData: {
				data: [],
				total: 0,
				loading: false,
				param: {
					type: 'rack',
					page: 1,
					size: 10,
					arg1: 'rackId',
					arg2: '',
				},
				disable: [],
			},
		});

		// 打开添加页面
		function onOpenAddPage() {
			addRef.value.openDialog();
		}

		// 打开修改页面
		function onOpenEditPage(row: MaterialDepot) {
			editRef.value.openDialog(row);
		}

		// 刷新
		function refreshTable() {
			initTableData();
		}

		// 查询点击
		function searchSubmit() {
			state.tableData.param.arg2 = verifyAndSpace(state.tableData.param.arg2);
			if (state.tableData.param.arg2 == '' || state.tableData.param.arg2 == null || state.tableData.param.arg2 == undefined) {
				ElMessage.warning('查询条件不能为空');
				return;
			}
			initTableData();
			ElMessage.success('查询成功');
		}

		// 行删除
		function onRowDel(row: MaterialRack) {
			ElMessageBox.confirm(`此操作将永久删除“${row.rackId}”？是否继续?`, '提示', {
				confirmButtonText: '确认',
				cancelButtonText: '取消',
				type: 'warning',
			})
				.then(() => {
					RackUpdateAPI()
						.delete(row)
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
			RackQueryAPI()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
					state.tableData.loading = false;
				})
				.catch((err) => {
					state.tableData.loading = false;
				});
		}
		// 分页大小改变
		function onHandleSizeChange(val: number) {
			state.tableData.param.size = val;
			state.tableData.loading = true;
			RackQueryAPI()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableData(res.data);
					state.tableData.loading = false;
				})
				.catch((err) => {
					state.tableData.loading = false;
				});
		}

		// 初始化
		function initTableData() {
			state.tableData.loading = true;
			RackQueryAPI()
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
			const array: Array<MaterialRack> = [];
			let elements = JSON.stringify(data);
			elements = JSON.parse(elements);
			for (let i = 0; i < elements.length; i++) {
				let element: any = elements[i];
				array.push({
					rackId: element.rackId,
					depotId: element.depotId,
					maxCapacity: element.maxCapacity,
					description: element.description,
					status: element.status,
					delFlag: element.delFlag,
					createBy: element.createBy,
					createDate: element.createDate,
					updateBy: element.updateBy,
					updateDate: element.updateDate,
					materialList: [],
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

<style></style>
