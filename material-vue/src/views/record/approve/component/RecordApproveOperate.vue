<template>
	<div class="record-approve-operate-container">
		<el-dialog :title="'出入库' + operateTypeName + '；' + ruleForm.row.recordId" v-model="isShowDialog" width="40%" draggable>
			<el-row :gutter="24">
				<el-col :xs="24" :sm="24" :md="24" :lg="10" :xl="10" class="mb20">
					<el-descriptions style="width: 100%" :column="1" size="large">
						<el-descriptions-item label="申请人" align="”center" autofocus="false">
							<el-tag size="large" round>{{ ruleForm.row.createBy }}</el-tag>
						</el-descriptions-item>
						<el-descriptions-item label="出入库记录类型" align="”center" autofocus="false">
							<a>{{ ruleForm.row.recordType }}</a>
						</el-descriptions-item>
						<el-descriptions-item label="申请物料总数" align="”center" autofocus="false">
							<a>{{ ruleForm.row.number }}</a>
						</el-descriptions-item>
						<el-descriptions-item label="申请物料总价值" align="”center" autofocus="false">
							<a>{{ ruleForm.row.value }}</a>
						</el-descriptions-item>
						<el-descriptions-item label="记录当前状态" align="”center" autofocus="false">
							<a>{{ ruleForm.row.recordStatus }}</a>
						</el-descriptions-item>
					</el-descriptions>
					<el-row class="mb-4">
						<el-button type="danger" @click="subAPprove(false)" round>不通过</el-button>
						<el-button type="success" @click="subAPprove(true)" round>通过</el-button>
					</el-row>
				</el-col>
				<el-col :xs="24" :sm="24" :md="24" :lg="14" :xl="14" class="mb20">
					<el-descriptions title="申请物料" direction="vertical" :column="4" size="default" border>
						<template v-for="item in ruleForm.MaterialList" :key="item.materialId">
							<el-descriptions-item label="物料ID">{{ item.materialId }}</el-descriptions-item>
							<el-descriptions-item label="物料系统名">{{ item.materialName }}</el-descriptions-item>
							<el-descriptions-item label="物料中文名">{{ item.materialNameZh }}</el-descriptions-item>
							<el-descriptions-item label="数量">{{ item.number }}</el-descriptions-item>
						</template>
					</el-descriptions>
				</el-col>
			</el-row>
			<template #footer>
				<span class="dialog-footer">
					<el-button type="primary" @click="onCancel" size="default">关 闭</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import { reactive, toRefs, defineComponent, getCurrentInstance, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import { OuterRecordAPI, RecordCommonAPI } from '/@/api/manager/material/record';

interface MaterialItem {
	materialId: string;
	materialName: string;
	materialNameZh: string;
	number: number;
}

interface State {
	isShowDialog: boolean;
	operateType: number;
	operateTypeName: string;
	ruleForm: {
		row: {
			recordId: string;
			recordType: string;
			createBy: string;
			number: number;
			value: number;
			recordStatus: string;
			status: number;
		};
		isQuery: boolean;
		MaterialList: Array<MaterialItem>;
	};
}

export default defineComponent({
	name: 'ResourcesAddMaterial',
	setup(props, context) {
		const { proxy } = getCurrentInstance() as any;
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			operateType: 0,
			operateTypeName: '',
			ruleForm: {
				row: {
					recordId: '',
					recordType: '',
					createBy: '',
					number: 0,
					value: 0.0,
					recordStatus: '',
					status: 0,
				},
				MaterialList: [],
				isQuery: false,
			},
		});

		// 打开弹窗
		function openDialog(row: any) {
			state.isShowDialog = true;
			if (row.operateType != undefined) {
				state.operateType = row.operateType;
			}
			if (row.data) {
				state.ruleForm.row = {
					recordId: row.data.recordId,
					recordType: row.data.recordType,
					createBy: row.data.createBy,
					number: row.data.number,
					value: row.data.value,
					recordStatus: row.data.recordStatus,
					status: row.data.status,
				};
			}

			if (state.operateType === 0) {
				state.operateTypeName = '申请审批';
			} else if (state.operateType === 1) {
				state.operateTypeName = '执行审批';
			}
			initMaterialList();
		}

		// 初始化请求
		function initMaterialList() {
			if (state.ruleForm.row.recordId) {
				if (state.ruleForm.row.recordType === '入库') {
					initEnterRecord(state.ruleForm.row.recordId);
				} else if (state.ruleForm.row.recordType === '出库') {
					initOuterecord(state.ruleForm.row.recordId);
				}
			}
		}

		function initEnterRecord(recordId: string) {
			RecordCommonAPI()
				.queryOne(`type=enter&recordId=${recordId}`)
				.then((res) => {
					let data: any = JSON.stringify(res.data);
					data = JSON.parse(data);
					if (data && data.matNumber) {
						const array: Array<MaterialItem> = [];
						let map: any = data.matNumber;
						for (let materialId in map) {
							array.push({
								materialId: materialId,
								materialName: '',
								materialNameZh: '',
								number: map[materialId],
							});
						}
						state.ruleForm.MaterialList = array;
					}
				});
		}

		function initOuterecord(recordId: string) {
			RecordCommonAPI()
				.queryOne(`type=outer&recordId=${recordId}`)
				.then((res) => {
					let data: any = JSON.stringify(res.data);
					data = JSON.parse(data);
					if (data && data.materialNumberMap) {
						const array: Array<MaterialItem> = [];
						let map: any = data.materialNumberMap;
						for (let materialId in map) {
							array.push({
								materialId: materialId,
								materialName: '',
								materialNameZh: '',
								number: map[materialId],
							});
						}
						state.ruleForm.MaterialList = array;
					}
				});
		}

		function subAPprove(result: boolean) {
			let msg = '';
			if (result) {
				msg = '确定通过该出入库审批？';
			} else {
				msg = '确定拒绝该出入库审批？';
			}
			ElMessageBox.confirm(msg, '提示', {
				confirmButtonText: t('message.resources.common.confirmButtonText'),
				cancelButtonText: t('message.resources.common.cancelButtonText'),
				type: 'warning',
			})
				.then(() => {
					setTimeout(() => {
						let res = 2; // 审批结果
						if (result) {
							res = 1;
						}
						let operation = '';
						if (state.operateType === 0) {
							operation = 'execute';
						} else if (state.operateType === 1) {
							operation = 'apply';
						} else {
							ElMessage.error('未知类型');
							return;
						}
						RecordCommonAPI()
							.approval(`recordId=${state.ruleForm.row.recordId}&operation=${operation}&result=${res}`)
							.then((res) => {
								ElNotification({
									title: '审核成功',
									message: '出入库申请审批成功',
									type: 'success',
								});
								context.emit('refresh');
								setTimeout(() => {
									closeDialog();
								}, 500);
							});
					}, 300);
				})
				.catch(() => {});
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				row: {
					recordId: '',
					recordType: '',
					createBy: '',
					number: 0,
					value: 0.0,
					recordStatus: '',
					status: 0,
				},
				MaterialList: [],
				isQuery: false,
			};
			state.isShowDialog = false;
		}

		// 取消
		function onCancel() {
			closeDialog();
		}

		watch(
			() => state.ruleForm.MaterialList,
			() => {
				if (state.ruleForm.MaterialList.length > 0) {
					MaterialQueryAPI()
						.queryList('type=material&way=all')
						.then((res) => {
							let list: any = JSON.stringify(res.data);
							list = JSON.parse(list);
							if (list) {
								for (let index = 0; index < state.ruleForm.MaterialList.length; index++) {
									const element: MaterialItem = state.ruleForm.MaterialList[index];
									let material = list.find((item: any) => item.materialId === element.materialId);
									if (material != undefined) {
										state.ruleForm.MaterialList[index].materialName = material.materialName;
										state.ruleForm.MaterialList[index].materialNameZh = material.materialNameZh;
									}
								}
							}
						});
				}
			},
			{ deep: true, immediate: true }
		);

		return {
			openDialog,
			closeDialog,
			onCancel,
			subAPprove,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss" scoped>
.record-approve-operate-container {
	.qrcode {
		margin: auto;
		width: 150px;
		height: 150px;
	}
}
</style>
