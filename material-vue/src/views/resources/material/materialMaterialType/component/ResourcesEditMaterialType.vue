<template>
	<div class="resources-edit-material-type-container">
		<el-dialog title="修改用户" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型编号" label-width="auto">
							<el-input v-model="ruleForm.typeId" show-word-limit disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型系统名" label-width="auto">
							<el-input v-model="ruleForm.typeName" placeholder="请输入物料类型系统名" maxlength="20" show-word-limit clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型中文名" label-width="auto">
							<el-input v-model="ruleForm.typeNameZh" placeholder="请输入物料类型中文名" maxlength="20" show-word-limit clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="父级物料类型系统名" label-width="auto">
							<el-autocomplete
								v-model="type.typeName"
								:fetch-suggestions="querySearchAsync"
								placeholder="请输入父级物料类型系统名"
								@select="handleSelect"
								clearable
								autofocus="false"
							></el-autocomplete>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="父级物料类型编号" label-width="auto">
							<el-input v-model="type.typeId" placeholder="父级物料类型系统名" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="父级物料类型中文名" label-width="auto">
							<el-input v-model="type.typeNameZh" placeholder="父级物料类型中文名" disabled></el-input>
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
							></el-switch>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="备注" label-width="auto">
							<el-input v-model="ruleForm.remark" type="textarea" placeholder="请输入备注" show-word-limit maxlength="150" clearable></el-input>
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
import { MaterialType } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { MaterialQueryAPI, MaterialUpdateAPI } from '/@/api/manager/material/material';
import { verifyAndSpace, verifiyNumberInteger } from '/@/utils/toolsValidate';

interface UserState {
	isShowDialog: boolean;
	ruleForm: MaterialType;
	type: MaterialType;
}

interface LinkItem {
	value: string;
	type: MaterialType;
}

export default defineComponent({
	name: 'ResourcesEditMaterialType',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			ruleForm: {
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
		function openDialog(row: MaterialType) {
			state.ruleForm = row;
			state.isShowDialog = true;
			initMaterialType(state.ruleForm.parentId);
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

		// 提交修改
		function onSubmit() {
			if (!checkRuleForm()) {
				ElMessage.warning(t('message.resources.common.formWarning'));
				return;
			}
			MaterialUpdateAPI()
				.updateMaterialType(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		// 数据检查
		function checkRuleForm(): boolean {
			state.ruleForm.typeName = verifyAndSpace(state.ruleForm.typeName);
			state.ruleForm.typeNameZh = verifyAndSpace(state.ruleForm.typeNameZh);
			state.ruleForm.parentId = verifiyNumberInteger(state.ruleForm.parentId);
			if (state.ruleForm.typeName && state.ruleForm.typeNameZh && state.ruleForm.parentId) {
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
				state.ruleForm.parentId = item.type.typeId;
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
