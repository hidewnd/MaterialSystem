<template>
	<div class="system-edit-user-container">
		<el-dialog title="修改用户" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="账户编号">
							<el-input v-model="ruleForm.accountId" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="账户名称">
							<el-input v-model="ruleForm.username" placeholder="请输入账户名称" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="账户密码">
							<el-input v-model="ruleForm.password" placeholder="请输入用户密码" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="手机号">
							<el-input v-model="accountInfo.phone" placeholder="请输入手机号" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="邮箱">
							<el-input v-model="accountInfo.email" placeholder="请输入" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="性别">
							<el-select v-model="accountInfo.sex" placeholder="请选择" clearable class="w100">
								<el-option label="男" value="0"></el-option>
								<el-option label="女" value="1"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="年龄">
							<el-input v-model="accountInfo.age" placeholder="请输入" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="用户状态">
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
						<el-form-item label="用户签名">
							<el-input v-model="accountInfo.sign" type="textarea" placeholder="请输入用户签名" maxlength="150"></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="账户备注">
							<el-input v-model="ruleForm.remark" type="textarea" placeholder="请输入账户备注" maxlength="150"></el-input>
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
import { ResourceSystemApi, SystemAccountApi } from '/@/api/resources/system';
import { SysAccount, SysAccountInfo, SysRole } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';
import { log } from 'console';

interface UserState {
	isShowDialog: boolean;
	ruleForm: SysAccount;
	accountInfo: SysAccountInfo;
	roleData: Array<SysRole>;
}

export default defineComponent({
	name: 'systemEditUser',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			roleData: [],
			ruleForm: {
				accountId: '',
				username: '',
				password: '',
				status: 0,
				delFlag: 0,
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				remark: '',
				roles: [],
			},
			accountInfo: {
				accountId: '',
				sex: '',
				age: 0,
				phone: '',
				email: '',
				avatar: '',
				sign: '',
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				remark: '',
			},
		});
		// 打开弹窗
		const openDialog = (row: SysAccount) => {
			state.ruleForm = row;
			state.isShowDialog = true;
			initTableData();
		};
		// 关闭弹窗
		const closeDialog = () => {
			state.isShowDialog = false;
			setTimeout(() => {
				context.emit('refresh');
			}, 1500);
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
			if (state.ruleForm.password.length < 6) {
				ElMessage.warning('密码长度应大于6!');
			}
			SystemAccountApi()
				.updateAccount(getAccountParam('update'))
				.then(() => {
					SystemAccountApi()
						.updateAccountInfo(getInfoParam('update'))
						.then((res) => {
							ElMessage.success(t('message.resources.table.saveSuccess'));
							closeDialog();
						});
				});
		};
		// 初始化部门数据
		const initTableData = () => {
			ResourceSystemApi()
				.queryOne({ name: 'info', type: 'id', arg1: state.ruleForm.accountId })
				.then((res) => {
					let info: any = JSON.stringify(res.data);
					info = JSON.parse(info);
					if (info) {
						state.accountInfo.accountId = info.accountId;
						state.accountInfo.sex = info.sex == 0 ? '男' : '女';
						state.accountInfo.age = info.age;
						state.accountInfo.phone = info.phone;
						state.accountInfo.email = info.email;
						state.accountInfo.avatar = info.avatar;
						state.accountInfo.sign = info.sign;
						state.accountInfo.createBy = info.createBy;
						state.accountInfo.createDate = info.createDate;
						state.accountInfo.updateBy = info.updateBy;
						state.accountInfo.updateDate = info.updateDate;
						state.accountInfo.remark = info.remark;
					}
				});
		};

		// 页面加载时
		onMounted(() => {});

		const getInfoParam = (type: string) => {
			let param = 'type=' + type + '&accountId=' + state.ruleForm.accountId;
			if (state.accountInfo.sex != null && state.accountInfo.sex != '' && state.accountInfo.sex != undefined) {
				if (state.accountInfo.sex === '男') param += '&sex=0';
				else param += '&sex=1';
			}
			if (state.accountInfo.age != null && state.accountInfo.age != 0 && state.accountInfo.age != undefined) {
				param += '&age=' + state.accountInfo.age;
			}
			if (state.accountInfo.phone != null && state.accountInfo.phone != '' && state.accountInfo.phone != undefined) {
				param += '&phone=' + state.accountInfo.phone;
			}
			if (state.accountInfo.email != null && state.accountInfo.email != '' && state.accountInfo.email != undefined) {
				param += '&email=' + state.accountInfo.email;
			}
			if (state.accountInfo.sign != null && state.accountInfo.sign != '' && state.accountInfo.sign != undefined) {
				param += '&sign=' + state.accountInfo.sign;
			}
			return param;
		};

		const getAccountParam = (type: string) => {
			let param = 'type=' + type + '&accountId=' + state.ruleForm.accountId + '&status=' + state.ruleForm.status;
			if (state.ruleForm.password != null && state.ruleForm.password != '' && state.ruleForm.password != undefined) {
				param += '&password=' + state.ruleForm.password;
			}
			if (state.ruleForm.remark != null && state.ruleForm.remark != '' && state.ruleForm.remark != undefined) {
				param += '&remark=' + state.ruleForm.remark;
			}
			return param;
		};
		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			getInfoParam,
			...toRefs(state),
		};
	},
});
</script>
