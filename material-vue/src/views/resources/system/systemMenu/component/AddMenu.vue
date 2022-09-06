<template>
	<div class="system-add-menu-container">
		<el-dialog title="新增权限" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="父级权限">
							<el-select
								v-model="ruleForm.parentId"
								filterable
								remote
								reserve-keyword
								clearable
								placeholder="请输入父级ID"
								:remote-method="onSelectRemote"
								@change="onSelectChange"
								:loading="ruleForm.loading"
								autofocus="false"
								style="max-width: 180px"
							>
								<el-option v-for="item in selectOptions" :key="item.menuId" :label="item.name" :value="item.menuId" />
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="权限名">
							<el-input v-model="ruleForm.name" placeholder="请输入权限名" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="请求地址">
							<el-input v-model="ruleForm.url" placeholder="请输入请求地址" clearable></el-input>
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
import { ResourceSystemApi, SystemMenuApi } from '/@/api/resources/system';
import { useI18n } from 'vue-i18n';
import { verifyCnAndSpace, verifiyNumberInteger, verifyUrl } from '/@/utils/toolsValidate';

interface OptionItem {
	menuId: string;
	name: string;
	url: string;
	perms: string;
}

interface UserState {
	isShowDialog: boolean;
	selectOptions: Array<OptionItem>;
	ruleForm: {
		name: string;
		url: string;
		parentId: string;
		loading: boolean;
	};
}

export default defineComponent({
	name: 'systemAddMenu',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			selectOptions: [],
			ruleForm: {
				name: '',
				url: '',
				parentId: '',
				loading: false,
			},
		});
		// 打开弹窗
		const openDialog = () => {
			state.isShowDialog = true;
		};
		// 关闭弹窗
		const closeDialog = () => {
			state.ruleForm = {
				name: '',
				url: '',
				parentId: '',
				loading: false,
			};
			state.isShowDialog = false;
			context.emit('refresh');
		};
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
			state.ruleForm.name = verifyCnAndSpace(state.ruleForm.name);
			if (state.ruleForm.parentId != '' && state.ruleForm.parentId != null) {
				const id = verifiyNumberInteger(state.ruleForm.parentId);
				if (id === null || id === '') {
					ElMessage.warning('父级ID应为数字');
					return;
				}
			}
			if (state.ruleForm.name == '') {
				ElMessage.warning('权限名称不能为空或者中文');
				return;
			}
			if (verifiyNumberInteger(state.ruleForm.name) != '') {
				ElMessage.warning('权限名称不能数字');
				return;
			}

			if (state.ruleForm.url.indexOf('/') != 0 || state.ruleForm.url === '') {
				ElMessage.warning('请求地址格式不正确');
				return;
			}
			let param = `menuName=${state.ruleForm.name}&url=${state.ruleForm.url}`;
			if (state.ruleForm.parentId != '' && state.ruleForm.parentId) {
				param += `&parentId=${state.ruleForm.parentId}`;
			}
			SystemMenuApi()
				.insertMenu(param)
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		function onSelectRemote(query: string) {
			state.ruleForm.loading = true;
			if (query) {
				ResourceSystemApi()
					.queryList(`menu/like/name/${query}`)
					.then((res) => {
						let list = JSON.stringify(res.data);
						list = JSON.parse(list);
						if (list) {
							const array: Array<OptionItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (element != undefined && element.status === 0) {
									array.push({
										menuId: element.menuId,
										name: element.name,
										url: element.url,
										perms: element.perms,
									});
								}
							}
							state.selectOptions = array;
						}
						state.ruleForm.loading = false;
					})
					.catch((res) => {
						state.ruleForm.loading = false;
					});
			} else {
			}
		}

		function onSelectChange(menuId: string) {}

		// 页面加载时
		onMounted(() => {});
		return {
			openDialog,
			closeDialog,
			onCancel,
			onSubmit,
			onSelectRemote,
			onSelectChange,
			...toRefs(state),
		};
	},
});
</script>
