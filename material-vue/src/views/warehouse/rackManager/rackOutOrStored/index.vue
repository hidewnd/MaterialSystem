<template>
	<div class="warehouse-rack-out-or-stored">
		<el-card shadow="alway" :header="$t('message.router.rackOutOrStored')">
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
							<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20 ml10">
								<el-form-item label-width="auto">
									<el-select
										v-model="ruleform.param.rackId"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入货架编号"
										:remote-method="searchRackRemote"
										:loading="searchRack.loading"
										@change="searchRacktChange"
										autofocus="false"
										style="max-width: 180px"
									>
										<el-option v-for="item in searchRack.rackOptions" :key="item.rackId" :label="item.depotName" :value="item.rackId" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-form>

						<el-button size="large" type="primary" class="ml10" @click="onSubmit">
							<el-icon><ele-Search /></el-icon>
							查询
						</el-button>
						<el-button size="large" type="primary" class="ml10" @click="onEnter" :disabled="!isShow">
							<el-icon><ele-Refresh /></el-icon>
							入库
						</el-button>
						<el-button size="large" type="primary" class="ml10" @click="onRefresh" :disabled="!allowRefresh || !isShow">
							<el-icon><ele-Refresh /></el-icon>
							刷新
						</el-button>
					</div>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-empty v-if="!isShow" description="请选择操作货架" />
					<template v-else>
						<el-table :data="ruleform.data" style="width: 100%" border>
							<el-table-column type="index" label="序号" width="60" />
							<el-table-column prop="depotName" label="仓库名" show-overflow-tooltip sortable></el-table-column>
							<el-table-column prop="materialId" label="物料编号" show-overflow-tooltip sortable></el-table-column>
							<el-table-column prop="materialName" label="物料系统名" show-overflow-tooltip sortable></el-table-column>
							<el-table-column prop="materialNameZh" label="物料中文名" show-overflow-tooltip></el-table-column>
							<el-table-column prop="number" label="库存数量" show-overflow-tooltip sortable></el-table-column>
							<el-table-column label="操作" width="100">
								<template #default="scope">
									<el-button size="small" type="text" @click="onOpenOperate(scope.row, true)"> 出库 </el-button>
									<el-button size="small" type="text" @click="onOpenOperate(scope.row, false)"> 入库 </el-button>
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
					</template>
				</el-col>
			</el-row>
		</el-card>

		<rack-material-operate ref="operateRef" @refres="onRefresh"></rack-material-operate>
		<rack-material-enter-operate ref="enterRef" @refres="onRefresh"></rack-material-enter-operate>
	</div>
</template>

