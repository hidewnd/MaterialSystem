<template>
	<div class="record-enter-bind">
		<el-card shadow="alway" :header="$t('message.router.enterBind')">
			<el-row :gutter="24" align="middle">
				<el-col :span="12" :xl="12" :lg="12" :tg="24" :md="24" :sm="24" :xs="24" class="mb20">
					<el-card shadow="hover" header="入库记录详情">
						<el-empty description="暂无数据" v-if="!showForm.isShow" />
						<el-descriptions :column="1" direction="horizontal" size="large" border v-if="showForm.isShow">
							<el-descriptions-item label="入库记录编号" align="center">{{ showForm.recordId }}</el-descriptions-item>
							<el-descriptions-item label="申请人" align="center">{{ showForm.createBy }}</el-descriptions-item>
							<el-descriptions-item label="记录当前总数" align="center">{{ showForm.number }}</el-descriptions-item>
							<el-descriptions-item label="记录当前总价值" align="center">{{ showForm.value }}</el-descriptions-item>
						</el-descriptions>
					</el-card>
				</el-col>
				<el-col :span="12" :xl="12" :lg="12" :tg="24" :md="24" :sm="24" :xs="24" class="mb20">
					<el-card shadow="hover" header="物料登记">
						<el-form
							ref="formRef"
							:model="subForm"
							:rules="rules"
							class="wrap-form"
							label-position="left"
							size="default"
							:inline-message="true"
							label-width="120px"
						>
							<el-form-item label="入库编号" prop="recordId">
								<el-select
									v-model="subForm.recordId"
									filterable
									remote
									reserve-keyword
									clearable
									placeholder="请输入入库记录编号"
									:remote-method="recordRemote"
									:loading="recordParam.loading"
								>
									<el-option v-for="item in recordOptions" :key="item.recordId" :label="item.recordId" :value="item.values" autofocus="false" />
								</el-select>
							</el-form-item>
							<el-form-item label="物料名" prop="materialName">
								<el-select
									v-model="subForm.materialId"
									filterable
									remote
									reserve-keyword
									clearable
									placeholder="请输入物料系统名"
									:remote-method="materialRemote"
									:loading="materialParam.loading"
									@change="materialSelectChange"
									autofocus="false"
								>
									<el-option v-for="item in materialoptions" :key="item.value" :label="item.materialName" :value="item.value" />
								</el-select>
							</el-form-item>
							<el-form-item label="登记数量" prop="number">
								<el-input-number
									v-model="subForm.number"
									:min="1"
									controls-position="right"
									placeholder="请输入物料数量"
									autofocus="false"
								></el-input-number>
							</el-form-item>

							<el-form-item>
								<el-button @click="onOpenAddPage">新增物料</el-button>
								<el-button type="primary" @click="submitForm" :disabled="!isSubButton">提交登记</el-button>
							</el-form-item>
						</el-form>
					</el-card>
				</el-col>
			</el-row>
			<el-row :gutter="24">
				<el-col :span="24" class="mb20">
					<el-steps :space="400" :active="3" align-center finish-status="finish">
						<el-step v-for="(item, index) in timelineData" :key="index" :title="item.title" :description="item.description" :icon="item.icon">
						</el-step>
					</el-steps>
				</el-col>
			</el-row>
		</el-card>

		<add-material ref="addRef" />
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw, onMounted, watch, ref } from 'vue';
import { Edit, Cloudy, EditPen, Document, Upload, Coin } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { EnterRecordAPI, RecordCommonAPI } from '/@/api/manager/material/record';
import { useI18n } from 'vue-i18n';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import AddMaterial from './component/ResourcesAddMaterial.vue';

interface RecordItem {
	recordId: string;
	values: string;
	data: {
		sign: number;
		recordId: string;
		number: number;
		values: number;
		createBy: string;
	};
}

interface MaterialItem {
	materialName: string;
	materialNameZh: string;
	value: string;
}

