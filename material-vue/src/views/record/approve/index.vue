<template>
	<div class="record-approve">
		<el-card shadow="alway" :header="$t('message.router.recordApprove')">
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<div class="resources-system-search mb15" style="align: center">
						<el-form :model="searchParam" label-width="90px">
							<el-col :xs="2" :sm="2" :md="2" :lg="2" :xl="2" class="mb30" style="margin-right: 10px !important">
								<el-select v-model="tableData.param.type" class="m-2" placeholder="Select" size="large">
									<el-option v-for="item in tableData.signSelect" :key="item.value" :label="item.lable" :value="item.value" />
								</el-select>
							</el-col>
							<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
								<el-form-item label-width="auto">
									<el-select
										v-model="searchParam.param.arg"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入用户名查询"
										:remote-method="AccountRemote"
										:loading="searchParam.loading"
										@change="AccountSelectChange"
										autofocus="false"
										style="max-width: 180px"
									>
										<el-option v-for="item in searchParam.result" :key="item.accountId" :label="item.username" :value="item.username" />
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
						<el-button size="large" type="primary" class="ml10" @click="exportData">
							<el-icon> <ele-Download /> </el-icon>
							导出
						</el-button>
					</div>
				</el-col>
			</el-row>

			<el-table :data="tableData.data" style="width: 100%">
				<el-table-column type="index" label="序号" width="60" />
				<el-table-column prop="recordId" label="出入库记录编号" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="recordType" label="出入库记录类型" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="createBy" label="出入库申请人" show-overflow-tooltip sortable></el-table-column>
				<el-table-column prop="number" label="记录物料总数" show-overflow-tooltip></el-table-column>
				<el-table-column prop="value" label="记录物料总价值" show-overflow-tooltip></el-table-column>
				<el-table-column prop="executeResult" label="申请审批结果" show-overflow-tooltip></el-table-column>
				<el-table-column prop="applyResult" label="执行审批结果" show-overflow-tooltip></el-table-column>
				<el-table-column prop="recordStatus" label="记录当前状态" show-overflow-tooltip></el-table-column>
				<el-table-column label="操作">
					<template #default="scope">
						<el-button
							:disabled="!tableData.allowExecute || scope.row.status < 0 || scope.row.status > 1"
							size="small"
							type="text"
							@click="executeApproval(scope.row)"
						>
							申请审批
						</el-button>
						<el-button :disabled="!tableData.allowApply || scope.row.status != 2" size="small" type="text" @click="applyApproval(scope.row)">
							执行审批
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				:total="tableData.total"
				v-model:page-size="tableData.param.size"
				@size-change="onHandleSizeChange"
				v-model:current-page="tableData.param.page"
				@current-change="onHandleCurrentChange"
				layout="total, sizes, prev, pager, next, jumper"
				class="mt15"
				:pager-count="5"
				:page-sizes="[10, 20, 30]"
				background
			>
			</el-pagination>
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

		<approve-operate ref="operatorRef" @refresh="refreshTable"></approve-operate>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw, onMounted, watch, ref } from 'vue';
import { Edit, Cloudy, EditPen, Document, Upload, Refresh } from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';
import { ResourceSystemApi } from '/@/api/resources/system';
import { RecordCommonAPI } from '/@/api/manager/material/record';
import { useStore } from '/@/store/index';
import ApproveOperate from './component/RecordApproveOperate.vue';
import { CommonApi } from '/@/api/common';

interface SeatchItem {
	accountId: string;
	username: string;
}

interface SignItem {
	lable: string;
	value: string;
}

interface TableItem {
	recordId: string;
	recordType: string;
	createBy: string;
	number: number;
	value: number;
	executeResult: string;
	applyResult: string;
	recordStatus: string;
	status: number;
}

interface FormState {
	tableData: {
		data: Array<TableItem>;
		loading: boolean;
		total: number;
		param: {
			page: number;
			size: number;
			type: string;
			arg: string;
		};
		allowExecute: boolean;
		allowApply: boolean;
		signSelect: Array<SignItem>;
	};
	timelineData: Array<object>;
	searchParam: {
		loading: boolean;
		materialName: string;
		param: {
			name: string;
			type: string;
			column: string;
			arg: string;
		};
		result: Array<SeatchItem>;
	};
}

