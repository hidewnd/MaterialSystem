<template>
	<div class="authority-restart-passpwrd">
		<el-card shadow="alway" :header="$t('message.router.restartPassword')">
			<el-row :gutter="24">
				<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-form
						ref="formRef"
						:model="ruleForm"
						:rules="rules"
						status-icon
						class="wrap-form"
						label-position="top"
						size="default"
						:inline-message="true"
						label-width="90px"
					>
						<el-form-item label="用户名" label-width="auto">
							<el-select
								v-model="ruleForm.accountId"
								filterable
								remote
								reserve-keyword
								clearable
								placeholder="请输入用户名查询"
								:remote-method="AccountRemote"
								@change="AccountChange"
								:loading="loading"
								autofocus="false"
								style="max-width: 180px"
							>
								<el-option v-for="item in accountOptions" :key="item.accountId" :label="item.username" :value="item.accountId" />
							</el-select>
						</el-form-item>
						<el-form-item label="新密码" prop="newPassword">
							<div class="enter-form-msg">请输入重置后的账户密码，若为空则默认重置密码为：123456</div>
							<el-input
								v-model="ruleForm.newPassword"
								type="password"
								:maxlength="30"
								:minlength="6"
								placeholder="Please input password"
								show-password
							/>
						</el-form-item>
						<el-form-item label="确认密码" prop="checkPassword">
							<div class="enter-form-msg">请确认重置的账户密码</div>
							<el-input
								v-model="ruleForm.checkPassword"
								type="password"
								:maxlength="30"
								:minlength="6"
								placeholder="Please input password"
								show-password
							/>
						</el-form-item>
						<el-form-item class="md20">
							<el-button @click="onRestart">重置表单</el-button>
							<el-button type="primary" @click="onSubmit" :disabled="!isAllowSub">提交修改</el-button>
						</el-form-item>
					</el-form>
				</el-col>
			</el-row>
			<el-row>
				<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20"> </el-col>
			</el-row>
		</el-card>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, getCurrentInstance, markRaw, onMounted, watch } from 'vue';
import { ResourceSystemApi, SystemAccountApi } from '/@/api/resources/system';
import { verifyAndSpace, verifyPassword } from '/@/utils/toolsValidate';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import { useStore } from '/@/store/index';
import { useI18n } from 'vue-i18n';
import { LoginApi } from '/@/api/login';
import { Session } from '/@/utils/storage';
import { resetRoute } from '/@/router';
import { MaterialQueryAPI } from '/@/api/manager/material/material';
interface AccountOption {
	accountId: string;
	username: string;
}
interface RestartState {
	ruleForm: {
		accountId: string;
		username: string;
		newPassword: string;
		checkPassword: string;
	};
	rules: object;
	accountOptions: Array<AccountOption>;
	loading: boolean;
	isAllowSub: boolean;
}

export default defineComponent({
	name: 'AuthorityRestartPasswprd',
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const store = useStore();
		const state = reactive<RestartState>({
			ruleForm: {
				accountId: '',
				username: '',
				newPassword: '',
				checkPassword: '',
			},
			rules: {
				newPassword: [
					{ validator: checkPassword, triggerr: 'blur' },
					{ validator: checkPassword, triggerr: 'change' },
				],
				checkPassword: [
					{ validator: checkConfirm, triggerr: 'blur' },
					{ validator: checkConfirm, triggerr: 'change' },
				],
			},
			accountOptions: [],
			loading: false,
			isAllowSub: false,
		});

		// 检查重置密码
		function checkPassword(rule: any, value: any, callback: any) {
			if (value) {
				if (verifyAndSpace(value) === '') {
					callback(new Error('重置密码不能有空格'));
				} else if (!verifyPassword(value)) {
					callback(new Error('重置密码以字母开头，长度在6~16之间，只能包含字母、数字和下划线'));
				} else {
					callback();
				}
			}
		}

		// 检查确认密码
		function checkConfirm(rule: any, value: any, callback: any) {
			if (state.ruleForm.newPassword && state.ruleForm.checkPassword === '') {
				callback('请再次输入密码确认');
			} else if (state.ruleForm.newPassword != state.ruleForm.checkPassword) {
				callback('两次输入的密码不一致！请重新检查');
			} else {
				callback();
			}
		}

		// select输入搜索
		function AccountRemote(qury: any) {
			if (qury) {
				state.loading = true;
				ResourceSystemApi()
					.queryList(`account/like/username/${qury}`)
					.then((res) => {
						let list = JSON.stringify(res.data);
						list = JSON.parse(list);
						if (list) {
							const array: Array<AccountOption> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (element) {
									array.push({ accountId: element.accountId, username: element.username });
								}
							}
							state.accountOptions = array;
						}
						state.loading = false;
					})
					.catch((res) => {
						state.loading = false;
					});
			} else {
			}
		}

		// select选中监听
		function AccountChange(accountId: any) {
			if (accountId) {
				state.isAllowSub = true;
			}
		}

		// 重置表单
		function onRestart() {
			proxy.$refs.formRef.resetFields();
		}

		// 表单提交
		function onSubmit() {
			if (state.ruleForm.accountId) {
				if (!state.ruleForm.newPassword) {
					state.ruleForm.newPassword = 'a123456';
					state.ruleForm.checkPassword = state.ruleForm.newPassword;
				}
				proxy.$refs.formRef.validate((valid: boolean) => {
					if (valid) {
						ElMessageBox.confirm(`是否确定重置用户的密码？`, '提示', {
							confirmButtonText: t('message.resources.common.confirmButtonText'),
							cancelButtonText: t('message.resources.common.cancelButtonText'),
							type: 'warning',
						})
							.then(() => {
								setTimeout(() => {
									SystemAccountApi()
										.resortPassword(`accountId=${state.ruleForm.accountId}&newPwd=${state.ruleForm.newPassword}`)
										.then((res) => {
											ElNotification({
												title: '通知',
												message: '重置系统用户密码成功',
												type: 'success',
											});
											setTimeout(() => {
												if (state.ruleForm.accountId === <any>store.state.userInfos.userInfos.userId) {
													ElMessage.warning('请重新登录');
													LoginApi()
														.logout({})
														.then(async (res) => {
															Session.clear(); // 清除缓存/token等
															await resetRoute(); // 删除/重置路由
															window.location.href = ''; // 去登录页
														})
														.catch(() => {});
												}
											}, 700);
										});
								}, 300);
							})
							.catch(() => {});
					}
				});
			}
		}

		watch(
			() => state.isAllowSub,
			() => {
				if (state.ruleForm.accountId) {
					for (let i = 0; i < state.accountOptions.length; i++) {
						const element = state.accountOptions[i];
						if (element.accountId === state.ruleForm.accountId) {
							state.ruleForm.username == element.username;
							return;
						}
					}
				}
			},
			{ deep: true, immediate: true }
		);

		// 初始化
		onMounted(() => {});

		return {
			AccountRemote,
			AccountChange,
			onRestart,
			onSubmit,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.authority-restart-passpwrd {
	.wrap-form {
		::v-deep(.el-form-item--small.el-form-item) {
			margin-bottom: 0 !important;
		}
		.enter-form-msg {
			color: #be6363ce;
			font-size: 12px;
			width: 100%;
			.enter-form-msg-red {
				color: red;
			}
		}
		.enter-form-msg + div {
			width: 20%;
		}
	}
}
</style>
