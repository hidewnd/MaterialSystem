<template>
	<div class="personal">
		<el-row>
			<!-- 个人信息 -->
			<el-col :xs="24" :sm="24">
				<el-card shadow="hover" header="个人信息" class="md20">
					<div class="personal-user md20">
						<div class="personal-user-left">
							<div class="h100 personal-user-left-upload" @click="onOpenUpload">
								<img v-if="checkAcatar()" :src="personalForm.avatar" />
								<img v-else src="https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1813762643,1914315241&fm=26&gp=0.jpg" />
							</div>
						</div>
						<div class="personal-user-right">
							<el-row>
								<el-col :span="24" class="personal-title mb18"
									>{{ message.geetings }}，{{ personalForm.username }}，生活变的再糟糕，也不妨碍我变得更好！
								</el-col>
								<el-col :span="24">
									<el-row>
										<el-col :xs="24" :sm="8" class="personal-item mb6">
											<div class="personal-item-label">当前用户：</div>
											<div class="personal-item-value">{{ personalForm.username }}</div>
										</el-col>
										<el-col :xs="24" :sm="16" class="personal-item mb6">
											<div class="personal-item-label">角色：</div>
											<div class="personal-item-value">{{ message.roleName }}</div>
										</el-col>
									</el-row>
								</el-col>
								<el-col :span="24">
									<el-row>
										<el-col :xs="24" :sm="16" class="personal-item mb6">
											<div class="personal-item-label">登录时间：</div>
											<div class="personal-item-value">{{ message.currentTime }}</div>
										</el-col>
									</el-row>
								</el-col>
							</el-row>
						</div>
					</div>
				</el-card>
			</el-col>
			<!-- 消息通知 -->
			<!-- 
			<el-col :xs="24" :sm="8" class="pl15 personal-info">
				<el-card shadow="hover">
					<template #header>
						<span>消息通知</span>
						<span class="personal-info-more">更多</span>
					</template>
					<div class="personal-info-box">
						<ul class="personal-info-ul">
							<li v-for="(v, k) in newsInfoList" :key="k" class="personal-info-li">
								<a :href="v.link" target="_block" class="personal-info-li-title">{{ v.title }}</a>
							</li>
						</ul>
					</div>
				</el-card>
			</el-col> -->

			<!-- 更新信息 -->
			<el-col :span="24">
				<el-card shadow="hover" class="mt15 personal-edit" header="更新信息">
					<div class="personal-edit-title mb20">基本信息</div>
					<el-row :gutter="35">
						<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
							<el-button type="primary" :disabled="!isUpdate" @click="onSubUpdate">
								<el-icon>
									<ele-Position />
								</el-icon>
								更新个人信息
							</el-button>
						</el-col>
					</el-row>
					<el-form :model="personalForm" size="default" label-width="40px" class="mt35 mb35">
						<el-row :gutter="24">
							<el-col :lg="12">
								<el-row>
									<el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6" class="mb20">
										<el-form-item label="邮箱">
											<el-input v-model="personalForm.email" placeholder="请输入邮箱" clearable @input="InputUpdate"></el-input>
										</el-form-item>
									</el-col>
								</el-row>
								<el-row>
									<el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6" class="mb20">
										<el-form-item label="性别">
											<el-select v-model="personalForm.sex" placeholder="请选择性别" clearable class="w100" @change="InputUpdate">
												<el-option label="男" :value="0"></el-option>
												<el-option label="女" :value="1"></el-option>
											</el-select>
										</el-form-item>
									</el-col>
								</el-row>
								<el-row>
									<el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6" class="mb20">
										<el-form-item label="年龄">
											<el-input-number
												v-model="personalForm.age"
												placeholder="请输入"
												:max="100"
												:min="0"
												controls-position="right"
												clearable
												@change="InputUpdate"
											></el-input-number>
										</el-form-item>
									</el-col>
								</el-row>
								<el-row>
									<el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="mb20">
										<el-form-item label="签名">
											<el-input
												v-model="personalForm.sign"
												placeholder="请输入签名"
												:rows="4"
												type="textarea"
												clearable
												:maxlength="150"
												@input="InputUpdate"
											></el-input>
										</el-form-item>
									</el-col>
								</el-row>
							</el-col>
						</el-row>
					</el-form>
					<div class="personal-edit-title mb15">账号安全</div>
					<div class="personal-edit-safe-box">
						<div class="personal-edit-safe-item">
							<div class="personal-edit-safe-item-left">
								<div class="personal-edit-safe-item-left-label">账户密码</div>
								<div class="personal-edit-safe-item-left-value">当前密码强度：{{ message.pwdLevel }}</div>
							</div>
							<div class="personal-edit-safe-item-right">
								<el-button type="text" @click="updatePassword">立即修改</el-button>
							</div>
						</div>
					</div>
					<div class="personal-edit-safe-box">
						<div class="personal-edit-safe-item">
							<div class="personal-edit-safe-item-left">
								<div class="personal-edit-safe-item-left-label">密保手机</div>
								<div class="personal-edit-safe-item-left-value">{{ message.phone }}</div>
							</div>
							<div class="personal-edit-safe-item-right">
								<el-button type="text" @click="updatePhone">立即修改</el-button>
							</div>
						</div>
					</div>
				</el-card>
			</el-col>
		</el-row>
		<avatar-upload ref="uploadRef" @refresh="onRefresh" />
	</div>
