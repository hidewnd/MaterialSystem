<template>
	<div class="resources-add-depot-container">
		<el-dialog title="新增仓库" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" :rules="rules" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库系统名" label-width="auto" prop="depotName">
							<el-input
								v-model="ruleForm.depotName"
								placeholder="请输入仓库系统名"
								@input="onVerifyCnAndSpace($event)"
								maxlength="20"
								show-word-limit
								clearable
							></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库中文名" label-width="auto" prop="depotNameZh">
							<el-input
								v-model="ruleForm.depotNameZh"
								placeholder="请输入仓库中文名"
								@input="onVerifyAndSpace($event)"
								maxlength="30"
								show-word-limit
								clearable
							></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库最大容量" label-width="auto">
							<el-input-number
								v-model="ruleForm.maxCapacity"
								:min="1"
								controls-position="right"
								placeholder="请输入仓库最大容量"
								autofocus="false"
							></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库容量阈值" label-width="auto">
							<el-input-number
								v-model="ruleForm.threshold"
								:min="1"
								:max="ruleForm.maxCapacity"
								controls-position="right"
								placeholder="请输入仓库容量阈值"
								autofocus="false"
							></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="仓库地址" label-width="auto" prop="place">
							<el-input v-model="ruleForm.place" type="textarea" placeholder="请输入仓库地址" maxlength="150" show-word-limit clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="仓库描述" label-width="auto">
							<el-input
								v-model="ruleForm.description"
								type="textarea"
								placeholder="请输入仓库描述"
								maxlength="150"
								show-word-limit
								clearable
							></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="备注" label-width="auto">
							<el-input v-model="ruleForm.remark" type="textarea" placeholder="请输入备注" maxlength="150" show-word-limit clearable></el-input>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel" size="default">取 消</el-button>
					<el-button type="primary" @click="onSubmit" size="default">新 增</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, defineComponent } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { verifyCnAndSpace, verifyAndSpace } from '/@/utils/toolsValidate';
import { DepotUpdateAPI } from '/@/api/manager/material/depot';

interface State {
	isShowDialog: boolean;
	ruleForm: {
		depotName: string;
		depotNameZh: string;
		maxCapacity: number;
		threshold: number;
		place: string;
		description: string;
		remark: string;
	};
	rules: object;
}

export default defineComponent({
	name: 'ResourcesAddDepot',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				depotName: '',
				depotNameZh: '',
				maxCapacity: 100,
				threshold: 90,
				place: '',
				description: '',
				remark: '',
			},
			rules: {
				depotName: [
					{ required: true, message: '请输入仓库系统名', trigger: 'blur' },
					{ required: true, message: '请输入仓库系统名', trigger: 'change' },
				],
				depotNameZh: [
					{ required: true, message: '请输入仓库中文名', trigger: 'blur' },
					{ required: true, message: '请输入仓库中文名', trigger: 'change' },
				],
				place: [
					{ required: true, message: '请输入仓库地址', trigger: 'blur' },
					{ required: true, message: '请输入仓库地址', trigger: 'change' },
				],
			},
		});

		// 打开弹窗
		function openDialog() {
			state.isShowDialog = true;
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				depotName: '',
				depotNameZh: '',
				maxCapacity: 100,
				threshold: 90,
				place: '',
				description: '',
				remark: '',
			};
			state.isShowDialog = false;
			setTimeout(() => {
				context.emit('refresh');
			}, 1500);
		}

		// 取消
		function onCancel() {
			ElMessageBox.confirm(t('message.resources.common.cancelMessage'), t('message.resources.common.cancelTitle'), {
				confirmButtonText: t('message.resources.common.confirmButtonText'),
				cancelButtonText: t('message.resources.common.cancelButtonText'),
				type: 'warning',
			})
				.then(() => {
					onSubmit();
				})
				.catch(() => {
					closeDialog();
				});
		}

		// 新增
		function onSubmit() {
			if (!checkRuleForm()) {
				ElMessage.warning(t('message.resources.common.formWarning'));
				return;
			}
			DepotUpdateAPI()
				.insert(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		// 数据检查
		function checkRuleForm(): boolean {
			state.ruleForm.depotName = verifyCnAndSpace(state.ruleForm.depotName);
			state.ruleForm.depotNameZh = verifyAndSpace(state.ruleForm.depotNameZh);
			state.ruleForm.place = verifyAndSpace(state.ruleForm.place);
			state.ruleForm.description = verifyAndSpace(state.ruleForm.description);
			state.ruleForm.remark = verifyAndSpace(state.ruleForm.remark);
			if (state.ruleForm.depotName && state.ruleForm.maxCapacity && state.ruleForm.threshold) {
				if (state.ruleForm.maxCapacity < state.ruleForm.threshold) {
					state.ruleForm.threshold = state.ruleForm.maxCapacity;
				}
				return true;
			}
			return false;
		}

		// 禁止输入中文
		function onVerifyCnAndSpace(val: string) {
			state.ruleForm.depotName = verifyCnAndSpace(val);
		}
		// 禁止输入空格
		const onVerifyAndSpace = (val: string) => {
			state.ruleForm.depotNameZh = verifyAndSpace(val);
		};
		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			onVerifyCnAndSpace,
			onVerifyAndSpace,
			...toRefs(state),
		};
	},
});
</script>
