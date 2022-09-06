<template>
	<div class="personal-avatar-upload-container">
		<el-dialog title="头像上传" v-model="isShowDialog" width="769px">
			<el-form :model="ruleform" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="24" :xl="24" class="mb10">
						<el-upload
							ref="uploadRef"
							class="avatar-uploader"
							:action="ruleform.data.host"
							:data="ruleform.data"
							:headers="uploadHeaders"
							list-type="picture-card"
							:on-preview="handlePreview"
							:on-remove="handleRemove"
							:before-upload="beforeAvatarUpload"
							:on-success="handleAvatarSuccess"
							:file-list="fileList"
							:limit="1"
							:auto-upload="false"
						>
							<el-icon class="avatar-uploader-icon"><Plus /></el-icon>
							<template #tip>
								<div class="el-upload__tip">请上传JPEG或PNG格式的图片，图片大小不大于2MB</div>
							</template>
						</el-upload>
					</el-col>
				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel" size="default">取 消</el-button>
					<el-button type="primary" @click="onSubmit" size="default">上 传</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, defineComponent, ref } from 'vue';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus/es';
import { initFrontEndControlRoutes } from '/@/router/frontEnd';
import { useI18n } from 'vue-i18n';
import { CommonApi } from '/@/api/common';
import { Plus } from '@element-plus/icons-vue';
import { SystemAccountApi } from '/@/api/resources/system';
import { Session } from '/@/utils/storage';
import { useStore } from '/@/store/index';
interface objectData {
	OSSAccessKeyId: string;
	Signature: string;
	policy: string;
	key: string; //文件名
	host: string;
	dir: string;
}

interface State {
	isShowDialog: boolean;
	uploadHeaders: any;
	ruleform: {
		accountId: string;
		avatar: string;
		action: string;
		data: objectData;
	};
	fileList: any;
}

export default defineComponent({
	name: 'DepotRackTransfer',
	components: { Plus },
	setup(props, context) {
		const { t } = useI18n();
		const uploadRef = ref();
		const store = useStore();
		const state = reactive<State>({
			isShowDialog: false,
			ruleform: {
				accountId: '',
				avatar: 'https://resources.hidewnd.cn/',
				action: '',
				data: {
					OSSAccessKeyId: '',
					Signature: '',
					policy: '',
					key: '', //文件名
					host: '',
					dir: '',
				},
			},
			fileList: [],
			uploadHeaders: { 'x-oss-object-acl': 'public-read' },
		});

		// 打开弹窗
		function openDialog(data: any) {
			state.isShowDialog = true;
			if (data) {
				state.ruleform.accountId = data.accountId;
			}
		}

		// 关闭弹窗
		function closeDialog() {
			state.isShowDialog = false;
			state.fileList = [];
			setTimeout(() => {
				context.emit('refresh');
			}, 1500);
		}
		// 移除
		function handleRemove(uploadFile: any, uploadFiles: any) {}

		//点击
		function handlePreview(file: any) {
			console.log(file);
		}

		function beforeAvatarUpload(rawFile: any) {
			if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
				ElMessage.error('头像仅支持JPEG或PNG格式');
				return false;
			} else if (rawFile.size / 1024 / 1024 > 2) {
				ElMessage.error('头像大小不大于2MB');
				return false;
			}
			return new Promise((resolve, reject) => {
				CommonApi()
					.getSignature()
					.then((res) => {
						let signature: any = JSON.stringify(res.data);
						signature = JSON.parse(signature);
						state.ruleform.data = {
							OSSAccessKeyId: signature.OSSAccessKeyId,
							Signature: signature.Signature,
							policy: signature.policy,
							key: signature.dir + '${filename}', //文件名
							host: signature.host,
							dir: signature.dir,
						};
						state.ruleform.avatar += signature.dir;
						resolve(true);
					})
					.catch((err) => {
						reject(false);
					});
			});
		}

		// 上传成功
		function handleAvatarSuccess(response: any, uploadFile: any) {
			uploadRef.value.clearFiles();
			state.ruleform.avatar += uploadFile.name;
			SystemAccountApi()
				.updateAccountInfo(`type=update&accountId=${state.ruleform.accountId}&avatar=${state.ruleform.avatar}`)
				.then((res) => {
					ElNotification({
						title: '通知',
						message: '更新头像成功',
						type: 'success',
					});
					let userinfo = <any>store.state.userInfos;
					userinfo.photo = state.ruleform.avatar;
					store.dispatch('userInfos/setUserInfos', userinfo);
					closeDialog();
				});
		}


		// 取消
		function onCancel() {
			closeDialog();
		}

		// 提交修改
		function onSubmit() {
			uploadRef.value!.submit();
		}

		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			handleRemove,
			handlePreview,
			beforeAvatarUpload,
			handleAvatarSuccess,
			uploadRef,
			...toRefs(state),
		};
	},
});
</script>
