<template>
	<div class="resources-edit-material-container">
		<el-dialog title="修改物料" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料编号">
							<el-input v-model="ruleForm.materialId" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料系统名">
							<el-input v-model="ruleForm.materialName" placeholder="请输入物料系统名" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料中文名">
							<el-input v-model="ruleForm.materialNameZh" placeholder="请输入物料中文名" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型编号" label-width="auto">
							<el-input v-model="ruleForm.typeId" placeholder="请输入物料类型编号" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型系统名" label-width="auto">
							<el-autocomplete
								v-model="type.typeName"
								:fetch-suggestions="querySearchAsync"
								placeholder="请输入物料类型系统名"
								@select="handleSelect"
								clearable
								autofocus="false"
							></el-autocomplete>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型中文名" label-width="auto">
							<el-input v-model="type.typeNameZh" placeholder="请输入物料类型中文名" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料价值">
							<el-input-number
								v-model="ruleForm.value"
								type="number"
								placeholder="请输入物料价值"
								:min="0.0"
								:precision="2"
								:step="0.1"
								controls-position="right"
								clearable
								autofocus="false"
							></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料容量占比" label-width="auto">
							<el-input-number
								v-model="ruleForm.capacityRatio"
								type="number"
								placeholder="请输入物料容量占比"
								:min="1"
								controls-position="right"
								clearable
							></el-input-number>
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
						<el-form-item label="物料描述">
							<el-input v-model="ruleForm.message" type="textarea" placeholder="请输入物料描述" maxlength="150" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="备注">
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
import { Material, MaterialType } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { MaterialQueryAPI, MaterialUpdateAPI } from '/@/api/manager/material/material';
import { verifyAndSpace, verifiyNumberInteger } from '/@/utils/toolsValidate';

interface UserState {
	isShowDialog: boolean;
	ruleForm: Material;
	type: MaterialType;
}

interface LinkItem {
	value: string;
	type: MaterialType;
}

export default defineComponent({
	name: 'ResourcesEditMaterial',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			ruleForm: {
				materialId: '',
				typeId: '',
				materialName: '',
				materialNameZh: '',
				value: 0.0,
				capacityRatio: 1,
				message: '',
				status: 1,
				delFlag: 0,
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				remark: '',
			},
			type: {
				typeId: '',
				parentId: '1',
				typeName: '',
				typeNameZh: '',
				childList: undefined,
				status: 0,
				delFlag: 0,
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				remark: '',
			},
		});
		// 打开弹窗
		function openDialog(row: Material) {
			state.ruleForm = row;
			state.isShowDialog = true;
			initMaterialType(state.ruleForm.typeId);
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
		function onSubmit() {
			if (!checkRuleForm()) {
				ElMessage.warning(t('message.resources.common.formWarning'));
				return;
			}
			MaterialUpdateAPI()
				.updateMaterial(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		// 数据检查
		function checkRuleForm(): boolean {
			state.ruleForm.materialName = verifyAndSpace(state.ruleForm.materialName);
			state.ruleForm.materialNameZh = verifyAndSpace(state.ruleForm.materialNameZh);
			state.ruleForm.typeId = verifiyNumberInteger(state.ruleForm.typeId);
			if (state.ruleForm.materialName && state.ruleForm.materialNameZh && state.ruleForm.typeId) {
				return true;
			}
			return false;
		}

		function initMaterialType(typeId: string) {
			MaterialQueryAPI()
				.queryOne(`id=${typeId}&type=type`)
				.then((res) => {
					let element: any = JSON.stringify(res.data);
					element = JSON.parse(element);
					if (element) {
						state.type.typeId = element.typeId;
						state.type.parentId = element.parentId;
						state.type.typeName = element.typeName;
						state.type.typeNameZh = element.typeNameZh;
						state.type.status = element.status;
						state.type.delFlag = element.delFlag;
						state.type.remark = element.remark;
						state.type.createBy = element.createBy;
						state.type.updateBy = element.updateBy;
					}
				});
		}

		function querySearchAsync(queryString: string, cb: (arg: any) => void) {
			if (queryString && verifyAndSpace(queryString)) {
				MaterialQueryAPI()
					.queryList(`type=matType&way=like&column=type_name&arg1=${queryString}`)
					.then((res) => {
						let elements: any = JSON.stringify(res.data);
						let result: Array<LinkItem> = [];
						elements = JSON.parse(elements);
						if (elements.length) {
							for (let i = 0; i < elements.length; i++) {
								let element: any = elements[i];
								result.push({ value: element.typeName, type: element });
							}
						}
						cb(result);
					});
			} else {
				MaterialQueryAPI()
					.queryPage(`type=type`)
					.then((res) => {
						let elements: any = JSON.stringify(res.data.element);
						let result: Array<LinkItem> = [];
						elements = JSON.parse(elements);
						if (elements.length) {
							for (let i = 0; i < elements.length; i++) {
								let element: any = elements[i];
								result.push({ value: element.typeName, type: element });
							}
						}
						cb(result);
					});
			}
		}

		function handleSelect(item: LinkItem) {
			if (item && item.type) {
				state.type = item.type;
				state.ruleForm.typeId = item.type.typeId;
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
