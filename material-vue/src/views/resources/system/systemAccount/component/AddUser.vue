<template>
	<div class="system-add-user-container">
		<el-dialog title="新增用户" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" :rules="rules" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="账户名称" prop="userName">
							<el-input v-model="ruleForm.userName" placeholder="请输入账户名称" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="账户密码" prop="password">
							<el-input v-model="ruleForm.password" placeholder="请输入" type="password" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="手机号" prop="phone">
							<el-input v-model="ruleForm.phone" placeholder="请输入手机号" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="邮箱">
							<el-input v-model="ruleForm.email" placeholder="请输入电子邮箱" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="年龄">
							<el-input-number v-model="ruleForm.age" placeholder="请输入年龄" :min="10" controls-position="right" clearable></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="性别">
							<el-select v-model="ruleForm.sex" placeholder="请选择" clearable class="w100">
								<el-option label="男" value="0"></el-option>
								<el-option label="女" value="1"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
						<el-form-item label="用户描述">
							<el-input v-model="ruleForm.sign" type="textarea" placeholder="请输入用户描述" maxlength="150"></el-input>
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
import { reactive, toRefs, onMounted, defineComponent } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { SystemAccountApi } from '/@/api/resources/system';
import { useI18n } from 'vue-i18n';

interface UserState {
	isShowDialog: boolean;
	ruleForm: {
		accountId: string;
		userName: string;
		password: string;
		remark: string;
	};
	accountInfo: {
		sex: string;
		age: number;
		phone: string;
		email: string;
		sign: string;
	};
	rules: object;
}

export default defineComponent({
	name: 'systemAddUser',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			ruleForm: {
				accountId: '',
				userName: '', // 账户名称
				password: '', // 账户密码
				remark: '', //  备注
			},
			accountInfo: {
				sex: '0',
				age: 0,
				phone: '',
				email: '',
				sign: '',
			},
			rules: {
				userName: [
					{ required: true, message: '请输入登录账户', trigger: 'blur' },
					{ required: true, message: '请输入登录账户', trigger: 'change' },
				],
				password: [
					{ required: true, message: '请输入账户密码', trigger: 'blur' },
					{ required: true, message: '请输入账户密码', trigger: 'change' },
				],
				phone: [
					{ required: true, message: '请输入手机号', trigger: 'blur' },
					{ required: true, type: 'number', message: '请输入手机号', trigger: 'change' },
				],
			},
		});
		// 打开弹窗
		const openDialog = () => {
			state.isShowDialog = true;
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
		// 新增
		const onSubmit = () => {
			SystemAccountApi()
				.register('username=' + state.ruleForm.userName + '&password=' + state.ruleForm.password)
				.then((res) => {
					let accountId = JSON.stringify(res.data);
					state.ruleForm.accountId = JSON.parse(accountId);
					SystemAccountApi()
						.updateAccountInfo(getInfoParam('update'))
						.then((res) => {
							let params = 'type=update&accountId=' + state.ruleForm.accountId;
							if (state.ruleForm.remark != null && state.ruleForm.remark != '' && state.ruleForm.remark != undefined) {
								params += '&remark=' + state.ruleForm.remark;
							}
							SystemAccountApi()
								.updateAccount(params)
								.then((res) => {
									ElMessage.success(t('message.resources.table.saveSuccess'));
									closeDialog();
								});
						});
				});
		};
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
