<template>
	<div class="record-outer-query">
		<el-card shadow="never" :header="$t('message.router.recordQuery')">
			<div class="resources-system-search mb15">
				<el-button size="default" type="primary" class="ml10" @click="refreshTable">
					<el-icon> <ele-Refresh /> </el-icon>
					刷新
				</el-button>
			</div>
			<el-table :data="tableData.data" style="width: 100%" stripe border>
				<el-table-column type="index" label="序号" width="60" />
				<el-table-column prop="recordId" label="出库记录ID" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="number" label="记录总数" show-overflow-tooltip></el-table-column>
				<el-table-column prop="value" label="记录总价值" :formatter="formatterValue" show-overflow-tooltip></el-table-column>
				<el-table-column prop="recordStatus" label="记录当前状态" show-overflow-tooltip></el-table-column>
				<el-table-column prop="applyApproval" label="申请结果" :formatter="formatterApply" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="executeApproval" label="执行审批结果" :formatter="formatterExecute" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="createBy" label="创建者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="updateBy" label="更新者" show-overflow-tooltip></el-table-column>
				<el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
				<el-table-column label="操作" width="100">
					<template #default="scope">
						<el-button :disabled="scope.row.status < 0 || scope.row.status >= 4" size="small" type="text" @click="onRowDel(scope.row)">
							关闭
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
			<el-divider border-style="dashed" />
			<el-card shadow="always" header="出库记录状态明细">
				<el-steps :space="400" :active="1" :align-center="true" finish-status="finish">
					<el-step
						v-for="(item, index) in tableData.timelineData"
						:key="index"
						:title="item.title"
						:description="item.description"
						:status="item.status"
						:icon="item.icon"
					>
					</el-step>
				</el-steps>
			</el-card>
		</el-card>
	</div>
</template>

<script lang="ts">
import { defineComponent, markRaw, onMounted, reactive, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { verifyAndSpace, verifyNumberComma } from '/@/utils/toolsValidate';
import { OuterRecord } from '/@/store/interface';
import { RecordCommonAPI } from '/@/api/manager/material/record';
import { Edit, Cloudy, EditPen, Document, Refresh, Upload } from '@element-plus/icons-vue';
import qs from 'qs';

interface TableDataState {
	tableData: {
		data: Array<OuterRecord>;
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
		timelineData: Array<object>;
	};
}

export default defineComponent({
	name: 'recordOuterQuery',
	components: {
		Edit: markRaw(Edit),
		Cloudy: markRaw(Cloudy),
		EditPen: markRaw(EditPen),
		Document: markRaw(Document),
		Refresh: markRaw(Refresh),
		Upload: markRaw(Upload),
	},
	setup() {
		const state = reactive<TableDataState>({
			tableData: {
				data: [],
				total: 0,
				loading: false,
				param: {
					type: 'outer',
					page: 1,
					size: 10,
					arg1: '',
					arg2: '',
				},
				disable: [],
				timelineData: [
					{ title: '填写申请', description: '出库记录填写', icon: Edit, status: 'process' },
					{ title: '提交申请', description: '出库记录提交', icon: Upload, status: 'process' },
					{ title: '记录初始化', description: '出库记录初始化', icon: Refresh, status: 'process' },
					{ title: '出库记录审核', description: '出库记录审核中', icon: Cloudy, status: 'process' },
					{ title: '出库物料分配', description: '分配出库物料清单', icon: Document, status: 'process' },
					{ title: '获取凭证', description: '获取出库所需凭证等', icon: Document, status: 'process' },
					{ title: '物料核销', description: '物料核对出库', icon: EditPen, status: 'process' },
					{ title: '出库完成', description: '出库记录结束存档', icon: Document, status: 'finish' },
				],
			},
		});

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
			RecordCommonAPI()
				.queryPage(qs.stringify(state.tableData.param))
				.then((res) => {
					setTableDataByList(res.data);
					ElMessage.success('查询成功');
					state.tableData.param.arg2 = '';
				});
		}

		// 行删除
		function onRowDel(row: OuterRecord) {
			ElMessageBox.confirm(`确认关闭出库记录[${row.recordId}]?`, '提示', {
				confirmButtonText: '确认',
				cancelButtonText: '取消',
				type: 'warning',
			})
				.then(() => {
					if (row.status < 0 || row.status >= 4) {
						setTimeout(() => {
							ElMessageBox.alert('该记录已取消或已完成，无法再次关闭', '取消失败', {
								confirmButtonText: '确认',
								type: 'error',
							});
						}, 500);
						return;
					}
					RecordCommonAPI()
						.closeRecord(`recordId=${row.recordId}`)
						.then((res) => {
							ElMessage.success('关闭出库记录成功');
							refreshTable();
						});
				})
				.catch(() => {});
		}

		// 分页改变
		function onHandleCurrentChange(val: number) {
			state.tableData.param.page = val;
			state.tableData.loading = true;
			RecordCommonAPI()
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
			RecordCommonAPI()
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
			RecordCommonAPI()
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
			const array: Array<OuterRecord> = [];
			let elements = JSON.stringify(data);
			elements = JSON.parse(elements);
			for (let i = 0; i < elements.length; i++) {
				let element: any = elements[i];
				array.push({
					recordId: element.recordId,
					sign: element.sign,
					number: element.number,
					value: element.value,
					proof: element.proof,
					sm2key: '',
					status: element.status,
					delFlag: element.delFlag,
					recordStatus: element.recordStatus,
					executeApproval: element.executeApproval,
					applyApproval: element.applyApproval,
					materialMap: element.materialMap,
					materialNumberMap: element.materialNumberMap,
					materialTotalNumber: element.materialTotalNumber,
					createBy: element.createBy,
					updateBy: element.updateBy,
					createDate: element.createDate,
					updateDate: element.updateDate,
					remark: element.remark,
				});
			}
			state.tableData.data = array;
			
		}

		function formatterValue(row: OuterRecord) {
			return verifyNumberComma(row.value + '');
		}

		function formatterApply(row: OuterRecord) {
			return formatter(row.applyApproval);
		}

		function formatterExecute(row: OuterRecord) {
			return formatter(row.applyApproval);
		}

		function formatter(approval: number) {
			if (approval === 1) {
				return '审核通过';
			}
			if (approval === 2) {
				return '审核失败';
			}
			return '尚未审批';
		}

		// 页面加载时
		onMounted(() => {
			initTableData();
		});

		return {
			refreshTable,
			searchSubmit,
			onRowDel,
			onHandleCurrentChange,
			onHandleSizeChange,
			formatterValue,
			formatterApply,
			formatterExecute,
			...toRefs(state),
		};
	},
});
</script>

<style></style>
