<template>
	<div class="resources-add-material-type-container">
		<el-dialog title="新增物料类型" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="物料类型系统名" label-width="auto">
							<el-input
								v-model="ruleForm.typeName"
								@input="inputChange"
								placeholder="请输入物料系统名"
								maxlength="20"
								show-word-limit
								clearable
							></el-input>
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
import { verifyCnAndSpace, verifiyNumberInteger, verifyAndSpace } from '/@/utils/toolsValidate';
import { MaterialQueryAPI, MaterialUpdateAPI } from '/@/api/manager/material/material';
import { MaterialType } from '/@/store/interface';

interface State {
	isShowDialog: boolean;
	ruleForm: {
		typeName: string;
		typeNameZh: string;
		parentId: string;
		remark: string;
	};
	type: MaterialType;
}
interface TypeItem {
	value: string;
	type: MaterialType;
}
export default defineComponent({
	name: 'ResourcesAddMaterialType',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				typeName: '',
				typeNameZh: '',
				parentId: '1',
				remark: '',
			},
			type: {
				typeId: '1',
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
			initType();
			state.isShowDialog = true;
		}

		function initType() {
			MaterialQueryAPI()
				.queryOne(`type=type&id=1`)
				.then((res) => {
					let element: any = JSON.stringify(res.data);
					element = JSON.parse(element);
					if (element) {
						state.type = {
							typeId: element.typeId,
							parentId: element.parentId,
							typeName: element.typeName,
							typeNameZh: element.typeNameZh,
							childList: undefined,
							status: element.status,
							delFlag: element.delFlag,
							createBy: element.createBy,
							createDate: element.createDate,
							updateBy: element.updateBy,
							updateDate: element.updateDate,
							remark: element.remark,
						};
						state.ruleForm.parentId = element.typeId;
					}
				});
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				typeName: '',
				typeNameZh: '',
				parentId: '',
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
				.insertType(state.ruleForm)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					setTimeout(() => {
						closeDialog();
					}, 700);
				});
		}

		// 数据检查
		function checkRuleForm(): boolean {
			state.ruleForm.typeName = verifyAndSpace(state.ruleForm.typeName);
			state.ruleForm.typeNameZh = verifyAndSpace(state.ruleForm.typeNameZh);
			state.ruleForm.parentId = verifiyNumberInteger(state.ruleForm.parentId);
			console.log(state.ruleForm);
			if (state.ruleForm.typeName && state.ruleForm.typeNameZh && state.ruleForm.parentId) {
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
				state.ruleForm.parentId = item.type.typeId;
			}
		}

		function inputChange(val: string) {
			if (!state.ruleForm.typeNameZh) {
				state.ruleForm.typeNameZh = val;
			}
		}

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			querySearchAsync,
			handleSelect,
			inputChange,
			...toRefs(state),
		};
	},
});
</script>
