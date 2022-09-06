<template>
	<div class="resources-edit-rack-container">
		<el-dialog title="修改货架" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="货架编号" label-width="auto">
							<el-input v-model="ruleForm.rackId" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库系统名" label-width="auto">
							<el-autocomplete
								v-model="depot.depotName"
								:fetch-suggestions="querySearchAsync"
								placeholder="请输入仓库系统名"
								@select="handleSelect"
								clearable
								autofocus="false"
							></el-autocomplete>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="货架最大容量" label-width="auto">
							<el-input-number
								v-model="ruleForm.maxCapacity"
								:min="1"
								controls-position="right"
								placeholder="请输入货架最大容量"
								clearable
								autofocus="false"
							></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="仓库中文名" label-width="auto">
							<el-input v-model="depot.depotNameZh" placeholder="仓库中文名" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="启用状态" label-width="auto">
							<el-switch
								v-model="ruleForm.status"
								inline-prompt
								active-text="启"
								:active-value="0"
								inactive-text="禁"
								:inactive-value="1"
								autofocus="false"
							></el-switch>
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
					<el-button @click="onCancel" size="default">关 闭</el-button>
					<el-button type="primary" @click="onSubmit" size="default">修 改</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, onMounted, defineComponent } from 'vue';
import { MaterialDepot, MaterialRack } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { RackUpdateAPI } from '/@/api/manager/material/rack';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
import { verifyAndSpace } from '/@/utils/toolsValidate';
import { DepotQueryAPI } from '/@/api/manager/material/depot';

interface State {
	isShowDialog: boolean;
	ruleForm: MaterialRack;
	depot: MaterialDepot;
}

interface DepotItem {
	value: string;
	depot: MaterialDepot;
}

export default defineComponent({
	name: 'ResourcesEditRack',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				rackId: '',
				depotId: '',
				maxCapacity: 0,
				description: '',
				status: 1,
				delFlag: 0,
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				remark: '',
				materialList: [],
			},
			depot: {
				depotId: '',
				depotName: '',
				depotNameZh: '',
				place: '',
				description: '',
				maxCapacity: 1,
				capacity: 0,
				threshold: 0,
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
		});

		// 打开弹窗
		function openDialog(row: MaterialRack) {
			state.ruleForm = row;
			state.isShowDialog = true;
			initMaterialDepot(state.ruleForm.depotId);
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
			if (!checkRuleForm()) {
				ElMessage.warning(t('message.resources.common.formWarning'));
				return;
			}
			RackUpdateAPI()
				.update(state.ruleForm)
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

		// 初始化
		function initMaterialDepot(typeId: string) {
			DepotQueryAPI()
				.queryOne(`depotId=${typeId}`)
				.then((res) => {
					let element: any = JSON.stringify(res.data);
					element = JSON.parse(element);
					if (element) {
						state.depot.depotId = element.depotId;
						state.depot.depotName = element.depotName;
						state.depot.depotNameZh = element.depotNameZh;
						state.depot.place = element.place;
						state.depot.description = element.description;
						state.depot.maxCapacity = element.maxCapacity;
						state.depot.threshold = element.threshold;
						state.depot.status = element.status;
						state.depot.delFlag = element.delFlag;
						state.depot.remark = element.remark;
						state.depot.createBy = element.createBy;
						state.depot.updateBy = element.updateBy;
					}
				});
		}

		// 输入查询
		function querySearchAsync(queryString: string, cb: (arg: any) => void) {
			if (queryString && verifyAndSpace(queryString)) {
				MaterialQueryAPI()
					.queryList(`type=depot&way=like&column=depot_name&arg1=${queryString}`)
					.then((res) => {
						let elements: any = JSON.stringify(res.data);
						let result: Array<DepotItem> = [];
						elements = JSON.parse(elements);
						if (elements.length) {
							for (let i = 0; i < elements.length; i++) {
								let element: any = elements[i];
								result.push({ value: element.depotName, depot: element });
							}
						}
						cb(result);
					});
			} else {
				DepotQueryAPI()
					.queryList(`size=10`)
					.then((res) => {
						let elements: any = JSON.stringify(res.data.element);
						let result: Array<DepotItem> = [];
						elements = JSON.parse(elements);
						if (elements.length) {
							for (let i = 0; i < elements.length; i++) {
								let element: any = elements[i];
								result.push({ value: element.depotName, depot: element });
							}
						}
						cb(result);
					});
			}
		}

		// 选中事件
		function handleSelect(item: DepotItem) {
			if (item && item.depot) {
				state.depot = item.depot;
				state.ruleForm.depotId = item.depot.depotId;
			}
		}

		// 页面加载时
		onMounted(() => {});

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			querySearchAsync,
			handleSelect,
			...toRefs(state),
		};
	},
});
</script>