export default defineComponent({
	name: 'recordApprove',
	components: {
		Edit: markRaw(Edit),
		Cloudy: markRaw(Cloudy),
		EditPen: markRaw(EditPen),
		Document: markRaw(Document),
		Upload: markRaw(Upload),
		Refresh: markRaw(Refresh),
		ApproveOperate,
	},
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const store = useStore();
		const operatorRef = ref();
		//全局state
		const state = reactive<FormState>({
			tableData: {
				loading: false,
				total: 0,
				data: [],
				param: {
					type: 'enter',
					page: 1,
					size: 10,
					arg: '',
				},
				allowExecute: false,
				allowApply: false,
				signSelect: [
					{ lable: '入库记录', value: 'enter' },
					{ lable: '出库记录', value: 'outer' },
				],
			},
			searchParam: {
				loading: false,
				materialName: '',
				param: {
					name: 'account',
					type: 'like',
					column: 'username',
					arg: '',
				},
				result: [],
			},
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
		});

		function initAllow() {
			const roles = <any>store.state.userInfos.userInfos.roles; // 角色权限
			if (roles) {
				for (let index = 0; index < roles.length; index++) {
					const element = roles[index];
					if (element === 'root') {
						state.tableData.allowExecute = true;
						state.tableData.allowApply = true;
						return;
					}
					if (element === 'financial' && !state.tableData.allowApply) {
						state.tableData.allowApply = true;
					}
					if (element === 'warehouse' && !state.tableData.allowExecute) {
						state.tableData.allowExecute = true;
					}
				}
			}
		}

		// 初始化事件
		function initTable() {
			state.searchParam.param.arg = '';
			state.tableData.loading = true;
			state.tableData.data = [];
			RecordCommonAPI()
				.queryPage(`type=${state.tableData.param.type}&size=${state.tableData.param.size}&page=${state.tableData.param.page}`)
				.then((res) => {
					let list: any = JSON.stringify(res.data.element);
					list = JSON.parse(list);
					if (list) {
						const array: Array<TableItem> = [];
						for (let i = 0; i < list.length; i++) {
							const element: any = list[i];
							array.push({
								recordId: element.recordId,
								recordType: getRecordType(element.sign),
								createBy: element.createBy,
								number: element.number,
								value: element.value,
								executeResult: getResult(element.executeApproval),
								applyResult: getResult(element.applyApproval),
								recordStatus: element.recordStatus,
								status: element.status,
							});
						}
						state.tableData.data = array;
						state.tableData.total = res.data.total;
					}
					state.tableData.loading = false;
				})
				.then((res) => {
					state.tableData.loading = false;
				});
		}

		function getResult(val: any): string {
			if (val === 1) {
				return '审核通过';
			}
			if (val === 2) {
				return '审核失败';
			}
			return '尚未审批';
		}

		function getRecordType(val: any): string {
			if (val == 0 || val === '0' || val === 'enter') {
				return '入库';
			} else if (val == 1 || val === '1' || val === 'outer') {
				return '出库';
			} else {
				return '未知类型';
			}
		}

		// select输入搜索
		function AccountRemote(qury: any) {
			if (qury) {
				state.searchParam.loading = true;
				ResourceSystemApi()
					.queryList(`account/like/username/${qury}`)
					.then((res) => {
						let list = JSON.stringify(res.data);
						list = JSON.parse(list);
						if (list) {
							const array: Array<SeatchItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (element) {
									array.push({ accountId: element.accountId, username: element.username });
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
				setTimeout(() => {
					searchSubmit();
				}, 700);
			}
		}

		// 搜索提交
		function searchSubmit() {
			state.tableData.loading = true;
			state.tableData.data = [];
			RecordCommonAPI()
				.queryPage(
					`type=${state.tableData.param.type}&size=${state.tableData.param.size}&page=${state.tableData.param.page}&arg1=createBy&arg2=${state.tableData.param.arg}`
				)
				.then((res) => {
					let list: any = JSON.stringify(res.data.element);
					list = JSON.parse(list);
					if (list) {
						const array: Array<TableItem> = [];
						for (let i = 0; i < list.length; i++) {
							const element: any = list[i];
							array.push({
								recordId: element.recordId,
								recordType: getRecordType(element.sign),
								createBy: element.createBy,
								number: element.number,
								value: element.value,
								executeResult: getResult(element.executeApproval),
								applyResult: getResult(element.applyApproval),
								recordStatus: element.recordStatus,
								status: element.status,
							});
						}
						state.tableData.data = array;
						state.tableData.total = res.data.total;
					}
					state.tableData.loading = false;
				})
				.then((res) => {
					state.tableData.loading = false;
				});
		}

		// 表格刷新
		function refreshTable() {
			initTable();
		}

		// 申请审批操作
		function executeApproval(row: TableItem) {
			operatorRef.value.openDialog({ data: row, operateType: 0 });
		}

		// 执行审批操作
		function applyApproval(row: TableItem) {
			operatorRef.value.openDialog({ data: row, operateType: 1 });
		}

		// 分页改变
		function onHandleCurrentChange(val: number) {
			state.tableData.param.page = val;
			state.tableData.loading = true;
			initTable();
		}
		// 分页大小改变
		function onHandleSizeChange(val: number) {
			state.tableData.param.size = val;
			state.tableData.loading = true;
			initTable();
		}

			function exportData() {
			CommonApi()
				.downloadRecord()
				.then((res: any) => {
					// debugger;
					let blob = new Blob([res], { type: 'application/xlsx' });
					let url = window.URL.createObjectURL(blob);
					const link = document.createElement('a'); //创建a标签
					link.href = url;
					link.download = '出入库记录数据.xlsx'; //重命名文件
					link.click();
					URL.revokeObjectURL(url);
				});
		}

		watch(
			() => state.tableData.param.type,
			() => {
				setTimeout(() => {
					initTable();
				}, 700);
			},
			{ deep: true, immediate: true }
		);

		// 页面初始化
		onMounted(() => {
			initTable();
			initAllow();
		});

		return {
			operatorRef,
			AccountRemote,
			AccountSelectChange,
			searchSubmit,
			refreshTable,
			executeApproval,
			applyApproval,
			onHandleSizeChange,
			onHandleCurrentChange,
			exportData,
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
