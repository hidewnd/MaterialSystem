<template>
	<div class="resources-add-user-container">
		<el-dialog title="新增物料" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料系统名" label-width="auto">
							<el-input v-model="ruleForm.materialName" @input="materialNameInput($event)" placeholder="请输入物料系统名" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料中文名" label-width="auto">
							<el-input v-model="ruleForm.materialNameZh" placeholder="请输入物料中文名" clearable></el-input>
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
						<el-form-item label="物料价值" label-width="auto">
							<el-input-number
								v-model="ruleForm.value"
								type="number"
								placeholder="请输入物料价值"
								:min="0.0"
								:precision="2"
								:step="0.1"
								controls-position="right"
								clearable
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
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="物料描述" label-width="auto">
							<el-input v-model="ruleForm.message" type="textarea" placeholder="请输入物料描述" maxlength="150" clearable></el-input>
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
import { verifiyNumberInteger, verifyAndSpace } from '/@/utils/toolsValidate';
import { MaterialQueryAPI, MaterialUpdateAPI } from '/@/api/manager/material/material';
import { MaterialType } from '/@/store/interface';

interface State {
	isShowDialog: boolean;
	ruleForm: {
		materialName: string;
		typeId: string;
		materialNameZh: string;
		value: number;
		capacityRatio: number;
		message: string;
		remark: string;
	};
	type: MaterialType;
}

interface TypeItem {
	value: string;
	type: MaterialType;
}

export default defineComponent({
	name: 'ResourcesAddMaterial',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				materialName: '',
				typeId: '',
				materialNameZh: '',
				value: 0.0,
				capacityRatio: 1,
				message: '',
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
		function openDialog() {
			state.isShowDialog = true;
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				materialName: '',
				typeId: '',
				materialNameZh: '',
				value: 0.0,
				capacityRatio: 1,
				message: '',
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
			MaterialUpdateAPI()
				.insertMaterial(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					setTimeout(() => {
						closeDialog();
					}, 700);
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

		function querySearchAsync(queryString: string, cb: (arg: any) => void) {
			if (queryString && verifyAndSpace(queryString)) {
				MaterialQueryAPI()
					.queryList(`type=matType&way=like&column=type_name&arg1=${queryString}`)
					.then((res) => {
						let elements: any = JSON.stringify(res.data);
						let result: Array<TypeItem> = [];
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
						let result: Array<TypeItem> = [];
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

		function handleSelect(item: TypeItem) {
			if (item && item.type) {
				state.type = item.type;
				state.ruleForm.typeId = item.type.typeId;
			}
		}

		function materialNameInput(val: string) {
			if (!state.ruleForm.materialNameZh) {
				state.ruleForm.materialNameZh = val;
			}
		}

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			querySearchAsync,
			handleSelect,
			materialNameInput,
			...toRefs(state),
		};
	},
});
</script>