<script lang="ts">
import { ElMessage } from 'element-plus';
import { defineComponent, reactive, toRefs, getCurrentInstance, watch, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { DepotQueryAPI } from '/@/api/manager/material/depot';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import { RackQueryAPI } from '/@/api/manager/material/rack';
import RackMaterialOperate from './component/RackMaterialOperate.vue';
import RackMaterialEnterOperate from './component/RackMaterialEnterOperate.vue';
import { log } from 'console';

interface DepotItem {
	depotId: string;
	depotName: string;
}

interface RackItem {
	rackId: string;
}

interface MaterialInfoItem {
	id: string;
	depotId: string;
	depotName: string;
	rackId: string;
	materialId: string;
	materialNameZh: string;
	materialName: string;
	number: number;
	location: number;
	qrcode: string;
}

interface RackStoredSate {
	isShow: boolean;
	allowRefresh: boolean;
	ruleform: {
		loading: boolean;
		data: Array<MaterialInfoItem>;
		total: number;
		param: {
			size: number;
			page: number;
			depotId: string;
			rackId: string;
			materialId: string;
		};
	};
	searchDepot: {
		depotOptions: Array<DepotItem>;
		loading: boolean;
	};
	searchRack: {
		rackOptions: Array<RackItem>;
		loading: boolean;
	};
}

export default defineComponent({
	name: 'WarehouseRackOutOrStored',
	components: { RackMaterialOperate, RackMaterialEnterOperate },
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const operateRef = ref();
		const enterRef = ref();
		const state = reactive<RackStoredSate>({
			isShow: false,
			allowRefresh: true,
			ruleform: {
				data: [],
				loading: false,
				total: 0,
				param: {
					size: 10,
					page: 1,
					depotId: '',
					rackId: '',
					materialId: '',
				},
			},
			searchDepot: {
				loading: false,
				depotOptions: [],
			},
			searchRack: {
				loading: false,
				rackOptions: [],
			},
		});

		// 表格初始化
		function initTable() {
			state.ruleform.loading = true;
			MaterialQueryAPI()
				.queryInfoListByArg(state.ruleform.param)
				.then((res) => {
					setTableData(res.data);
				});
		}

		// 提交查询
		function onSubmit() {
			if (!state.ruleform.param.depotId || !state.ruleform.param.rackId) {
				ElMessage.warning('请选择具体操作货架');
				return;
			}
			state.ruleform.loading = true;
			MaterialQueryAPI()
				.queryInfoListByArg(state.ruleform.param)
				.then((res) => {
					setTableData(res.data);
				});
		}

		function onRefresh() {
			state.ruleform.param.size = 10;
			state.ruleform.param.page = 1;
			initTable();
			state.allowRefresh = false;
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

		function searchRackRemote(query: string) {
			if (!state.ruleform.param.depotId) {
				ElMessage.error('请选择仓库');
				return;
			}
			if (query) {
				state.searchRack.loading = true;
				RackQueryAPI()
					.queryPage(`arg1=rackId&arg2=${query}`)
					.then((res) => {
						let list = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<RackItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (state.ruleform.param.depotId == element.depotId) {
									array.push({ rackId: element.rackId });
								}
							}
							state.searchRack.rackOptions = array;
						}
						state.searchRack.loading = false;
					})
					.catch((res) => {
						state.searchRack.loading = false;
					});
			} else {
			}
		}

		function searchRacktChange(rackId: string) {}

		// 设置表格数据
		function setTableData(data: any) {
			let elements = JSON.stringify(data.element);
			elements = JSON.parse(elements);
			if (elements) {
				const array: Array<MaterialInfoItem> = [];
				for (let index = 0; index < elements.length; index++) {
					const element: any = elements[index];
					array.push({
						id: element.id,
						depotId: element.depotId,
						depotName: element.depotName,
						rackId: element.rackId,
						materialId: element.materialId,
						materialNameZh: element.materialNameZh,
						materialName: element.materialName,
						number: element.number,
						location: element.location,
						qrcode: element.qrCode,
					});
				}
				state.ruleform.data = array;
				state.ruleform.total = data.total;
				state.ruleform.loading = false;
				state.isShow = true;
			}
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

		function onOpenOperate(row: MaterialInfoItem, isOuter: boolean) {
			operateRef.value.openDialog(row, isOuter);
		}

		function onEnter() {
			if (state.ruleform.param.depotId && state.ruleform.param.rackId) {
				let depotName = '';
				for (let index = 0; index < state.searchDepot.depotOptions.length; index++) {
					const element = state.searchDepot.depotOptions[index];
					if (element) {
						depotName = element.depotName;
					}
				}
				enterRef.value.openDialog({ depotId: state.ruleform.param.depotId, depotName: depotName, rackId: state.ruleform.param.rackId });
			}
		}

		watch(
			() => state.allowRefresh,
			() => {
				if (!state.allowRefresh) {
					setTimeout(() => {
						state.allowRefresh = true;
					}, 5000);
				}
			},
			{ deep: true, immediate: true }
		);

		// 初始化
		onMounted(() => {});
		return {
			enterRef,
			operateRef,
			searchDepotRemote,
			searchDepotChange,
			searchRackRemote,
			searchRacktChange,
			onSubmit,
			onRefresh,
			onOpenOperate,
			onHandleCurrentChange,
			onHandleSizeChange,
			onEnter,
			...toRefs(state),
		};
	},
});
</script>

<style></style>
