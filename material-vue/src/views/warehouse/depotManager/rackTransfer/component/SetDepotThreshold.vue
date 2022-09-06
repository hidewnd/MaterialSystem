<template>
	<div class="set-depot-threshold-container">
		<el-dialog title="设置仓库容量阈值" v-model="isShowDialog" width="769px">
			<el-form :model="ruleform" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="24" :xl="24">
						<el-descriptions :column="1">
							<el-descriptions-item label="当前仓库系统名">{{ ruleform.depotName }}</el-descriptions-item>
							<el-descriptions-item label="当前仓库中文名">{{ ruleform.depotNameZh }}</el-descriptions-item>
							<el-descriptions-item label="当前仓库最大容量">{{ ruleform.maxCapacity }}</el-descriptions-item>
							<el-descriptions-item label="当前容量">{{ ruleform.capacity }}</el-descriptions-item>
						</el-descriptions>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="当前仓库容量阈值" label-width="auto">
							<el-input-number
								v-model="ruleform.threshold"
								type="number"
								placeholder="请输入仓库容量阈值"
								:min="1"
								:max="ruleform.maxCapacity"
								controls-position="right"
								clearable
							></el-input-number>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel" size="default">取 消</el-button>
					<el-button type="primary" @click="onSubmit" size="default">保 存</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, defineComponent } from 'vue';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { DepotQueryAPI, DepotUpdateAPI } from '/@/api/manager/material/depot';

interface State {
	isShowDialog: boolean;
	isAllow: boolean;
	ruleform: {
		depotId: string;
		depotName: string;
		depotNameZh: string;
		capacity: number;
		maxCapacity: number;
		threshold: number;
	};
}

export default defineComponent({
	name: 'SetDepotThreshold',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			isAllow: false,
			ruleform: {
				depotId: '',
				depotName: '',
				depotNameZh: '',
				capacity: 0,
				maxCapacity: 1,
				threshold: 1,
			},
		});

		// 打开弹窗
		function openDialog(depotId: any) {
			state.isShowDialog = true;
			DepotQueryAPI()
				.queryOne(`depotId=${depotId}`)
				.then((res) => {
					let element: any = JSON.stringify(res.data);
					element = JSON.parse(element);
					if (element) {
						state.ruleform.depotId = depotId;
						state.ruleform.depotName = element.depotName;
						state.ruleform.depotNameZh = element.depotNameZh;
						state.ruleform.capacity = element.capacity;
						state.ruleform.maxCapacity = element.maxCapacity;
						state.ruleform.threshold = element.threshold;
					}
				});
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleform = {
				depotId: '',
				depotName: '',
				depotNameZh: '',
				capacity: 0,
				maxCapacity: 1,
				threshold: 1,
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

		// 提交修改
		function onSubmit() {
			if (!state.ruleform.depotId || !state.ruleform.threshold) {
				ElMessage.error('请重新检查阈值参数');
				return;
			}
			if (state.ruleform.maxCapacity < state.ruleform.threshold) {
				ElMessage.error('阈值参数不能大于仓库最大容量');
				return;
			}
			DepotUpdateAPI()
				.setThreshold(`depotId=${state.ruleform.depotId}&threshold=${state.ruleform.threshold}`)
				.then((res) => {
					closeDialog();
					ElNotification({
						title: '通知',
						message: '仓库容量阈值修改成功',
						type: 'success',
					});
				});
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
