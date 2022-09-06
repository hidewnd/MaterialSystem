<template>
	<div class="depot-rack-transfer-container">
		<el-dialog title="货架转移" v-model="isShowDialog" width="769px">
			<el-form :model="ruleform" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="24" :xl="24">
						<el-descriptions :column="1">
							<el-descriptions-item label="当前仓库系统名">{{ ruleform.depotName }}</el-descriptions-item>
							<el-descriptions-item label="当前仓库中文名">{{ ruleform.depotNameZh }}</el-descriptions-item>
							<el-descriptions-item label="当前操作货架编号">{{ ruleform.rackId }}</el-descriptions-item>
						</el-descriptions>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="24" :xl="24" class="mb10">
						<el-form-item label="转移目标仓库" label-width="auto">
							<el-select
								v-model="ruleform.targetId"
								filterable
								remote
								reserve-keyword
								placeholder="请输入仓库名"
								:remote-method="searchDepotRemote"
								:loading="searchDepot.loading"
								@change="searchDepotChange"
								:autofocus="false"
								style="max-width: 180px"
							>
								<el-option v-for="item in searchDepot.depotOptions" :key="item.depotId" :label="item.depotName" :value="item.depotId" />
							</el-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel" size="default">取 消</el-button>
					<el-button type="primary" @click="onSubmit" size="default" :disabled="!isAllow">转 移</el-button>
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

interface DepotItem {
	depotId: string;
	depotName: string;
}

interface State {
	isShowDialog: boolean;
	isAllow: boolean;
	ruleform: {
		rackId: string;
		depotId: string;
		depotName: string;
		depotNameZh: string;
		targetId: string;
	};
	searchDepot: {
		depotOptions: Array<DepotItem>;
		loading: boolean;
	};
}

export default defineComponent({
	name: 'DepotRackTransfer',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			isAllow: false,
			ruleform: {
				depotName: '',
				depotNameZh: '',
				rackId: '',
				depotId: '',
				targetId: '',
			},
			searchDepot: {
				loading: false,
				depotOptions: [],
			},
		});

		// 打开弹窗
		function openDialog(row: any) {
			state.isShowDialog = true;
			if (row) {
				state.ruleform.depotId = row.depotId;
				state.ruleform.rackId = row.rackId;
				state.ruleform.depotId = row.depotId;
			}
			if (row.depotId) {
				DepotQueryAPI()
					.queryOne(`depotId=${row.depotId}`)
					.then((res) => {
						let element: any = JSON.stringify(res.data);
						element = JSON.parse(element);
						if (element) {
							state.ruleform.depotName = element.depotName;
							state.ruleform.depotNameZh = element.depotNameZh;
						}
					});
			}
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleform = {
				depotName: '',
				depotNameZh: '',
				rackId: '',
				depotId: '',
				targetId: '',
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
			if (!state.ruleform.targetId) {
				ElMessage.warning(t('message.resources.common.formWarning'));
				return;
			}
			if (state.ruleform.rackId != undefined && state.ruleform.depotId != undefined) {
				DepotUpdateAPI()
					.transferRack(`rackId=${state.ruleform.rackId}&oldId=${state.ruleform.depotId}&targetId=${state.ruleform.targetId}`)
					.then((res) => {
						closeDialog();
						ElNotification({
							title: '通知',
							message: '货架转移成功',
							type: 'success',
						});
					});
			}
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

		function searchDepotChange(depotId: string) {
			state.isAllow = true;
		}

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			searchDepotRemote,
			searchDepotChange,
			...toRefs(state),
		};
	},
});
</script>
