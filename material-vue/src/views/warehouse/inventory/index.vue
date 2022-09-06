<template>
	<div class="warehouse-inventory">
		<el-card shadow="alway" :header="$t('message.router.inventory')">
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
							<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20 ml10">
								<el-form-item label-width="auto">
									<el-select
										v-model="ruleform.param.materialId"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入物料系统名"
										:remote-method="searchMateriakRemote"
										:loading="searchMaterial.loading"
										@change="searchMaterialChange"
										autofocus="false"
										style="max-width: 180px"
									>
										<el-option
											v-for="item in searchMaterial.materialOptions"
											:key="item.materialId"
											:label="item.materialNameZh"
											:value="item.materialId"
										/>
									</el-select>
								</el-form-item>
							</el-col>
						</el-form>

						<el-button size="large" type="primary" class="ml10" @click="onSubmit">
							<el-icon><ele-Search /></el-icon>
							查询
						</el-button>

						<el-button size="large" type="primary" class="ml10" @click="onRefresh">
							<el-icon><ele-Refresh /></el-icon>
							刷新
						</el-button>
					</div>
				</el-col>
			</el-row>

			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-table :data="ruleform.data" style="width: 100%">
						<el-table-column type="index" label="序号" width="60" />
						<el-table-column prop="depotName" label="仓库名" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="rackId" label="货架编号" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="materialName" label="物料系统名" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="materialNameZh" label="物料中文名" show-overflow-tooltip></el-table-column>
						<el-table-column prop="number" label="库存数量" show-overflow-tooltip sortable></el-table-column>
						<el-table-column prop="location" label="库存地址" show-overflow-tooltip></el-table-column>
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
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { DepotQueryAPI } from '/@/api/manager/material/depot';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import { RackQueryAPI } from '/@/api/manager/material/rack';

interface DepotItem {
	depotId: string;
	depotName: string;
}

interface RackItem {
	rackId: string;
}

interface MaterialItem {
	materialId: string;
	materialName: string;
	materialNameZh: string;
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
}

interface InventoryState {
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
	searchMaterial: {
		materialOptions: Array<MaterialItem>;
		loading: boolean;
	};
}

export default defineComponent({
	name: 'WarehouseInventory',
	components: {},
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const state = reactive<InventoryState>({
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
			searchMaterial: {
				loading: false,
				materialOptions: [],
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
			state.ruleform.loading = true;
			MaterialQueryAPI()
				.queryInfoListByArg(state.ruleform.param)
				.then((res) => {
					setTableData(res.data);
				});
		}

		function onRefresh() {
			state.ruleform.param = {
				size: 10,
				page: 1,
				depotId: '',
				rackId: '',
				materialId: '',
			};
			initTable();
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
								array.push({ rackId: element.rackId });
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

		function searchMateriakRemote(query: string) {
			if (query) {
				state.searchMaterial.loading = true;
				MaterialQueryAPI()
					.queryPage(`type=material&arg1=materialName&arg2=${query}`)
					.then((res) => {
						let list = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<MaterialItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								array.push({ materialId: element.materialId, materialName: element.materialName, materialNameZh: element.materialNameZh });
							}
							state.searchMaterial.materialOptions = array;
						}
						state.searchMaterial.loading = false;
					})
					.catch((res) => {
						state.searchMaterial.loading = false;
					});
			}
		}

		function searchMaterialChange(materialId: string) {}

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
					});
				}
				state.ruleform.data = array;
				state.ruleform.total = data.total;
				state.ruleform.loading = false;
			}
		}

		// 初始化
		onMounted(() => {
			initTable();
		});
		return {
			onSubmit,
			onRefresh,
			searchDepotRemote,
			searchDepotChange,
			searchRackRemote,
			searchRacktChange,
			searchMateriakRemote,
			searchMaterialChange,
			onHandleSizeChange,
			onHandleCurrentChange,
			...toRefs(state),
		};
	},
});
</script>

<style></style>
