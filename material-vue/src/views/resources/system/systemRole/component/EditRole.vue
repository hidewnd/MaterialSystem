<template>
	<div class="system-edit-role-container">
		<el-dialog title="修改角色" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="角色编号">
							<el-input v-model="ruleForm.roleId" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="系统名称">
							<el-input v-model="ruleForm.roleName" placeholder="请输入系统名称"></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="中文名称">
							<el-input v-model="ruleForm.roleNameZh" placeholder="请输入中文名称" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="角色排序号">
							<el-input-number v-model="ruleForm.roleSort" placeholder="请输入排序号" :min="1" controls-position="right" type="number" clearable></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="用户状态">
							<el-switch
								v-model="ruleForm.stated"
								inline-prompt
								active-text="启"
								:active-value="0"
								inactive-text="禁"
								:inactive-value="1"
							></el-switch>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="备注">
							<el-input v-model="ruleForm.remark" type="textarea" placeholder="请输入备注" maxlength="150"></el-input>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel" size="default">取 消</el-button>
					<el-button type="primary" @click="onSubmit" size="default">修 改</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import qs from 'qs';
import { reactive, toRefs, onMounted, defineComponent } from 'vue';
import { ResourceSystemApi, SystemRoleApi } from '/@/api/resources/system';
import { SysRole } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { log } from 'console';
import { verifyCnAndSpace } from '/@/utils/toolsValidate';
interface RoleState {
	isShowDialog: boolean;
	ruleForm: SysRole;
}

export default defineComponent({
	name: 'systemEditRole',
	setup() {
		const { t } = useI18n();
		const state = reactive<RoleState>({
			isShowDialog: false,
			ruleForm: {
				roleId: '',
				roleName: '',
				roleNameZh: '',
				roleSort: 0,
				stated: 0,
				delFlag: 0,
				remark: '',
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				menus: [],
			},
		});
		// 打开弹窗
		const openDialog = (row: SysRole) => {
			state.ruleForm = row;
			state.isShowDialog = true;
		};
		// 关闭弹窗
		const closeDialog = () => {
			ResourceSystemApi()
				.queryOne({ name: 'role', type: 'id', arg1: state.ruleForm.roleId })
				.then((res) => {
					let data: any = JSON.stringify(res.data);
					data = JSON.parse(data);
					if (data) {
						state.ruleForm.updateDate = data.updateDate;
						state.ruleForm.updateBy = data.updateBy;
					}
				});
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
		// 保存
		const onSubmit = () => {
			state.ruleForm.roleName = verifyCnAndSpace(state.ruleForm.roleName);
			if (state.ruleForm.roleSort < 1) {
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
			SystemRoleApi()
				.setRole(getParam('update'))
				.then(() => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		};

		const getParam = (type: string) => {
			let param = 'type=' + type + '&roleId=' + state.ruleForm.roleId + '&stated=' + state.ruleForm.stated;
			if (state.ruleForm.roleName != null && state.ruleForm.roleName != '' && state.ruleForm.roleName != undefined) {
				param += '&roleName=' + state.ruleForm.roleName;
			}
			if (state.ruleForm.roleNameZh != null && state.ruleForm.roleNameZh != '' && state.ruleForm.roleNameZh != undefined) {
				param += '&roleNameZh=' + state.ruleForm.roleNameZh;
			}
			if (state.ruleForm.remark != null && state.ruleForm.remark != '' && state.ruleForm.remark != undefined) {
				param += '&remark=' + state.ruleForm.remark;
			}
			if (state.ruleForm.roleSort != null && state.ruleForm.roleSort > 0 && state.ruleForm.roleSort != undefined) {
				param += '&roleSort=' + state.ruleForm.roleSort;
			}
			return param;
		};
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
