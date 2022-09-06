<template>
	<div class="record-outer-apply">
		<el-card :header="$t('message.router.outerApply')" shadow="always">
			<el-row :gutter="12">
				<el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="mb20">
					<el-form ref="formRef" :model="ruleForm" class="wrap-form" label-position="left" size="default" :inline-message="true" label-width="120px">
						<el-row :gutter="12" v-for="(v, k) in ruleForm.outerMaterial" :key="k">
							<el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
								<el-form-item
									label="物料名"
									:prop="`outerMaterial[${k}].materialId`"
									label-width="120"
									:rules="[{ required: true, message: `物料名不能为空`, trigger: 'blur' }]"
								>
									<template #label>
										<el-button type="primary" circle size="small" @click="onAddRow" v-if="k === 0">
											<el-icon>
												<ele-Plus />
											</el-icon>
										</el-button>
										<el-button type="danger" circle size="small" @click="onDelRow(k)" v-else>
											<el-icon>
												<ele-Delete />
											</el-icon>
										</el-button>
										<span class="ml10">物料名</span>
									</template>
									<el-select
										v-model="ruleForm.outerMaterial[k].materialId"
										filterable
										remote
										reserve-keyword
										clearable
										placeholder="请输入物料系统名"
										:remote-method="materialRemote2"
										:loading="searchParam.loading"
										@change="materialSelectChange2"
										autofocus="false"
									>
										<el-option v-for="item in materialoptions2" :key="item.value" :label="item.materialName" :value="item.value" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :xs="12" :sm="12" :md="8" :lg="8" :xl="6" class="mb20">
								<el-form-item
									label="数量"
									label-width="120"
									:prop="`outerMaterial[${k}].number`"
									:rules="[{ required: true, message: `物料数量不能为空`, trigger: 'blur' }]"
								>
									<el-input-number
										v-model="ruleForm.outerMaterial[k].number"
										:min="1"
										:step="1"
										controls-position="right"
										placeholder="请输入出库物料数量"
										autofocus="false"
									></el-input-number>
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item>
							<el-button type="primary" @click="submitForm">提交申请</el-button>
							<el-button @click="resetForm">重置申请</el-button>
						</el-form-item>
					</el-form>
				</el-col>
				<el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="mb20">
					<el-card shadow="alway" header="库存查询">
						<el-row>
							<el-form ref="searchFormRef" :model="searchParam" label-position="left" size="default" :inline-message="true" label-width="90px">
								<el-col :xs="24" :sm="25" :md="25" :lg="25" :xl="25" class="mb20">
									<el-form-item label="物料名" prop="param" :rules="[{ required: true, message: `物料名不能为空`, trigger: 'blur' }]">
										<el-select
											v-model="searchParam.materialName"
											filterable
											remote
											reserve-keyword
											clearable
											placeholder="请输入物料系统名"
											:remote-method="materialRemote"
											:loading="searchParam2.loading"
											@change="materialSelectChange"
											autofocus="false"
										>
											<el-option v-for="item in materialoptions" :key="item.value" :label="item.materialName" :value="item.value" />
										</el-select>
									</el-form-item>
								</el-col>
							</el-form>
							<el-button :icon="Search" size="default" type="success" class="ml10" @click="searchCapacity">查询</el-button>
						</el-row>
						<el-row>
							<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
								<el-empty description="暂无数据" :image-size="80" v-if="searchParam.result.length == 0" />
								<el-descriptions title="库存列表" :column="2" border v-else>
									<template v-for="item in searchParam.result" :key="item.materialId">
										<el-descriptions-item label="物料系统名" label-align="center">{{ item.materialName }}</el-descriptions-item>
										<el-descriptions-item label="库存数量" label-align="center">{{ item.number }}</el-descriptions-item>
									</template>
								</el-descriptions>
							</el-col>
						</el-row>
					</el-card>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24" :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-steps :space="400" :active="0" align-center finish-status="finish">
						<el-step v-for="(item, index) in timelineData" :key="index" :title="item.title" :description="item.description" :icon="item.icon">
						</el-step>
					</el-steps>
				</el-col>
			</el-row>
		</el-card>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw, onMounted } from 'vue';
