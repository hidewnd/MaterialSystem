<template>
	<div class="resources-material-material">
		<el-card shadow="never" :header="$t('message.router.materialMaterial')">
			<div class="resources-system-search mb15">
				<el-form :model="tableData.param" size="default" label-width="90px">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label-width="auto">
							<el-input placeholder="请输入物料名称" style="max-width: 180px" v-model="tableData.param.arg2" clearable></el-input>
						</el-form-item>
					</el-col>
				</el-form>
				<el-button size="default" type="primary" class="ml10" @click="searchSubmit">
					<el-icon><ele-Search /></el-icon>
					查询
				</el-button>
				<el-button size="default" type="success" class="ml10" @click="onOpenAddPage">
					<el-icon><ele-FolderAdd /> </el-icon>
					新增物料
				</el-button>
				<el-button size="default" type="primary" class="ml10" @click="refreshTable">
					<el-icon> <ele-Refresh /> </el-icon>
					刷新
				</el-button>
				<el-button size="default" type="primary" class="ml10" @click="exportData">
					<el-icon> <ele-Download /> </el-icon>
					导出
				</el-button>
			</div>

			<el-table :data="tableData.data" style="width: 100%">
				<el-table-column type="index" label="序号" width="60" />
				<el-table-column prop="materialId" label="物料编号" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="typeId" label="物料类型" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="materialName" label="权限系统名" show-overflow-tooltip></el-table-column>
				<el-table-column prop="materialNameZh" label="物料中文名" show-overflow-tooltip></el-table-column>
				<el-table-column prop="value" label="物料价值" show-overflow-tooltip></el-table-column>
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
		<add-material ref="addRef" @refresh="refreshTable" />
		<edit-material ref="editRef" @refresh="refreshTable" />
	</div>
</template>

<script lang="ts">
import { defineComponent, onMounted, reactive, ref, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { verifyAndSpace } from '/@/utils/toolsValidate';
import { Material } from '/@/store/interface';
import { MaterialQueryAPI, MaterialUpdateAPI } from '/@/api/manager/material/material';
import AddMaterial from './component/ResourcesAddMaterial.vue';
import EditMaterial from './component/ResourcesEditMaterial.vue';
import qs from 'qs';
import { blob } from 'stream/consumers';
import { CommonApi } from '/@/api/common';

interface TableDataState {
	tableData: {
		data: Array<Material>;
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
	name: 'ResourcesMaterialMaterial',
	components: { AddMaterial, EditMaterial },
	setup() {
		const addRef = ref();
		const editRef = ref();
		const state = reactive<TableDataState>({
			tableData: {
				data: [],
				total: 0,
				loading: false,
				param: {
					type: 'material',
					page: 1,
					size: 10,
					arg1: 'materialName',
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
		function onOpenEditPage(row: Material) {
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
			MaterialQueryAPI()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableDataByList(res.data.element);
					ElMessage.success('查询成功');
					state.tableData.param.arg2 = '';
				});
		}

		// 行删除
		function onRowDel(row: Material) {
			ElMessageBox.confirm(`此操作将永久删除“${row.materialName}”？是否继续?`, '提示', {
				confirmButtonText: '确认',
				cancelButtonText: '取消',
				type: 'warning',
			})
				.then(() => {
					MaterialUpdateAPI()
						.deleteMaterial(row)
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
			MaterialQueryAPI()
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
			MaterialQueryAPI()
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
			MaterialQueryAPI()
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
			const array: Array<Material> = [];
			let elements = JSON.stringify(data);
			elements = JSON.parse(elements);
			for (let i = 0; i < elements.length; i++) {
				let element: any = elements[i];
				array.push({
					materialId: element.materialId,
					typeId: element.typeId,
					materialName: element.materialName,
					materialNameZh: element.materialNameZh,
					value: element.value,
					capacityRatio: element.capacityRatio,
					message: element.message,
					status: element.status,
					delFlag: element.delFlag,
					createBy: element.createBy,
					createDate: element.createDate,
					updateBy: element.updateBy,
					updateDate: element.updateDate,
					remark: element.remark,
				});
			}
			state.tableData.data = array;
			state.tableData.total = array.length;
		}

		function exportData() {
			CommonApi()
				.downloadMaterial()
				.then((res: any) => {
					// debugger;
					let blob = new Blob([res], { type: 'application/xlsx' });
					let url = window.URL.createObjectURL(blob);
					const link = document.createElement('a'); //创建a标签
					link.href = url;
					link.download = '登记物料数据.xlsx'; //重命名文件
					link.click();
					URL.revokeObjectURL(url);
				});
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
			exportData,
			...toRefs(state),
		};
	},
});
</script>

<style></style>
