<template>
	<div class="system-add-role-container">
		<el-dialog title="新增角色" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" ref="formRef" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item
							label="角色系统名"
							label-width="auto"
							:prop="ruleForm.roleName"
							:rules="[
								{ required: true, message: '请输入角色名称' },
								{ min: 3, max: 30, message: '角色名称最小长度应大于3' },
							]"
						>
							<el-input v-model="ruleForm.roleName" placeholder="请输入账户名称" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item
							label="角色中文名"
							label-width="auto"
							:prop="ruleForm.roleNameZh"
							:rules="[{ required: false, message: '请输入角色中文名' }]"
						>
							<el-input v-model="ruleForm.roleNameZh" placeholder="请输入" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="角色排序号" label-width="auto" :rules="[{ required: true, message: '请输入角色名称' }]">
							<el-input-number v-model.number="ruleForm.roleSort" type="number" :min="1" controls-position="right" placeholder="请输入排序号" clearable></el-input-number>
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
import { reactive, toRefs, onMounted, defineComponent, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { SystemRoleApi } from '/@/api/resources/system';
import { useI18n } from 'vue-i18n';
import { verifyAndSpace, verifyCnAndSpace } from '/@/utils/toolsValidate';

interface RoleState {
	isShowDialog: boolean;
	ruleForm: {
		roleName: string;
		roleNameZh: string;
		roleSort: number;
	};
}

export default defineComponent({
	name: 'systemAddRole',
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const state = reactive<RoleState>({
			isShowDialog: false,
			ruleForm: {
				roleName: '',
				roleNameZh: '',
				roleSort: 12,
			},
		});

		// 打开弹窗
		const openDialog = () => {
			state.isShowDialog = true;
		};
		// 关闭弹窗
		const closeDialog = () => {
			state.ruleForm = {
				roleName: '',
				roleNameZh: '',
				roleSort: 12,
			};
			state.isShowDialog = false;
		};
		// 取消
		const onCancel = () => {
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
		};
		// 提交
		const onSubmit = () => {
			proxy.$refs.formRef.validate((valid: boolean) => {
				state.ruleForm.roleName = verifyCnAndSpace(state.ruleForm.roleName);
				if (state.ruleForm.roleSort < 0) {
					ElMessage.warning('角色排序号不能小于0');
					return;
				}
				if (state.ruleForm.roleName == '') {
					ElMessage.warning('角色系统名称不能为空或者中文');
					return;
				}
				if (!/[^\d.]/g.test(state.ruleForm.roleName)) {
					ElMessage.warning('角色系统名称不能含有数字');
					return;
				}
				state.ruleForm.roleNameZh = (state.ruleForm.roleNameZh === '' || state.ruleForm.roleNameZh === null) ? state.ruleForm.roleName : state.ruleForm.roleNameZh;
				SystemRoleApi()
					.insertRole(`roleName=${state.ruleForm.roleName}&nameZh=${state.ruleForm.roleNameZh}&roleSort=${state.ruleForm.roleSort}`)
					.then((res) => {
						ElMessage.success(t('message.resources.table.saveSuccess'));
						closeDialog();
					});
			});
		};
		// 页面加载时
		onMounted(() => {});
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
