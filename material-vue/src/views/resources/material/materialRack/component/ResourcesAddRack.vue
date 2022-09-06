<template>
	<div class="resources-add-rack-container">
		<el-dialog title="新增货架" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="货架最大容量" label-width="auto">
							<el-input-number
								v-model="ruleForm.maxCapacity"
								type="number"
								placeholder="请输入货架系统名"
								:min="1"
								controls-position="right"
								clearable
								autofocus="false"
							></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="货架描述" label-width="auto">
							<el-input v-model="ruleForm.description" type="textarea" placeholder="请输入货架描述" maxlength="150" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="备注" label-width="auto">
							<el-input v-model="ruleForm.remark" type="textarea" placeholder="请输入备注" maxlength="150" clearable></el-input>
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
import { RackUpdateAPI } from '/@/api/manager/material/rack';

interface State {
	isShowDialog: boolean;
	ruleForm: {
		maxCapacity: number;
		description: string;
		remark: string;
	};
}

export default defineComponent({
	name: 'ResourcesAddRack',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				maxCapacity: 100,
				description: '',
				remark: '',
			},
		});

		// 打开弹窗
		function openDialog() {
			state.isShowDialog = true;
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				maxCapacity: 100,
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
			RackUpdateAPI()
				.insert(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		// 数据检查
		function checkRuleForm(): boolean {
			if (state.ruleForm.maxCapacity && state.ruleForm.maxCapacity > 1) {
				return true;
			}
			return false;
		}

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			...toRefs(state),
		};
	},
});
</script>
