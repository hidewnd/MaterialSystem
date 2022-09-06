<template>
	<div class="warehouse-depot-transfer">
		<el-card shadow="alway" :header="$t('message.router.rackTransfer')">
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<div class="resources-system-search mb15">
						<el-form :model="ruleform.param" label-width="90px">
							<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
								<el-form-item label-width="auto">
									<el-select
										v-model="ruleform.param.depotId"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入仓库名"
										:remote-method="searchDepotRemote"
										:loading="searchDepot.loading"
										@change="searchDepotChange"
										autofocus="false"
										style="max-width: 180px"
									>
										<el-option v-for="item in searchDepot.depotOptions" :key="item.depotId" :label="item.depotName" :value="item.depotId" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-form>
						<el-button size="large" type="primary" class="ml10" @click="onSubmit">
							<el-icon><ele-Search /></el-icon>
							查询
						</el-button>
						<el-button size="large" type="primary" class="ml10" @click="onOpenSetThreshold" :disabled="!isButtonAllow">
							<el-icon><ele-Refresh /></el-icon>
							修改容量阈值
						</el-button>
					</div>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-table :data="ruleform.data" style="width: 100%">
						<el-table-column type="index" label="序号" width="60" />
						<el-table-column prop="rackId" label="货架编号" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="maxCapacity" label="货架最大容量" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="capacity" label="货架当前容量" show-overflow-tooltip></el-table-column>
						<el-table-column prop="status" label="启用状态" show-overflow-tooltip>
							<template #default="scope">
								<el-tag type="success" v-if="scope.row.status === 0">启用</el-tag>
								<el-tag type="info" v-else>禁用</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="操作" width="100">
							<template #default="scope">
								<el-button size="small" type="text" @click="onOpenTransfer(scope.row)"> 转移 </el-button>
							</template>
						</el-table-column>
					</el-table>
					<el-pagination
						@size-change="onHandleSizeChange"
						@current-change="onHandleCurrentChange"
						class="mt15"
						:pager-count="5"
						:page-sizes="[10, 20, 30]"
						v-model:current-page="ruleform.param.page"
						background
						v-model:page-size="ruleform.param.size"
						layout="total, sizes, prev, pager, next, jumper"
						:total="ruleform.total"
					>
					</el-pagination>
				</el-col>
			</el-row>
		</el-card>

		<depot-rack-transfer ref="setTransfer" @refresh="refreshTable" />
		<set-depot-threshold ref="setThreshold" @refresh="refreshTable" />
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { DepotQueryAPI } from '/@/api/manager/material/depot';
import { RackQueryAPI } from '/@/api/manager/material/rack';
import DepotRackTransfer from './component/DepotRackTransfer.vue';
import SetDepotThreshold from './component/SetDepotThreshold.vue';
import { ElMessage } from 'element-plus';

interface DepotItem {
	depotId: string;
	depotName: string;
}

interface TableItem {
	depotId: string;
	rackId: string;
	maxCapacity: number;
	capacity: number;
	status: number;
}

interface DepotTransferState {
	isButtonAllow: boolean;
	ruleform: {
		loading: boolean;
		data: Array<TableItem>;
		total: number;
		param: {
			size: number;
			page: number;
			depotId: string;
		};
	};
	searchDepot: {
		depotOptions: Array<DepotItem>;
		loading: boolean;
	};
}

export default defineComponent({
	name: 'WarehouseDepotTransfer',
	components: { DepotRackTransfer, SetDepotThreshold },
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const setTransfer = ref();
		const setThreshold = ref();
		const state = reactive<DepotTransferState>({
			isButtonAllow: false,
			ruleform: {
				loading: false,
				data: [],
				total: 0,
				param: {
					size: 10,
					page: 1,
					depotId: '',
				},
			},
			searchDepot: {
				loading: false,
				depotOptions: [],
			},
		});

		// 表格初始化
		function initTable() {
			state.ruleform.loading = true;
			if (!state.ruleform.param.depotId) {
				ElMessage.warning('请提前选择操作仓库！');
			} else {
				RackQueryAPI()
					.queryPage(`size=${state.ruleform.param.size}&page=${state.ruleform.param.page}&arg1=depotId&arg2=${state.ruleform.param.depotId}`)
					.then((res) => {
						if (res.data) {
							setTableData(res.data);
						}
					});
			}
		}

		// 提交查询
		function onSubmit() {
			initTable();
		}

		function onRefresh() {
			setTimeout(() => {
				state.ruleform.param = {
					size: 10,
					page: 1,
					depotId: state.ruleform.param.depotId,
				};
				initTable();
			}, 1500);
		}

		function refreshTable() {
			initTable();
		}

		// 设置表格数据
		function setTableData(data: any) {
			let elements = JSON.stringify(data.element);
			elements = JSON.parse(elements);
			if (elements) {
				const array: Array<TableItem> = [];
				for (let index = 0; index < elements.length; index++) {
					const element: any = elements[index];
					array.push({
						depotId: element.depotId,
						rackId: element.rackId,
						maxCapacity: element.maxCapacity,
						capacity: element.capacity,
						status: element.status,
					});
				}
				state.ruleform.data = array;
				state.ruleform.total = data.totoal;
				state.ruleform.loading = false;
			}
		}

		function searchDepotRemote(query: string) {
			if (query) {
				state.searchDepot.loading = true;
				DepotQueryAPI()
					.queryList(`arg1=depotName&arg2=${query}`)
					.then((res) => {
						let list = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<DepotItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								array.push({ depotId: element.depotId, depotName: element.depotName });
							}
							state.searchDepot.depotOptions = array;
						}
						state.searchDepot.loading = false;
					})
					.catch((res) => {
						state.searchDepot.loading = false;
					});
			}
		}

		function searchDepotChange(depotId: string) {}

		function initSearchOptions() {
			state.searchDepot.loading = true;
			DepotQueryAPI()
				.queryList(``)
				.then((res) => {
					let list = JSON.stringify(res.data.element);
					list = JSON.parse(list);
					if (list) {
						const array: Array<DepotItem> = [];
						for (let i = 0; i < list.length; i++) {
							const element: any = list[i];
							array.push({ depotId: element.depotId, depotName: element.depotName });
						}
						state.searchDepot.depotOptions = array;
					}
					state.searchDepot.loading = false;
				})
				.catch((res) => {
					state.searchDepot.loading = false;
				});
		}

		// 分页改变
		function onHandleCurrentChange(val: number) {
			state.ruleform.param.page = val;
			initTable();
		}

		// 分页大小改变
		function onHandleSizeChange(val: number) {
			state.ruleform.param.size = val;
			initTable();
		}

		function onOpenTransfer(row: TableItem) {
			setTransfer.value.openDialog(row);
		}
		function onOpenSetThreshold() {
			setThreshold.value.openDialog(state.ruleform.param.depotId);
		}

		watch(
			() => state.ruleform.param.depotId,
			() => {
				if (state.ruleform.param.depotId) {
					initTable();
					state.isButtonAllow = true;
				} else {
					state.isButtonAllow = false;
					state.ruleform.data = [];
				}
			},
			{ deep: true, immediate: true }
		);

		// 页面加载时
		onMounted(() => {
			initSearchOptions();
		});

		return {
			setTransfer,
			setThreshold,
			onSubmit,
			onRefresh,
			refreshTable,
			onOpenTransfer,
			onOpenSetThreshold,
			searchDepotRemote,
			searchDepotChange,
			onHandleSizeChange,
			onHandleCurrentChange,
			...toRefs(state),
		};
	},
});
</script>

<style></style>