import { Edit, Cloudy, EditPen, Document, Upload, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { OuterRecordAPI } from '/@/api/manager/material/record';
import { useI18n } from 'vue-i18n';
import { MaterialQueryAPI } from '/@/api/manager/material/material';

interface MaterialItem {
	materialName: string;
	materialNameZh: string;
	value: string;
}

interface FormState {
	ruleForm: any;
	timelineData: Array<object>;
	materialoptions: Array<MaterialItem>;
	materialoptions2: Array<MaterialItem>;
	searchParam: {
		loading: boolean;
		materialName: string;
		param: string;
		result: Array<object>;
	};
	searchParam2: {
		loading: boolean;
		materialName: string;
		param: string;
		result: Array<object>;
	};
	materialNameList: Array<any>;
}

export default defineComponent({
	name: 'RecordOuterApply',
	components: {
		Edit: markRaw(Edit),
		Cloudy: markRaw(Cloudy),
		EditPen: markRaw(EditPen),
		Document: markRaw(Document),
		Upload: markRaw(Upload),
		Refresh: markRaw(Refresh),
	},
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const state = reactive<FormState>({
			ruleForm: {
				outerMaterial: [
					{
						materialId: '',
						materialName: '',
						number: 0,
					},
				],
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
			searchParam: {
				loading: false,
				materialName: '',
				param: '',
				result: [],
			},
			searchParam2: {
				loading: false,
				materialName: '',
				param: '',
				result: [],
			},
			materialoptions: [],
			materialoptions2: [],
			materialNameList: [],
		});

		// 提交申请
		function submitForm() {
			proxy.$refs.formRef.validate((valid: boolean) => {
				if (valid) {
					let total = 0;
					for (let i = 0; i < state.ruleForm.outerMaterial.length; i++) {
						total += state.ruleForm.outerMaterial[i].number;
					}
					ElMessageBox.confirm('是否确定提交该出库申请？\n' + `物料总数量：${total}。`, '提示', {
						confirmButtonText: t('message.resources.common.confirmButtonText'),
						cancelButtonText: t('message.resources.common.cancelButtonText'),
						type: 'warning',
					})
						.then(() => {
							setTimeout(() => {
								let map: any = {};
								if (state.ruleForm.outerMaterial) {
									for (let i = 0; i < state.ruleForm.outerMaterial.length; i++) {
										const element = state.ruleForm.outerMaterial[i];
										map[element.materialId] = element.number;
									}
									OuterRecordAPI()
										.generalOuter({ outerMaterial: map })
										.then((res) => {
											ElMessageBox.alert(`生成出库记录成功! 出库记录编号：${res.data}`, '提示', {
												type: 'success',
												center: true,
												confirmButtonText: '确认',
											});
										});
								}
							}, 300);
						})
						.catch(() => {});
				}
			});
		}

		// 重置
		function resetForm() {
			proxy.$refs.formRef.resetFields();
		}

		// 新增行
		const onAddRow = () => {
			state.ruleForm.outerMaterial.push({
				materialId: '',
				number: 0,
			});
		};

		// 删除行
		const onDelRow = (k: number) => {
			state.ruleForm.outerMaterial.splice(k, 1);
		};

		// 查询按钮
		function searchCapacity() {
			state.searchParam.loading = true;
			if (state.searchParam.param) {
				MaterialQueryAPI()
					.queryCapacity(`materialId=${state.searchParam.param}`)
					.then((res) => {
						let data: any = JSON.stringify(res.data);
						data = JSON.parse(data);
						if (data != null) {
							MaterialQueryAPI()
								.queryOne(`id=${state.searchParam.param}&type=material`)
								.then((res) => {
									let material: any = JSON.stringify(res.data);
									material = JSON.parse(material);
									const array: any = [];
									if (material) {
										array.push({
											materialId: material.materaialId,
											materialName: material.materialName,
											materialNameZh: material.materialNameZh,
											number: data,
										});
									} else {
										array.push({
											materialId: material.materaialId,
											materialName: '',
											materialNameZh: '',
											number: data,
										});
									}
									state.searchParam.result = array;
								});
						}
						state.searchParam.loading = false;
					})
					.catch(() => {
						state.searchParam.loading = false;
					});
			} else {
				state.searchParam.loading = true;
				MaterialQueryAPI()
					.queryAllCapacity()
					.then((res) => {
						setStockList(res.data);
						state.searchParam.loading = false;
					})
					.catch(() => {
						state.searchParam.loading = false;
					});
			}
		}

		const materialNameList: Array<object> = [];

		//select选中事件
		function materialSelectChange(materialId: any) {
			state.searchParam.param = materialId;
			for (let i = 0; i < materialNameList.length; i++) {
				const element: any = materialNameList[i];
				if (element.materialId === materialId) {
					state.searchParam.materialName = element.materialName;
					return;
				}
			}
		}

		// select输入查询
		function materialRemote(query: string) {
			if (query) {
				state.searchParam.loading = true;
				state.searchParam.materialName = query;
				MaterialQueryAPI()
					.queryList(`type=material&way=like&column=material_name&arg1=${state.searchParam.materialName}`)
					.then((res) => {
						if (res.data) {
							let list: any = JSON.stringify(res.data);
							list = JSON.parse(list);
							const array: Array<MaterialItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								array.push({
									materialName: element.materialName,
									materialNameZh: element.materialNameZh,
									value: element.materialId,
								});
								materialNameList.push({ materialId: element.materialId, materialName: element.materialName });
							}
							state.materialoptions = array;
							state.searchParam.loading = false;
						}
					});
			}
		}

		//select选中事件
		function materialSelectChange2(materialId: any) {
			state.searchParam2.param = materialId;
			for (let i = 0; i < materialNameList.length; i++) {
				const element: any = materialNameList[i];
				if (element.materialId === materialId) {
					state.searchParam2.materialName = element.materialName;
					return;
				}
			}
		}

		// select输入查询
		function materialRemote2(query: string) {
			if (query) {
				state.searchParam2.loading = true;
				state.searchParam2.materialName = query;
				MaterialQueryAPI()
					.queryList(`type=material&way=like&column=material_name&arg1=${query}`)
					.then((res) => {
						if (res.data) {
							let list: any = JSON.stringify(res.data);
							list = JSON.parse(list);
							const array: Array<MaterialItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								array.push({
									materialName: element.materialName,
									materialNameZh: element.materialNameZh,
									value: element.materialId,
								});
								materialNameList.push({ materialId: element.materialId, materialName: element.materialName });
							}
							state.materialoptions2 = array;
							state.searchParam2.loading = false;
						}
					});
			}
		}

		function init() {
			MaterialQueryAPI()
				.queryAllCapacity()
				.then((res) => {
					setStockList(res.data);
				});
		}

		function setStockList(data: any) {
			let list: any = JSON.stringify(data);
			list = JSON.parse(list);
			if (list) {
				state.searchParam.result = [];
				for (let key in list) {
					MaterialQueryAPI()
						.queryList(`way=all&type=material`)
						.then((res) => {
							let array: any = JSON.stringify(res.data);
							array = JSON.parse(array);
							if (array) {
								let material = array.find((item: any) => item.materialId === key);
								if (material != undefined) {
									state.searchParam.result.push({
										materialId: key,
										materialName: material.materialName,
										materialNameZh: material.materialNameZh,
										number: list[key],
									});
								} else {
									state.searchParam.result.push({
										materialId: key,
										materialName: key,
										materialNameZh: key,
										number: list[key],
									});
								}
							}
						});
				}
			}
		}

		onMounted(() => {
			init();
		});

		return {
			submitForm,
			resetForm,
			onAddRow,
			onDelRow,
			Search,
			searchCapacity,
			materialRemote,
			materialSelectChange,
			materialRemote2,
			materialSelectChange2,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.record-outer-apply {
	width: 100%;
	height: 100%;
	position: relative;

	.wrap-form {
		::v-deep(.el-form-item--small.el-form-item) {
			margin-bottom: 0 !important;
		}
		.enter-form-msg {
			color: #a79d9dce;
			font-size: 12px;
			width: 100%;
			.enter-form-msg-red {
				color: red;
			}
		}
		.enter-form-msg + div {
			width: 20%;
		}
	}
}
</style>
