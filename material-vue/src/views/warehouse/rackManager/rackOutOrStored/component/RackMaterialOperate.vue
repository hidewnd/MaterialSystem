<template>
	<div class="rack-material-operate-container">
		<el-dialog title="出入库操作" v-model="isShowDialog" width="769px" draggable>
			<el-row :gutter="24">
				<el-col :xs="24" :sm="24" :md="24" :lg="10" :xl="10" class="mb10">
					<el-descriptions style="width: 100%" :column="1" size="large">
						<el-descriptions-item label="当前仓库">{{ ruleform.depotName }}</el-descriptions-item>
						<el-descriptions-item label="当前货架">{{ ruleform.rackId }}</el-descriptions-item>
						<el-descriptions-item label="当前物料编号" v-if="isOuter">{{ ruleform.materialId }}</el-descriptions-item>
						<el-descriptions-item label="当前物料系统名" v-if="isOuter">{{ ruleform.materialName }}</el-descriptions-item>
						<el-descriptions-item label="当前物料中文名" v-if="isOuter">{{ ruleform.materialNameZh }}</el-descriptions-item>
						<el-descriptions-item label="库存数量" v-if="isOuter">{{ ruleform.number }}</el-descriptions-item>
					</el-descriptions>
				</el-col>
				<el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="mb10">
					<template v-if="isOuter">
						<el-form ref="outerformRef" :model="outerform" size="default" label-position="left">
							<el-row :gutter="24">
								<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
									<el-form-item
										label="出库记录编号"
										label-width="auto"
										prop="recordId"
										:rules="[
											{ required: true, message: `出库记录编号不能为空`, trigger: 'blur' },
											{ required: true, message: `出库记录编号不能为空`, trigger: 'change' },
										]"
									>
										<el-select
											v-model="outerform.recordId"
											filterable
											remote
											reserve-keyword
											clearable
											placeholder="请输入出库编号"
											:remote-method="searchRecordRemote"
											:loading="searchRecord.loading"
											@change="searchRecordChange"
											autofocus="false"
											style="max-width: 180px"
										>
											<el-option v-for="item in searchRecord.options" :key="item.recordId" :label="item.recordId" :value="item.recordId" />
										</el-select>
									</el-form-item>
								</el-col>
								<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
									<el-form-item
										label="出库凭证"
										label-width="auto"
										prop="proof"
										:rules="[
											{ required: true, message: `出库凭证不能为空`, trigger: 'blur' },
											{ required: true, message: `出库凭证不能为空`, trigger: 'change' },
										]"
									>
										<el-input v-model="outerform.proof" placeholder="请输入出库凭证" clearable />
									</el-form-item>
								</el-col>
							</el-row>
						</el-form>
					</template>
					<template v-else>
						<el-form ref="enterformRef" :model="enterform" size="default" label-position="left">
							<el-row :gutter="24">
								<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
									<el-form-item label="入库记录编号" label-width="auto" prop="recordId">
										<el-select
											v-model="enterform.recordId"
											filterable
											remote
											reserve-keyword
											clearable
											placeholder="请输入库编号"
											:remote-method="searchRecordRemote"
											:loading="searchRecord.loading"
											@change="searchRecordChange"
											autofocus="false"
											style="max-width: 180px"
											:rules="[
												{ required: true, message: `入库记录编号不能为空`, trigger: 'blur' },
												{ required: true, message: `入库记录编号不能为空`, trigger: 'change' },
											]"
										>
											<el-option v-for="item in searchRecord.options" :key="item.recordId" :label="item.recordId" :value="item.recordId" />
										</el-select>
									</el-form-item>
								</el-col>
								<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
									<el-form-item
										label="待入库物料"
										label-width="auto"
										prop="materialId"
										:rules="[
											{ required: true, message: `待入库物料编号不能为空`, trigger: 'blur' },
											{ required: true, message: `待入库物料编号不能为空`, trigger: 'change' },
										]"
									>
										<el-select
											v-model="enterform.materialId"
											filterable
											remote
											reserve-keyword
											clearable
											placeholder="输入物料系统名进行查询"
											:remote-method="searchMateriakRemote"
											:loading="searchMaterial.loading"
											@change="searchMaterialChange"
											autofocus="false"
											style="max-width: 180px"
											:rules="[
												{ required: true, message: `入库物料编号不能为空`, trigger: 'blur' },
												{ required: true, message: `入库物料编号不能为空`, trigger: 'change' },
											]"
										>
											<el-option
												v-for="item in searchMaterial.materialOptions"
												:key="item.materialId"
												:label="item.materialName"
												:value="item.materialId"
											/>
										</el-select>
									</el-form-item>
								</el-col>
								<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
									<el-form-item label="入库数量" label-width="auto" prop="number">
										<el-input-number v-model="enterform.number" :min="1" controls-position="right" placeholder="请输入入库数量" />
									</el-form-item>
								</el-col>
							</el-row>
						</el-form>
					</template>
				</el-col>
			</el-row>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel" size="default">取 消</el-button>
					<el-button type="primary" @click="onSubmit" size="default" :disabled="!isAllow">提 交</el-button>
				</span>
			</template>
			<template>
				<el-dialog v-model="innerVisible" width="30%" title="Inner Dialog" append-to-body>
					<el-descriptions direction="horizontal" style="width: 100%" :column="1" size="large" border>
						<el-descriptions-item label="入库结果" align="”center" autofocus="false">
							<el-input v-model="enterResult.msg" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="物料二维码值" align="”center" autofocus="false">
							<el-input v-model="enterResult.qrcode" type="textarea" :rows="4" readonly autofocus="false" />
						</el-descriptions-item>
					</el-descriptions>
				</el-dialog>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, defineComponent, watch, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import { EnterRecordAPI, OuterRecordAPI, RecordCommonAPI } from '/@/api/manager/material/record';
