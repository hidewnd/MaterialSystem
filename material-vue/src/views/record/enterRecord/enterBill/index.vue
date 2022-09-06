<template>
	<div class="record-approve">
		<el-card shadow="alway" :header="$t('message.router.recordApprove')">
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<div class="resources-system-search mb15" style="align: center">
						<el-form :model="tableData.param" label-width="90px">
							<el-col :xs="2" :sm="2" :md="2" :lg="2" :xl="2" class="mb30" style="margin-right: 10px !important">
								<el-select v-model="tableData.param.type" class="m-2" placeholder="Select" size="large">
									<el-option v-for="item in tableData.signSelect" :key="item.value" :label="item.lable" :value="item.value" />
								</el-select>
							</el-col>
							<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
								<el-form-item label-width="auto">
									<el-select
										v-model="tableData.param.arg"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入入库编号查询"
										:remote-method="AccountRemote"
										:loading="searchParam.loading"
										@change="AccountSelectChange"
										autofocus="false"
										style="max-width: 180px"
									>
										<el-option v-for="item in searchParam.result" :key="item.recordId" :label="item.recordId" :value="item.recordId" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-form>
						<el-button size="large" type="primary" class="ml10" @click="searchSubmit">
							<el-icon><ele-Search /></el-icon>
							查询
						</el-button>
						<el-button size="large" type="primary" class="ml10" @click="refreshTable">
							<el-icon><ele-Refresh /></el-icon>
							刷新
						</el-button>
					</div>
				</el-col>
			</el-row>

			<el-empty description="请选择查询入库记录" v-if="!tableData.isShow" />
			<template v-else>
				<el-table :data="tableData.data" style="width: 100%">
					<el-table-column type="index" label="序号" width="60" />
					<el-table-column prop="materialId" label="物料编号" show-overflow-tooltip sortable></el-table-column>
					<el-table-column prop="name" label="物料系统名" show-overflow-tooltip sortable></el-table-column>
					<el-table-column prop="nameZh" label="物料中文名" show-overflow-tooltip></el-table-column>
					<el-table-column prop="number" label="记录物料数" show-overflow-tooltip sortable></el-table-column>
				</el-table>
			</template>
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20"> </el-col>
			</el-row>

			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-steps :space="400" :active="3" align-center finish-status="finish">
						<el-step v-for="(item, index) in timelineData" :key="index" :title="item.title" :description="item.description" :icon="item.icon">
						</el-step>
					</el-steps>
				</el-col>
			</el-row>
		</el-card>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw, onMounted, watch, ref } from 'vue';
import { Edit, Cloudy, EditPen, Document, Upload, Coin } from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';
import { EnterRecordAPI, RecordCommonAPI } from '/@/api/manager/material/record';
import { ElMessage } from 'element-plus';

interface SearchItem {
	recordId: string;
	username: string;
}

interface SignItem {
	lable: string;
	value: string;
}

interface TableItem {
	materialId: string;
	name: string;
	nameZh: string;
	number: number;
}

interface FormState {
	tableData: {
		data: Array<TableItem>;
		loading: boolean;
		isShow: boolean;
		total: number;
		param: {
			type: string;
			arg: string;
		};
		signSelect: Array<SignItem>;
	};
	timelineData: Array<object>;
	searchParam: {
		loading: boolean;
		result: Array<SearchItem>;
	};
}

