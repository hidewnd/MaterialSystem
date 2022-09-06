<template>
	<div class="system-edit-user-container">
		<el-dialog title="修改权限" v-model="isShowDialog" width="769px">
			<el-form :model="ruleForm" size="default" label-width="90px">
				<el-row :gutter="35">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="权限编号">
							<el-input v-model="ruleForm.menuId" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="父级权限" label-width="auto">
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
								style="max-width: 180px"
							>
								<el-option v-for="item in selectOptions" :key="item.menuId" :label="item.name" :value="item.menuId" />
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="权限名称">
							<el-input v-model="ruleForm.name" placeholder="请输入用户密码" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="请求地址">
							<el-input v-model="ruleForm.url" placeholder="请输入地址通配符" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="权限符">
							<el-input v-model="ruleForm.perms" placeholder="请输入" disabled></el-input>
						</el-form-item>
					</el-col>
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label="权限状态">
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
import { reactive, toRefs, onMounted, defineComponent } from 'vue';
import { ResourceSystemApi, SystemMenuApi } from '/@/api/resources/system';
import { SysMenu } from '/@/store/interface';
import { ElMessage, ElMessageBox } from 'element-plus/es';
import { useI18n } from 'vue-i18n';

interface OptionItem {
	menuId: string;
	name: string;
	url: string;
	perms: string;
}

interface UserState {
	isShowDialog: boolean;
	ruleForm: SysMenu;
	loading: boolean;
	selectOptions: Array<OptionItem>;
	parentMenuName: string;
}

export default defineComponent({
	name: 'systemEditMenu',
	setup(props, context) {
		const { t } = useI18n();
		const state = reactive<UserState>({
			isShowDialog: false,
			selectOptions: [],
			loading: false,
			parentMenuName: '',
			ruleForm: {
				menuId: '',
				parentId: '',
				name: '',
				url: '',
				perms: '',
				status: 0,
				delFlag: 0,
				createBy: '',
				createDate: '',
				updateBy: '',
				updateDate: '',
				remark: '',
				parent: undefined,
			},
		});
		// 打开弹窗
		function openDialog(row: SysMenu) {
			state.ruleForm = row;
			initSelectOptions();
			state.isShowDialog = true;
		}
		// 关闭弹窗
		function closeDialog() {
			ResourceSystemApi()
				.queryOne({ name: 'menu', type: 'id', arg1: state.ruleForm.menuId })
				.then((res) => {
					let data: any = JSON.stringify(res.data);
					data = JSON.parse(data);
					if (data) {
						state.ruleForm.updateDate = data.updateDate;
						state.ruleForm.updateBy = data.updateBy;
					}
				});
			state.isShowDialog = false;
			context.emit('refresh');
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

		// 保存
		function onSubmit() {
			SystemMenuApi()
				.setMeu(getParam('update'))
				.then((res) => {
					ElMessage.success(t('message.resources.table.saveSuccess'));
					closeDialog();
				});
		}

		function onSelectRemote(query: string) {
			if (query) {
				state.loading = true;
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
						state.loading = false;
					})
					.catch((res) => {
						state.loading = false;
					});
			} else {
				initSelectOptions();
			}
		}

		function onSelectChange(menuId: string) {}

		function initSelectOptions() {
			state.loading = true;
			ResourceSystemApi()
				.queryList(`menu/all`)
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
					state.loading = false;
				})
				.catch((res) => {
					state.loading = false;
				});
		}

		// 页面加载时
		onMounted(() => {});

		function getParam(type: string) {
			let param = `type=${type}&menuId=${state.ruleForm.menuId}&name=${state.ruleForm.name}&perms=${state.ruleForm.perms}&url=${state.ruleForm.url}&status=${state.ruleForm.status}`;
			if (state.ruleForm.parentId != null && state.ruleForm.parentId != '' && state.ruleForm.parentId != undefined) {
				param += '&parentId=' + state.ruleForm.parentId;
			}
			if (state.ruleForm.remark != null && state.ruleForm.remark != '' && state.ruleForm.remark != undefined) {
				param += '&remark=' + state.ruleForm.remark;
			}
			return param;
		}
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
