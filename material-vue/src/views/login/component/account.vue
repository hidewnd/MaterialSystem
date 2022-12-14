<template>
	<el-form size="large" class="login-content-form">
		<el-form-item class="login-animation1">
			<el-input type="text" :placeholder="$t('message.account.accountPlaceholder1')" v-model="ruleForm.username" clearable autocomplete="off">
				<template #prefix>
					<el-icon class="el-input__icon"><ele-User /></el-icon>
				</template>
			</el-input>
		</el-form-item>
		<el-form-item class="login-animation2">
			<el-input
				:type="isShowPassword ? 'text' : 'password'"
				:placeholder="$t('message.account.accountPlaceholder2')"
				v-model="ruleForm.password"
				autocomplete="off"
			>
				<template #prefix>
					<el-icon class="el-input__icon"><ele-Unlock /></el-icon>
				</template>
				<template #suffix>
					<i
						class="iconfont el-input__icon login-content-password"
						:class="isShowPassword ? 'icon-yincangmima' : 'icon-xianshimima'"
						@click="isShowPassword = !isShowPassword"
					>
					</i>
				</template>
			</el-input>
		</el-form-item>
		<el-form-item class="login-animation3">
			<el-col :span="15">
				<el-input type="text" maxlength="4" :placeholder="$t('message.account.accountPlaceholder3')" v-model="code" clearable autocomplete="off">
					<template #prefix>
						<el-icon class="el-input__icon"><ele-Position /></el-icon>
					</template>
				</el-input>
			</el-col>
			<el-col :span="1"></el-col>
			<el-col :span="8">
				<el-button class="login-content-code" @click="updateCode">{{ createcode }}</el-button>
			</el-col>
		</el-form-item>
		<el-form-item class="login-animation4">
			<el-button type="primary" class="login-content-submit" round @click="onSignIn" :loading="loading.signIn">
				<span>{{ $t('message.account.accountBtnText') }}</span>
			</el-button>
		</el-form-item>
	</el-form>
</template>

<script lang="ts">
import { toRefs, reactive, defineComponent, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { initFrontEndControlRoutes } from '/@/router/frontEnd';
import { useStore } from '/@/store';
import { Session } from '/@/utils/storage';
import { formatAxis } from '/@/utils/formatTime';
import { LoginApi } from '/@/api/login';
export default defineComponent({
	name: 'loginAccount',
	setup() {
		const { t } = useI18n();
		const store = useStore();
		const route = useRoute();
		const router = useRouter();
		const state = reactive({
			isShowPassword: false,
			ruleForm: {
				username: '',
				password: '',
			},
			code: '',
			createcode: '1234',
			loading: {
				signIn: false,
			},
		});
		// ????????????
		const currentTime = computed(() => {
			return formatAxis(new Date());
		});

		// ??????
		const onSignIn = async () => {
			if (state.code.toLowerCase() != state.createcode.toLowerCase()) {
				ElMessage.error('??????????????????');
				return;
			}
			state.loading.signIn = true;
			let defaultRoles: Array<string> = ['common'];
			let defaultAuthBtnList: Array<string> = ['btn.add', 'btn.del', 'btn.edit', 'btn.link'];
			LoginApi()
				.login(state.ruleForm)
				.then((res) => {
					let user = JSON.parse(decodeURIComponent(escape(window.atob(res.data.access_token.split('.')[1]))));
					defaultRoles = defaultRoles.concat(user.authorities);
					// ????????????
					const userInfos = {
						username: user.user_name,
						userId: user.user_id,
						photo: 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1813762643,1914315241&fm=26&gp=0.jpg',
						time: new Date().getTime(),
						roles: defaultRoles,
						authBtnList: defaultAuthBtnList,
						password: state.ruleForm.password,
					};
					// ?????? token ??????????????????
					Session.set('token', res.data.access_token);
					Session.set('refreshToken', res.data.refresh_token);
					// ????????????????????????????????????
					Session.set('userInfo', userInfos);
					// 1????????????????????????(?????????????????????vuex)
					store.dispatch('userInfos/setUserInfos', userInfos);
					Success();
				})
				.catch(() => {
					state.loading.signIn = false;
				});
		};

		async function Success() {
			await initFrontEndControlRoutes();
			signInSuccess();
		}

		// ????????????????????????
		function signInSuccess() {
			// ????????????????????????????????????
			let currentTimeInfo = currentTime.value;
			// ??????????????????????????????
			// ????????????????????????????????? router ??????????????????????????? No match found for location with path "/"
			// ??????????????????????????????????????????/???????????????????????????????????????????????????????????????
			if (route.query?.redirect) {
				router.push({
					path: <string>route.query?.redirect,
					query: Object.keys(<string>route.query?.params).length > 0 ? JSON.parse(<string>route.query?.params) : '',
				});
			} else {
				router.push('/');
			}
			// ??????????????????
			// ?????? loading
			state.loading.signIn = true;
			const signInText = t('message.signInText');
			ElMessage.success(`${currentTimeInfo}???${signInText}`);
		}

		function createCode(length: number) {
			var code = '';
			//  ??????????????????????????????????????????????????????????????????
			var codeChars = [
				0,
				1,
				2,
				3,
				4,
				5,
				6,
				7,
				8,
				9,
				'a',
				'b',
				'c',
				'd',
				'e',
				'f',
				'g',
				'h',
				'i',
				'j',
				'k',
				'l',
				'm',
				'n',
				'o',
				'p',
				'q',
				'r',
				's',
				't',
				'u',
				'v',
				'w',
				'x',
				'y',
				'z',
				'A',
				'B',
				'C',
				'D',
				'E',
				'F',
				'G',
				'H',
				'I',
				'J',
				'K',
				'L',
				'M',
				'N',
				'O',
				'P',
				'Q',
				'R',
				'S',
				'T',
				'U',
				'V',
				'W',
				'X',
				'Y',
				'Z',
			];
			//?????????????????????????????????
			for (var i = 0; i < length; i++) {
				//???????????????????????????
				var charNum = Math.floor(Math.random() * 62);
				//??????????????????????????????
				code += codeChars[charNum];
			}
			return code;
		}

		function updateCode() {
			state.createcode = createCode(4);
		}

		onMounted(() => {
			state.createcode = createCode(4);
		});
		return {
			onSignIn,
			updateCode,
			...toRefs(state),
		};
	},
});
</script>

<style scoped lang="scss">
.login-content-form {
	margin-top: 20px;
	@for $i from 1 through 4 {
		.login-animation#{$i} {
			opacity: 0;
			animation-name: error-num;
			animation-duration: 0.5s;
			animation-fill-mode: forwards;
			animation-delay: calc($i/10) + s;
		}
	}
	.login-content-password {
		display: inline-block;
		width: 20px;
		cursor: pointer;
		&:hover {
			color: #909399;
		}
	}
	.login-content-code {
		width: 100%;
		padding: 0;
		font-weight: bold;
		letter-spacing: 5px;
	}
	.login-content-submit {
		width: 100%;
		letter-spacing: 2px;
		font-weight: 300;
		margin-top: 15px;
	}
}
</style>