export default defineComponent({
	name: 'recordEnterBill',
	components: {
		Edit: markRaw(Edit),
		Cloudy: markRaw(Cloudy),
		EditPen: markRaw(EditPen),
		Document: markRaw(Document),
		Upload: markRaw(Upload),
		Coin: markRaw(Coin),
	},
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const state = reactive<FormState>({
			tableData: {
				loading: false,
				total: 0,
				isShow: false,
				data: [],
				param: {
					type: 'bind',
					arg: '',
				},
				signSelect: [
					{ lable: '已登记', value: 'bind' },
					{ lable: '待入库', value: 'wait' },
				],
			},
			searchParam: {
				loading: false,
				result: [],
			},
			timelineData: [
				{ title: '填写申请', description: '填写入库申请', icon: Edit, status: 'process' },
				{ title: '提交申请', description: '提交入库申请，系统初始化入库记录', icon: Upload, status: 'process' },
				{ title: '记录审核', description: '财务主管进行入库申请审核', icon: Cloudy, status: 'process' },
				{ title: '入库登记', description: '登记需要入库的物料', icon: EditPen, status: 'process' },
				{ title: '入库审核', description: '仓库管理员进行入库记录二次审核', icon: Cloudy, status: 'process' },
				{ title: '物料入库', description: '物料正式入库，入库成功后生成该批次物料二维码', icon: Coin, status: 'process' },
				{ title: '入库完成', description: '物料全部入库后，入库记录自动完成，相关信息整理归档', icon: Document, status: 'finish' },
			],
		});

		// 初始化事件
		function initTable() {
			if (state.tableData.param.arg === undefined || !state.tableData.param.arg) {
				ElMessage.error('请选择查询入库记录');
				return;
			}
			state.tableData.loading = true;
			if (state.tableData.param.type === 'wait') {
				EnterRecordAPI()
					.queryEnterMaterialList(`recordId=${state.tableData.param.arg}`)
					.then((res) => {
						setTableData(res.data);
					})
					.catch((err) => {
						state.tableData.loading = false;
					});
				return;
			} else {
				EnterRecordAPI()
					.queryEnterBindList(`recordId=${state.tableData.param.arg}`)
					.then((res) => {
						setTableData(res.data);
					})
					.catch((err) => {
						state.tableData.loading = false;
					});
				return;
			}
		}

		function setTableData(data: any) {
			let list: any = JSON.stringify(data);
			list = JSON.parse(list);
			if (list) {
				const array: Array<TableItem> = [];
				for (let index = 0; index < list.length; index++) {
					const element: any = list[index];
					array.push({
						materialId: element.materialId,
						name: element.name,
						nameZh: element.nameZh,
						number: element.number,
					});
				}
				state.tableData.data = array;
			}
			state.tableData.loading = false;
		}

		// select输入搜索
		function AccountRemote(qury: any) {
			if (qury) {
				state.searchParam.loading = true;
				RecordCommonAPI()
					.queryPage(`arg1=recordId&arg2=${qury}`)
					.then((res) => {
						let list = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<SearchItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (element.status > 0) {
									array.push({ recordId: element.recordId, username: element.createBy });
								}
							}
							state.searchParam.result = array;
						}
						state.searchParam.loading = false;
					})
					.catch((res) => {
						state.searchParam.loading = false;
					});
			} else {
			}
		}

		// select选中事件
		function AccountSelectChange(val: any) {
			if (val) {
				state.tableData.param.arg = val;
			}
		}

		// 搜索提交
		function searchSubmit() {
			state.tableData.loading = true;
			state.tableData.data = [];
			initTable();
		}

		// 表格刷新
		function refreshTable() {
			initTable();
		}

		watch(
			() => state.tableData.param.type,
			() => {
				if (state.tableData.param.arg) {
					setTimeout(() => {
						state.tableData.data = [];
						initTable();
					}, 500);
				}
			},
			{ deep: true, immediate: true }
		);

		watch(
			() => state.tableData.param.arg,
			() => {
				if (state.tableData.param.arg) {
					setTimeout(() => {
						initTable();
					}, 200);
				}
			},
			{ deep: true, immediate: true }
		);
		watch(
			() => state.tableData.data,
			() => {
				if (state.tableData.data.length > 0) {
					state.tableData.isShow = true;
				}
			},
			{ deep: true, immediate: true }
		);
		return {
			AccountRemote,
			AccountSelectChange,
			searchSubmit,
			refreshTable,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.record-approve {
	width: 100%;
	height: 100%;
	position: relative;
}
</style>
