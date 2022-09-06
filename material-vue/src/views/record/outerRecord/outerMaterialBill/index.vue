<template>
	<div class="record-outer-material-bill">
		<el-card shadow="alway" :header="$t('message.router.outerMaterialBill')">
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<div class="resources-system-search mb15">
						<el-form :model="searchParam" size="default" label-width="90px">
							<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
								<el-form-item label-width="auto">
									<el-select
										v-model="searchParam.param"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入出库编号"
										:remote-method="RecordRemote"
										:loading="searchParam.loading"
										@change="RecordSelectChange"
										autofocus="false"
										style="max-width: 180px"
									>
										<el-option v-for="item in searchParam.outerrecordList" :key="item.value" :label="item.value" :value="item.value" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-form>
						<el-button size="default" type="primary" class="ml10" @click="searchSubmit">
							<el-icon><ele-Search /></el-icon>
							查询
						</el-button>
						<el-button size="default" type="success" class="ml10" @click="showPoof" :disabled="!searchParam.param">
							<el-icon><ele-Expand /> </el-icon>
							查看凭证
						</el-button>
						<el-button size="default" type="primary" class="ml10" @click="refreshTable">
							<el-icon><ele-Refresh /></el-icon>
							刷新
						</el-button>
					</div>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-table :data="tableData.data" border style="width: 100%">
						<el-table-column prop="materialId" label="物料编号" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="materialName" label="物料系统名" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="materialNameZh" label="物料中文名" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="number" label="出库数量" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="location" label="库存地址" show-overflow-tooltip></el-table-column>
						<el-table-column fixed="right" label="操作" width="120">
							<template #default="scope">
								<el-button type="text" size="small" @click.prevent="openInfo(scope.row)"> 查看详情 </el-button>
							</template>
						</el-table-column>
					</el-table>
				</el-col>
			</el-row>

			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-steps :space="400" :active="5" align-center finish-status="finish">
						<el-step v-for="(item, index) in timelineData" :key="index" :title="item.title" :description="item.description" :icon="item.icon">
						</el-step>
					</el-steps>
				</el-col>
			</el-row>
		</el-card>

		<query-proof ref="proofRef"></query-proof>
		<query-info ref="infoRef"></query-info>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, onMounted, ref, markRaw } from 'vue';
import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults';
import { OuterRecordAPI, RecordCommonAPI } from '/@/api/manager/material/record';
import { verifyAndSpace } from '/@/utils/toolsValidate';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import queryProof from './component/RecordQueryProof.vue';
import queryInfo from './component/RecordQueryMaterialInfo.vue';
import { Edit, Cloudy, EditPen, Document, Upload, Refresh, Search } from '@element-plus/icons-vue';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import { info } from 'console';
interface TableItem {
	materialId: string;
	materialName: string;
	materialNameZh: string;
	number: string;
	location: string;
}

interface SelectItem {
	value: string;
}

interface FormState {
	ruleform: {};
	tableData: {
		data: Array<TableItem>;
		total: number;
		loading: boolean;
		param: object;
	};
	searchParam: {
		loading: boolean;
		param: string;
		outerrecordList: Array<SelectItem>;
	};
	timelineData: Array<object>;
}

interface SpanMethodProps {
	row: TableItem;
	column: TableColumnCtx<TableItem>;
	rowIndex: number;
	columnIndex: number;
}