</template>

<script lang="ts">
import { useI18n } from 'vue-i18n';
import { toRefs, reactive, computed, defineComponent, onMounted, getCurrentInstance, ref } from 'vue';
import { formatAxis, formatDate } from '/@/utils/formatTime';
import { useStore } from '/@/store/index';
import { SystemAccountApi } from '/@/api/resources/system';
import { verifyPasswordStrength, verifyUrl } from '/@/utils/toolsValidate';
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus';
import { LoginApi } from '/@/api/login';
import { Session } from '/@/utils/storage';
import { resetRoute } from '/@/router';
import AvatarUpload from './component/AvatarUpload.vue';
interface RoleItem {
	roleId: string;
	roleName: string;
	roleNameZh: string;
	roleSort: number;
}

// 定义接口来定义对象的类型
interface PersonalState {
	personalForm: {
		accountId: string;
		username: string;
		avatar: string;
		email: string;
		phone: string;
		sign: string;
		sex: string;
		age: number;
	};
	isUpdate: boolean;
	roles: Array<RoleItem>;
	message: {
		currentTime: string;
		geetings: any;
		roleName: any;
		phone: any;
		pwdLevel: any;
	};
}

export default defineComponent({
	name: 'personal',
	components: { AvatarUpload },
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const store = useStore();
		const uploadRef = ref();
		const state = reactive<PersonalState>({
			message: {
				currentTime: '',
				geetings: computed(() => {
					return formatAxis(new Date());
				}),
				roleName: '',
				phone: '',
				pwdLevel: '',
			},
			roles: [],
			isUpdate: false,
			personalForm: {
				accountId: '',
				username: '',
				avatar: '',
				email: '',
				phone: '',
				sign: '',
				sex: '',
				age: 0,
			},
		});

		const roleName = computed(() => {
			if (state.roles && state.roles.length > 0) {
				return state.roles[0].roleNameZh;
			} else {
				return '暂未分配';
			}
		});

		const messagePhone = computed(() => {
			if (!state.personalForm.phone) {
				return '尚未绑定手机号';
			} else {
				var reg = /^(\d{3})\d{4}(\d{4})$/;
				return '已绑定手机：' + state.personalForm.phone.replace(reg, '$1****$2');
			}
		});

		function init() {
			const userInfo = <any>store.state.userInfos.userInfos; // 用户详情
			let date = new Date();
			date.setTime(userInfo.time);
			state.message.currentTime = formatDate(date, 'YYYY-mm-dd HH:MM:SS');
			state.personalForm.accountId = userInfo.userId;
			state.personalForm.username = userInfo.username;
			state.personalForm.avatar = userInfo.photo;
			initAccountInfo();
			state.message.roleName = roleName;
			state.message.phone = messagePhone;
			state.message.pwdLevel = verifyPasswordStrength(userInfo.password);
		}

		function initAccountInfo() {
			if (state.personalForm.accountId) {
				SystemAccountApi()
					.queryUserInfo()
					.then((res) => {
						let info: any = JSON.stringify(res.data);
						info = JSON.parse(info);
						if (info) {
							setAccountInfo(info.info);
							setRoles(info.roles);
						}
					});
			}
		}

		function setAccountInfo(info: any) {
			if (info) {
				if (info.avatar != null && info.avatar != undefined && verifyUrl(info.avatar)) {
					state.personalForm.avatar = info.avatar;
				}
				if (info.email != null && info.email != undefined) {
					state.personalForm.email = info.email;
				}
				if (info.phone != null && info.phone != undefined) {
					state.personalForm.phone = info.phone;
				}
				if (info.sign != null && info.sign != undefined) {
					state.personalForm.sign = info.sign;
				}
				state.personalForm.sex = info.sex;
				state.personalForm.age = info.age;
			}
		}

		function setRoles(roles: any) {
			if (roles && roles.length > 0) {
				const array: Array<RoleItem> = [];
				for (let index = 0; index < roles.length; index++) {
					const element: any = roles[index];
					array.push({
						roleId: element.roleId,
						roleName: element.roleName,
						roleNameZh: element.roleNameZh,
						roleSort: element.roleSort,
					});
				}
				array.sort((a, b) => {
					return a.roleSort - b.roleSort;
				});
				state.roles = array;
			}
		}

		function checkAcatar() {
			if (state.personalForm.avatar != null && state.personalForm.avatar != undefined && verifyUrl(state.personalForm.avatar)) {
				return true;
			} else {
				return false;
			}
		}

		function InputUpdate() {
			if (!state.isUpdate) {
				state.isUpdate = true;
			}
		}

		function onSubUpdate() {
			state.isUpdate = false;
			console.log(getInfoParam());
			SystemAccountApi()
				.updateAccountInfo(getInfoParam())
				.then((res) => {
					ElNotification({
						title: '通知',
						message: '更新个人信息成功',
						type: 'success',
					});
				});
		}

		function getInfoParam() {
			let param = `type=update&accountId=${state.personalForm.accountId}&age=${state.personalForm.age}&sex=${state.personalForm.sex}`;
			if (state.personalForm.email != null && state.personalForm.email != '' && state.personalForm.email != undefined) {
				param += '&email=' + state.personalForm.email;
			}
			if (state.personalForm.sign != null && state.personalForm.sign != '' && state.personalForm.sign != undefined) {
				param += '&sign=' + state.personalForm.sign;
			}
			return param;
		}

		// 更新密码
		function updatePassword() {
			ElMessageBox.prompt('请输入新密码', '修改密码', {
				confirmButtonText: '提交修改',
				cancelButtonText: '取消修改',
				inputPattern: /^[a-zA-Z]\w{5,15}$/,
				inputErrorMessage: '新密码以字母开头，长度在6~16之间，只能包含字母、数字和下划线',
			})
				.then(({ value }) => {
					SystemAccountApi()
						.resortPassword(`accountId=${state.personalForm.accountId}&newPwd=${value}`)
						.then((res) => {
							ElNotification({
								title: '通知',
								message: '更新密码成功',
								type: 'success',
							});
							setTimeout(() => {
								ElMessage.warning('请重新登录');
								LoginApi()
									.logout({})
									.then(async (res) => {
										Session.clear(); // 清除缓存/token等
										await resetRoute(); // 删除/重置路由
										window.location.href = '/'; // 去登录页
									})
									.catch(() => {});
							}, 1300);
						});
				})
				.catch(() => {
					ElMessage({
						type: 'info',
						message: '您取消了修改密码',
					});
				});
		}

		// 更新手机号
		function updatePhone() {
			ElMessageBox.prompt('请输入手机号', '修改手机号', {
				confirmButtonText: '提交修改',
				cancelButtonText: '取消修改',
				inputPattern: /^((12[0-9])|(13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\d{8}$/,
				inputErrorMessage: '请输入正确的手机号',
			})
				.then(({ value }) => {
					SystemAccountApi()
						.updateAccountInfo(`type=update&accountId=${state.personalForm.accountId}&phone=${value}`)
						.then((res) => {
							ElNotification({
								title: '通知',
								message: '更新个人信息成功',
								type: 'success',
							});
						});
				})
				.catch(() => {
					ElMessage({
						type: 'info',
						message: '您取消了修改手机号',
					});
				});
		}

		function onOpenUpload() {
			uploadRef.value.openDialog(state.personalForm);
		}

		function onRefresh() {
			initAccountInfo();
		}

		onMounted(() => {
			init();
		});
		return {
			uploadRef,
			checkAcatar,
			InputUpdate,
			onSubUpdate,
			updatePassword,
			updatePhone,
			onOpenUpload,
			onRefresh,
			...toRefs(state),
		};
	},
});
</script>

<style scoped lang="scss">
@import '../../theme/mixins/index.scss';
.personal {
	.personal-user {
		height: 130px;
		display: flex;
		align-items: center;
		.personal-user-left {
			width: 100px;
			height: 130px;
			border-radius: 3px;
			::v-deep(.el-upload) {
				height: 100%;
			}
			.personal-user-left-upload {
				img {
					width: 100%;
					height: 100%;
					border-radius: 3px;
				}
				&:hover {
					img {
						animation: logoAnimation 0.3s ease-in-out;
					}
				}
			}
		}
		.personal-user-right {
			flex: 1;
			padding: 0 15px;
			.personal-title {
				font-size: 18px;
				@include text-ellipsis(1);
			}
			.personal-item {
				display: flex;
				align-items: center;
				font-size: 13px;
				.personal-item-label {
					color: var(--el-text-color-secondary);
					@include text-ellipsis(1);
				}
				.personal-item-value {
					@include text-ellipsis(1);
				}
			}
		}
	}
	.personal-info {
		.personal-info-more {
			float: right;
			color: var(--el-text-color-secondary);
			font-size: 13px;
			&:hover {
				color: var(--color-primary);
				cursor: pointer;
			}
		}
		.personal-info-box {
			height: 130px;
			overflow: hidden;
			.personal-info-ul {
				list-style: none;
				.personal-info-li {
					font-size: 13px;
					padding-bottom: 10px;
					.personal-info-li-title {
						display: inline-block;
						@include text-ellipsis(1);
						color: var(--el-text-color-secondary);
						text-decoration: none;
					}
					& a:hover {
						color: var(--color-primary);
						cursor: pointer;
					}
				}
			}
		}
	}
	.personal-recommend-row {
		.personal-recommend-col {
			.personal-recommend {
				position: relative;
				height: 100px;
				color: var(--color-whites);
				border-radius: 3px;
				overflow: hidden;
				cursor: pointer;
				&:hover {
					i {
						right: 0px !important;
						bottom: 0px !important;
						transition: all ease 0.3s;
					}
				}
				i {
					position: absolute;
					right: -10px;
					bottom: -10px;
					font-size: 70px;
					transform: rotate(-30deg);
					transition: all ease 0.3s;
				}
				.personal-recommend-auto {
					padding: 15px;
					position: absolute;
					left: 0;
					top: 5%;
					.personal-recommend-msg {
						font-size: 12px;
						margin-top: 10px;
					}
				}
			}
		}
	}
	.personal-edit {
		.personal-edit-title {
			position: relative;
			padding-left: 10px;
			color: var(--el-text-color-regular);
			&::after {
				content: '';
				width: 2px;
				height: 10px;
				position: absolute;
				left: 0;
				top: 50%;
				transform: translateY(-50%);
				background: var(--color-primary);
			}
		}
		.personal-edit-safe-box {
			border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
			padding: 15px 0;
			.personal-edit-safe-item {
				width: 100%;
				display: flex;
				align-items: center;
				justify-content: space-between;
				.personal-edit-safe-item-left {
					flex: 1;
					overflow: hidden;
					.personal-edit-safe-item-left-label {
						color: var(--el-text-color-regular);
						margin-bottom: 5px;
					}
					.personal-edit-safe-item-left-value {
						color: var(--el-text-color-secondary);
						@include text-ellipsis(1);
						margin-right: 15px;
					}
				}
			}
			&:last-of-type {
				padding-bottom: 0;
				border-bottom: none;
			}
		}
	}
}
</style>
