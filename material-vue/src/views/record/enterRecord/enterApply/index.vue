<template>
	<div class="record-enter-apply">
		<el-card :header="$t('message.router.enterApply')" shadow="always">
			<el-row :gutter="35">
				<el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="mb20">
					<el-form
						ref="formRef"
						:model="ruleForm"
						:rules="rules"
						class="wrap-form"
						label-position="top"
						size="default"
						:inline-message="true"
						label-width="120px"
					>
						<el-form-item label="预计总数量" prop="number">
							<div class="enter-form-msg">该行填写的是本次入库申请预计申请的物料总数，最终入库总数以实际为准。</div>
							<el-input-number
								v-model="ruleForm.number"
								:min="1"
								controls-position="right"
								placeholder="请输入物料数量"
								autofocus="false"
							></el-input-number>
						</el-form-item>
						<el-form-item label="预计总价值" prop="number">
							<div class="enter-form-msg">该行填写的是本次入库申请预计申请的物料总价值，最终入库物料总值以实际为准。</div>
							<el-input-number
								v-model="ruleForm.value"
								:min="0.0"
								:precision="2"
								:step="0.1"
								controls-position="right"
								placeholder="请输入金额"
								autofocus="false"
							></el-input-number>
						</el-form-item>
						<el-form-item>
							<el-button type="primary" @click="submitForm">提交申请</el-button>
							<el-button @click="resetForm">重置申请</el-button>
						</el-form-item>
					</el-form>
				</el-col>
			</el-row>
			<el-row :gutter="35">
				<el-col :span="35" :xs="35" :sm="35" :md="35" :lg="35" :xl="35" class="mb20">
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
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw } from 'vue';
import { Edit, Cloudy, EditPen, Document, Upload, Coin } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { EnterRecordAPI } from '/@/api/manager/material/record';
import { useI18n } from 'vue-i18n';
interface FormState {
	ruleForm: {
		sign: number;
		number: number;
		value: number;
	};
	rules: object;
	timelineData: Array<object>;
}

export default defineComponent({
	name: 'RecordEnterApply',
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
			ruleForm: {
				sign: 0,
				number: 1,
				value: 0.0,
			},
			rules: {
				number: [
					{ required: true, message: '请输入物料总数量', trigger: 'blur' },
					{ required: true, message: '请输入物料总数量', trigger: 'change' },
				],
				value: [
					{ required: true, message: '请填写入库记录总金额', trigger: 'blur' },
					{ required: true, message: '请填写入库记录总金额', trigger: 'change' },
				],
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

		function submitForm() {
			proxy.$refs.formRef.validate((valid: boolean) => {
				if (valid) {
					ElMessageBox.confirm('是否确定提交入库申请？\n' + `物料总数量：${state.ruleForm.number}；物料总价值：￥${state.ruleForm.value}。`, '提示', {
						confirmButtonText: t('message.resources.common.confirmButtonText'),
						cancelButtonText: t('message.resources.common.cancelButtonText'),
						type: 'warning',
					})
						.then(() => {
							setTimeout(() => {
								EnterRecordAPI()
									.generalEnter(state.ruleForm)
									.then((res) => {
										if (res) {
											ElMessage.success(`提交成功，入库记录编号：${res.data}`);
											state.ruleForm.value = 0.0;
											state.ruleForm.number = 1;
										}
									});
							}, 300);
						})
						.catch(() => {});
				}
			});
		}

		function resetForm() {
			proxy.$refs.formRef.resetFields();
		}

		return {
			submitForm,
			resetForm,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.record-enter-apply {
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
