<template>
	<div class="resources-edit-depot-container">
		<el-dialog title="修改仓库" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" :rules="rules" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库编号" label-width="auto">
							<el-input v-model="ruleForm.depotId" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库系统名" label-width="auto" prop="depotName">
							<el-input
								v-model="ruleForm.depotName"
								@input="onVerifyCnAndSpace($event)"
								placeholder="请输入仓库系统名"
								maxlength="30"
								show-word-limit
								clearable
							></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库中文名" label-width="auto" prop="depotNameZh">
							<el-input
								v-model="ruleForm.depotNameZh"
								@input="onVerifyAndSpace($event)"
								placeholder="请输入仓库中文名"
								maxlength="30"
								show-word-limit
								clearable
							></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库最大容量" label-width="auto" prop="maxCapacity">
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
						<el-form-item label="仓库容量阈值" label-width="auto" prop="threshold">
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
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库当前容量" label-width="auto">
							<el-input-number v-model="ruleForm.capacity" :min="1" :max="ruleForm.maxCapacity" controls-position="right" disabled></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="启用状态">
							<el-switch
								v-model="ruleForm.status"
								inline-prompt
								active-text="启"
								:active-value="0"
								inactive-text="禁"
								:inactive-value="1"
							></el-switch>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="仓库地址" label-width="auto" prop="place">
							<el-input v-model="ruleForm.place" type="textarea" placeholder="请输入仓库地址" maxlength="150" show-word-limit clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="仓库描述" label-width="auto" prop="description">
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
					<el-button @click="onCancel" size="default">关 闭</el-button>
					<el-button type="primary" @click="onSubmit" size="default">修 改</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, onMounted, defineComponent } from 'vue';
import { MaterialDepot } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { DepotUpdateAPI } from '/@/api/manager/material/depot';
import { verifyAndSpace, verifyCnAndSpace } from '/@/utils/toolsValidate';

interface UserState {
	isShowDialog: boolean;
	ruleForm: MaterialDepot;
	rules: object;
}

export default defineComponent({
	name: 'ResourcesEditDepot',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			ruleForm: {
				depotId: '',
				depotName: '',
				depotNameZh: '',
				place: '',
				description: '',
				maxCapacity: 100,
				capacity: 0,
				threshold: 90,
				message: '',
				status: 0,
				delFlag: 0,
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				rackList: [],
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
				maxCapacity: [
					{ required: true, message: '请输入仓库最大容量', trigger: 'blur' },
					{ required: true, type: 'number', message: '请输入仓库最大容量', trigger: 'change' },
				],
				threshold: [
					{ required: true, message: '请输入仓库容量阈值', trigger: 'blur' },
					{ required: true, type: 'number', message: '请输入仓库容量阈值', trigger: 'change' },
				],
				description: [
					{ required: true, message: '请输入仓库描述', trigger: 'blur' },
					{ required: true, message: '请输入仓库描述', trigger: 'change' },
				],
				place: [
					{ required: true, message: '请输入仓库地址', trigger: 'blur' },
					{ required: true, message: '请输入仓库地址', trigger: 'change' },
				],
			},
		});

		// 打开弹窗
		function openDialog(row: MaterialDepot) {
			state.ruleForm = row;
			state.isShowDialog = true;
		}

		// 关闭弹窗
		function closeDialog() {
			state.isShowDialog = false;
			setTimeout(() => {
				context.emit('refresh');
			}, 1500);
		}

		// 取消
		function onCancel() {
			closeDialog();
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
		// 提交
		function onSubmit() {
			if (!checkParam()) {
				ElMessage.warning(t('message.resources.common.formWarning'));
				return;
			}
			DepotUpdateAPI()
				.update(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		// 参数检查
		function checkParam(): boolean {
			state.ruleForm.depotName = verifyCnAndSpace(state.ruleForm.depotName);
			state.ruleForm.depotNameZh = verifyAndSpace(state.ruleForm.depotNameZh);
			if (state.ruleForm.depotId && state.ruleForm.depotName && state.ruleForm.depotNameZh) {
				if (state.ruleForm.maxCapacity && state.ruleForm.threshold && state.ruleForm.maxCapacity >= state.ruleForm.threshold) {
					return true;
				}
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

		// 页面加载时
		onMounted(() => {});

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