export default defineComponent({
	name: 'RecordOuterMaterialBill',
	components: {
		queryProof,
		queryInfo,
		Edit: markRaw(Edit),
		Cloudy: markRaw(Cloudy),
		EditPen: markRaw(EditPen),
		Document: markRaw(Document),
		Upload: markRaw(Upload),
		Refresh: markRaw(Refresh),
	},
	setup() {
		const proofRef = ref();
		const infoRef = ref();
		const { t } = useI18n();
		//全局state
		const state = reactive<FormState>({
			ruleform: {},
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
			},
			searchParam: {
				loading: false,
				param: '',
				outerrecordList: [],
			},
			timelineData: [
				{ title: '填写申请', description: '出库记录填写', icon: Edit, status: 'process' },
				{ title: '提交申请', description: '出库记录提交', icon: Upload, status: 'process' },
				{ title: '记录初始化', description: '出库记录初始化', icon: Refresh, status: 'process' },
				{ title: '出库记录审核', description: '出库记录审核中', icon: Cloudy, status: 'process' },
				{ title: '出库物料分配', description: '分配出库物料清单', icon: Document, status: 'process' },
				{ title: '获取凭证', description: '查询分配物料库存地址及数量，获取出库所需凭证等出库必要信息', icon: Document, status: 'process' },
				{ title: '物料核销', description: '物料核对出库', icon: EditPen, status: 'process' },
				{ title: '出库完成', description: '出库记录结束存档', icon: Document, status: 'finish' },
			],
		});

		const objectSpanMethod = ({ row, column, rowIndex, columnIndex }: SpanMethodProps) => {
			if (columnIndex === 0) {
				if (rowIndex % 2 === 0) {
					return {
						rowspan: 2,
						colspan: 1,
					};
				} else {
					return {
						rowspan: 0,
						colspan: 0,
					};
				}
			}
		};

		// 提交查询
		function searchSubmit() {
			if (!verifyAndSpace(state.searchParam.param)) {
				ElMessage.warning('搜索框不能为空');
				return;
			}
			OuterRecordAPI()
				.queryMaterialList(`recordId=${state.searchParam.param}`)
				.then((res) => {
					let list: any = JSON.stringify(res.data);
					list = JSON.parse(list);
					const array: Array<TableItem> = [];
					if (list) {
						for (let id in list) {
							const numberlist: any = list[id];
							if (numberlist) {
								for (let number in numberlist) {
									const location = numberlist[number];
									array.push({ materialId: id, materialName: '', number: number, location: location, materialNameZh: '' });
								}
							}
						}
						state.tableData.data = array;
						state.tableData.total = res.data.total;
						initName();
					}
				});
		}

		function initName() {
			if (state.tableData.data.length > 0) {
				MaterialQueryAPI()
					.queryList(`way=all&type=material`)
					.then((res) => {
						let list: any = JSON.stringify(res.data);
						list = JSON.parse(list);
						if (list) {
							for (let i = 0; i < state.tableData.data.length; i++) {
								const element = state.tableData.data[i];
								let material = list.find((item: any) => item.materialId === element.materialId);
								if (material != undefined) {
									state.tableData.data[i].materialName = material.materialName;
									state.tableData.data[i].materialNameZh = material.materialNameZh;
								}
							}
						}
					});
			}
		}

		// 刷新列表
		function refreshTable() {
			if (!state.searchParam.param) {
				ElMessage.warning('当前查询参数为空！');
			} else {
				searchSubmit();
			}
		}

		// select输入查询
		function RecordRemote(query: any) {
			if (query) {
				state.searchParam.loading = true;
				RecordCommonAPI()
					.queryPage(`type=outer&arg1=recordId&arg2=${query}`)
					.then((res) => {
						let list: any = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<SelectItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								array.push({ value: element.recordId });
							}
							state.searchParam.outerrecordList = array;
						}
						state.searchParam.loading = false;
					})
					.catch((err) => {
						state.searchParam.loading = false;
					});
			} else {
				state.searchParam.loading = true;
				RecordCommonAPI()
					.queryPage(`type=outer`)
					.then((res) => {
						let list: any = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<SelectItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								array.push({ value: element.recordId });
							}
							state.searchParam.outerrecordList = array;
						}
						state.searchParam.loading = false;
					})
					.catch((err) => {
						state.searchParam.loading = false;
					});
			}
		}

		//select选中事件
		function RecordSelectChange(recordId: any) {
			state.searchParam.param = recordId;
			searchSubmit();
		}

		// 查看凭证
		function showPoof() {
			if (!state.searchParam.param) {
				ElMessage.error('请先选择出库记录！');
				return;
			}
			proofRef.value.openDialog(state.searchParam.param);
		}

		// 查看详情
		function openInfo(row: any) {
			if (!state.searchParam.param) {
				ElMessage.error('请先选择出库记录！');
				return;
			}
			infoRef.value.openDialog({
				recordId: state.searchParam.param,
				materialId: row.materialId,
				materialName: row.materialName,
				materialNameZh: row.materialNameZh,
				location: row.location,
				number: row.number,
			});
		}

		// 初始化事件
		function initEvent() {
			RecordCommonAPI()
				.queryPage(`type=outer`)
				.then((res) => {
					let list: any = JSON.stringify(res.data.element);
					list = JSON.parse(list);
					if (list) {
						for (let i = 0; i < list.length; i++) {
							const element: any = list[i];
							if ((element && element.executeApproval) || element.status == 2) {
								state.searchParam.outerrecordList.push({ value: element.recordId });
							}
						}
					}
				});
		}

		// 页面初始化
		onMounted(() => {
			initEvent();
		});

		return {
			proofRef,
			infoRef,
			showPoof,
			searchSubmit,
			refreshTable,
			objectSpanMethod,
			RecordRemote,
			RecordSelectChange,
			openInfo,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.record-outer-bill {
	width: 100%;
	height: 100%;
	position: relative;
}
</style>