interface FormState {
	isSubButton: Boolean;
	showForm: {
		isShow: boolean;
		sign: number;
		recordId: string;
		number: number;
		value: number;
		createBy: string;
	};
	subForm: {
		recordId: string;
		materialName: string;
		materialId: string;
		number: number;
		sign: number;
	};
	recordParam: {
		loading: boolean;
		type: string;
		size: number;
		page: number;
		arg1: string;
		arg2: string;
	};
	materialParam: {
		loading: boolean;
		type: string;
		way: string;
		column: string;
		arg1: string;
	};
	recordOptions: Array<RecordItem>;
	materialoptions: Array<MaterialItem>;
	rules: object;
	timelineData: Array<object>;
}
export default defineComponent({
	name: 'RecordEnterBind',
	components: {
		Edit: markRaw(Edit),
		Cloudy: markRaw(Cloudy),
		EditPen: markRaw(EditPen),
		Document: markRaw(Document),
		Upload: markRaw(Upload),
		Coin: markRaw(Coin),
		AddMaterial,
	},
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const addRef = ref();
		const state = reactive<FormState>({
			isSubButton: false,
			showForm: {
				isShow: false,
				sign: 0,
				recordId: '',
				number: 0,
				value: 0.0,
				createBy: '',
			},
			subForm: {
				sign: 0,
				recordId: '',
				materialId: '',
				materialName: '',
				number: 0,
			},
			recordParam: {
				loading: false,
				type: 'enter',
				size: 10,
				page: 1,
				arg1: 'recordId',
				arg2: '',
			},
			materialParam: {
				loading: false,
				type: 'material',
				way: 'like',
				column: 'material_name',
				arg1: '',
			},
			recordOptions: [],
			materialoptions: [],
			rules: {
				number: [
					{ required: true, message: '请输入物料总数量', trigger: 'blur' },
					{ required: true, message: '请输入物料总数量', trigger: 'change' },
				],
				recordId: [
					{ required: true, message: '请填写入库记录编号', trigger: 'blur' },
					{ required: true, message: '请填写入库记录编号', trigger: 'change' },
				],
				materialId: [
					{ required: true, message: '请填写入库物料系统名', trigger: 'blur' },
					{ required: true, message: '请填写入库物料系统名', trigger: 'change' },
				],
			},
			timelineData: [
				{ title: '填写申请', description: '填写入库申请', icon: Edit, status: 'process' },
				{ title: '提交申请', description: '提交入库申请，系统初始化入库记录', icon: Upload, status: 'process' },
				{ title: '申请审核', description: '财务主管进行入库申请审核', icon: Cloudy, status: 'process' },
				{ title: '入库登记', description: '登记需要入库的物料', icon: EditPen, status: 'process' },
				{ title: '入库审核', description: '仓库管理员进行入库记录二次审核', icon: Cloudy, status: 'process' },
				{ title: '物料入库', description: '物料正式入库，入库成功后生成该批次物料二维码', icon: Coin, status: 'process' },
				{ title: '入库完成', description: '物料全部入库后，入库记录自动完成，相关信息整理归档', icon: Document, status: 'finish' },
			],
		});

		const materialNameList: Array<object> = [];

		// 提交登记
		function submitForm() {
			proxy.$refs.formRef.validate((valid: boolean) => {
				if (valid) {
					ElMessageBox.confirm(
						`是否确定为入库记录[${state.subForm.recordId}]登记物料:${state.subForm.materialName}, 登记数量：${state.subForm.number}`,
						'提示',
						{
							confirmButtonText: t('message.resources.common.confirmButtonText'),
							cancelButtonText: t('message.resources.common.cancelButtonText'),
							type: 'warning',
						}
					)
						.then(() => {
							setTimeout(() => {
								const param = {
									recordId: state.subForm.recordId,
									materialId: state.subForm.materialId,
									number: state.subForm.number,
								};
								console.log(param);
								EnterRecordAPI()
									.enterBind(state.subForm)
									.then((res) => {
										ElMessage.success('入库登记成功');
										initDescription(state.subForm.recordId);
									});
							}, 300);
						})
						.catch(() => {});
				}
			});
		}

		function recordRemote(query: string) {
			if (query) {
				state.recordParam.arg2 = query;
				state.recordParam.loading = true;
				RecordCommonAPI()
					.queryPage(`type=${state.recordParam.type}&arg1=${state.recordParam.arg1}&arg2=${state.recordParam.arg2}`)
					.then((res) => {
						if (res.data.element) {
							let list: any = JSON.stringify(res.data.element);
							list = JSON.parse(list);
							const array: Array<RecordItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (element.status == 2) {
									array.push({
										recordId: element.recordId,
										values: element.recordId,
										data: {
											sign: element.sign,
											recordId: element.recordId,
											number: element.number,
											values: element.values,
											createBy: element.createBy,
										},
									});
								}
							}
							state.recordOptions = array;
							state.recordParam.loading = false;
						}
					});
			} else {
				initRecordOptions();
			}
		}

		function materialRemote(query: string) {
			if (query) {
				state.materialParam.loading = true;
				state.materialParam.arg1 = query;
				MaterialQueryAPI()
					.queryList(
						`type=${state.materialParam.type}&way=${state.materialParam.way}&column=${state.materialParam.column}&arg1=${state.materialParam.arg1}`
					)
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
							state.materialParam.loading = false;
						}
					});
			} else {
				initMaterialOptions();
			}
		}

		function materialSelectChange(materialId: any) {
			for (let i = 0; i < materialNameList.length; i++) {
				const element: any = materialNameList[i];
				if (element.materialId === materialId) {
					state.subForm.materialName = element.materialName;
					return;
				}
			}
		}

		function initRecordOptions() {
			state.recordParam.loading = true;
			RecordCommonAPI()
				.queryPage(`type=${state.recordParam.type}`)
				.then((res) => {
					if (res.data.element) {
						let list: any = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						const array: Array<RecordItem> = [];
						for (let i = 0; i < list.length; i++) {
							const element: any = list[i];
							if (element.status == 2) {
								array.push({
									recordId: element.recordId,
									values: element.recordId,
									data: {
										sign: element.sign,
										recordId: element.recordId,
										number: element.number,
										values: element.values,
										createBy: element.createBy,
									},
								});
							}
						}
						state.recordOptions = array;
						state.recordParam.loading = false;
					}
				});
		}

		function initMaterialOptions() {
			// state.materialParam.loading = true;
			MaterialQueryAPI()
				.queryList(`type=${state.materialParam.type}&way=all`)
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
						state.materialParam.loading = false;
					}
				});
		}

		function onOpenAddPage() {
			addRef.value.openDialog();
		}

		// 页面加载时
		onMounted(() => {
			initRecordOptions();
		});

		watch(
			() => state.subForm.recordId,
			(recordId) => {
				if (recordId) {
					initDescription(recordId);
				}
			},
			{
				deep: true,
				immediate: true,
			}
		);

		watch(
			() => state.subForm.materialId,
			(materialId) => {
				if (materialId) {
					if (state.subForm.recordId.length > 0 && materialId.length > 0) {
						state.isSubButton = true;
					}
				}
			},
			{
				deep: true,
				immediate: true,
			}
		);

		function initDescription(recordId: string) {
			RecordCommonAPI()
				.queryOne(`type=enter&recordId=${recordId}`)
				.then((res) => {
					if (res.data) {
						let element: any = JSON.stringify(res.data);
						element = JSON.parse(element);
						state.showForm.sign = element.sign;
						state.showForm.recordId = element.recordId;
						state.showForm.number = element.number;
						state.showForm.value = element.value;
						state.showForm.createBy = element.createBy;
						state.showForm.isShow = true;
					}
				});
		}

		return {
			submitForm,
			recordRemote,
			materialRemote,
			materialSelectChange,
			addRef,
			onOpenAddPage,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.record-enter-bind {
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