import QRCode from 'qrcodejs2-fixes';

interface RecordItem {
	recordId: string;
	sign: number;
	status: number;
	createBy: string;
	apply: number;
}

interface MaterialItem {
	materialId: string;
	materialName: string;
	materialNameZh: string;
}

interface State {
	isShowDialog: boolean;
	isAllow: boolean;
	isOuter: boolean;
	innerVisible: boolean;
	ruleform: {
		depotId: string;
		depotName: string;
		rackId: string;
		materialId: string;
		materialName: string;
		materialNameZh: string;
		number: number;
	};
	outerform: {
		recordId: string;
		qrcode: string;
		proof: string;
	};
	enterform: {
		recordId: string;
		materialId: string;
		rackId: string;
		number: number;
	};
	searchRecord: {
		options: Array<RecordItem>;
		loading: boolean;
	};
	searchMaterial: {
		materialOptions: Array<MaterialItem>;
		loading: boolean;
	};
	enterResult: {
		msg: string;
		qrcode: string;
	};
}

export default defineComponent({
	name: 'RackMaterialOperate',
	setup(props, context) {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const state = reactive<State>({
			isShowDialog: false,
			isAllow: false,
			isOuter: false,
			innerVisible: false,
			ruleform: {
				depotId: '',
				depotName: '',
				rackId: '',
				materialId: '',
				materialName: '',
				materialNameZh: '',
				number: 0,
			},
			outerform: {
				recordId: '',
				qrcode: '',
				proof: '',
			},
			enterform: {
				recordId: '',
				materialId: '',
				rackId: '',
				number: 1,
			},
			searchRecord: {
				loading: false,
				options: [],
			},
			searchMaterial: {
				loading: false,
				materialOptions: [],
			},
			enterResult: {
				msg: '',
				qrcode: '',
			},
		});

		// 打开弹窗
		function openDialog(row: any, isOuter: boolean) {
			state.isShowDialog = true;
			state.isOuter = isOuter;
			if (row) {
				state.ruleform.depotId = row.depotId;
				state.ruleform.depotName = row.depotName;
				state.ruleform.rackId = row.rackId;
				state.ruleform.materialId = row.materialId;
				state.ruleform.number = row.number;
				initName();
				if (isOuter) {
					state.outerform.qrcode = row.qrcode;
				} else {
					state.enterform.rackId = row.rackId;
				}
			}
		}

		function initName() {
			if (state.ruleform.materialId && state.isOuter) {
				MaterialQueryAPI()
					.queryOne(`type=material&id=${state.ruleform.materialId}`)
					.then((res) => {
						let element: any = JSON.stringify(res.data);
						element = JSON.parse(element);
						if (element) {
							state.ruleform.materialName = element.materialName;
							state.ruleform.materialNameZh = element.materialNameZh;
						}
					});
			}
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleform = {
				depotId: '',
				depotName: '',
				rackId: '',
				materialId: '',
				materialName: '',
				materialNameZh: '',
				number: 0,
			};
			state.outerform = {
				recordId: '',
				qrcode: '',
				proof: '',
			};
			state.enterform = {
				recordId: '',
				materialId: '',
				rackId: '',
				number: 1,
			};
			state.isShowDialog = false;
			state.innerVisible = false;
			setTimeout(() => {
				context.emit('refresh');
			}, 1500);
		}

		// 取消
		function onCancel() {
			closeDialog();
		}

		// 提交修改
		function onSubmit() {
			if (state.isOuter) {
				submitOuter();
			} else {
				submitEnter();
			}
		}

		// 提交物料出库
		function submitOuter() {
			if (state.outerform.qrcode == undefined) {
				ElMessage.warning('缺少该物料二维码');
				return;
			}
			proxy.$refs.outerformRef.validate((valid: boolean) => {
				if (valid) {
					ElMessageBox.confirm(`确定执行该物料出库吗`, '提示', {
						confirmButtonText: t('message.resources.common.confirmButtonText'),
						cancelButtonText: t('message.resources.common.cancelButtonText'),
						type: 'warning',
					}).then(() => {
						OuterRecordAPI()
							.outerRecord({
								recordId: state.outerform.recordId,
								proof: state.outerform.proof,
								qrcode: state.outerform.qrcode,
							})
							.then((res) => {
								closeDialog();
								ElNotification({
									title: '通知',
									message: '物料出库成功',
									type: 'success',
								});
							});
					});
				}
			});
		}

		// 提交物料入库
		function submitEnter() {
			if (state.enterform.rackId == undefined || !state.enterform.recordId) {
				ElMessage.warning('缺少入库参数');
				return;
			}
			proxy.$refs.enterformRef.validate((valid: boolean) => {
				if (valid) {
					ElMessageBox.confirm(`确定执行该物料入库吗`, '提示', {
						confirmButtonText: t('message.resources.common.confirmButtonText'),
						cancelButtonText: t('message.resources.common.cancelButtonText'),
						type: 'warning',
					}).then(() => {
						EnterRecordAPI()
							.enterSave(state.enterform)
							.then((res) => {
								let result: any = JSON.stringify(res);
								result = JSON.parse(result);
								if (result) {
									state.innerVisible = true;
									state.enterResult.msg = result.msg;
									state.enterResult.qrcode = result.data;
									// ElMessageBox.alert(`物料入库二维码：${result.data}`, result.msg, {
									// 	confirmButtonText: t('message.resources.common.confirmButtonText'),
									// 	type: 'success',
									// 	callback: (action: Action) => {
									// 		closeDialog();
									// 	},
									// });
								}
							});
					});
				}
			});
		}

		// 初始化生成二维码
		const initQrcode = () => {
			proxy.$refs.qrcodeRef.innerHTML = '';
			new QRCode(proxy.$refs.qrcodeRef, {
				text: `${state.enterResult.qrcode}`,
				width: 150,
				height: 150,
				colorDark: '#000000',
				colorLight: '#ffffff',
			});
		};

		// 出入库记录 搜索
		function searchRecordRemote(query: string) {
			if (query) {
				state.searchRecord.loading = true;
				let type = 0;
				if (state.isOuter) {
					type = 1;
				}
				RecordCommonAPI()
					.queryPage(`type=${type}&arg1=recordId&arg2=${query}`)
					.then((res) => {
						let list = JSON.stringify(res.data.element);
						list = JSON.parse(list);
						if (list) {
							const array: Array<RecordItem> = [];
							for (let index = 0; index < list.length; index++) {
								const element: any = list[index];
								if (element.status != undefined && element.status < 4) {
									array.push({
										recordId: element.recordId,
										sign: element.sign,
										status: element.status,
										createBy: element.createBy,
										apply: element.applyApproval,
									});
								}
							}
							state.searchRecord.options = array;
						}
						state.searchRecord.loading = false;
					})
					.catch((err) => {
						state.searchRecord.loading = false;
					});
			}
		}
		function searchRecordChange(query: string) {}

		// 物料搜索
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

		watch(
			() => state.outerform.proof,
			() => {
				if (state.outerform.recordId && state.outerform.proof) {
					state.isAllow = true;
				} else {
					state.isAllow = false;
				}
			},
			{ deep: true, immediate: true }
		);

		watch(
			() => state.enterform.materialId,
			() => {
				if (state.enterform.recordId && state.enterform.materialId) {
					state.isAllow = true;
				} else {
					state.isAllow = false;
				}
			},
			{ deep: true, immediate: true }
		);

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			searchRecordRemote,
			searchRecordChange,
			searchMateriakRemote,
			searchMaterialChange,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.rack-material-operate-container {
	.context {
		text-align: center;
		width: 90%;
	}
	.qrcode {
		margin: auto;
		width: 150px;
		height: 150px;
	}
}
</style>
