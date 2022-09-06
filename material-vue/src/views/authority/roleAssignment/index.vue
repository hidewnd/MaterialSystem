<template>
	<div class="authority-role-assignment">
		<el-card shadow="alway" :header="$t('message.router.roleAssignment')">
			<div class="resources-system-search mb15">
				<el-form :model="ruleform" size="default" label-width="90px">
					<el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb20">
						<el-form-item label-width="auto">
							<el-select
								v-model="ruleform.accountId"
								filterable
								remote
								reserve-keyword
								clearable
								placeholder="请输用户系统名查询"
								:remote-method="onSelectRemote"
								@change="onSelectChange"
								:loading="ruleform.loading"
								autofocus="false"
								style="max-width: 180px"
							>
								<el-option v-for="item in selectOptions" :key="item.accountId" :label="item.username" :value="item.accountId" />
							</el-select>
						</el-form-item>
					</el-col>
				</el-form>
				<el-button size="default" type="primary" class="ml10" @click="onSearch">
					<el-icon><ele-Search /></el-icon>
					查询
				</el-button>
				<el-button size="default" type="success" class="ml10" @click="onSave" :disabled="!isAllow">
					<el-icon><ele-FolderAdd /> </el-icon>
					保存
				</el-button>
				<el-button size="default" type="primary" class="ml10" @click="onRefresh" :disabled="!isAllow">
					<el-icon> <ele-Refresh /> </el-icon>
					刷新
				</el-button>
			</div>

			<div v-if="isAllow">
				<el-transfer
					:data="roleList"
					v-model="ruleform.list"
					@change="handleChange"
					:render-content="renderFunc"
					filterable
					:titles="['未选择角色', '已选择角色']"
					:button-texts="['取消', '选择']"
					:format="{
						noChecked: '${total}',
						hasChecked: '${checked}/${total}',
					}"
					:props="{
						key: 'roleId',
					}"
				>
				</el-transfer>
			</div>
			<el-empty description="请选择操作用户" v-else> </el-empty>
		</el-card>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, ref, getCurrentInstance, onMounted, watch } from 'vue';
import type { VNode, VNodeProps } from 'vue';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { ResourceSystemApi, SystemAccountApi } from '/@/api/resources/system';

interface OptionItem {
	accountId: string;
	username: string;
}

interface RoleItem {
	roleId: string;
	roleName: string;
	roleNameZh: string;
	disabled: boolean;
}

interface MenuSate {
	ruleform: {
		accountId: string;
		username: string;
		loading: boolean;
		list: Array<string>;
		removeList: Array<string>;
		addList: Array<string>;
	};
	selectOptions: Array<OptionItem>; //select数据源
	roleList: Array<RoleItem>; // 穿梭框menu数据源
	oldList: Array<string>; // 已选择menu
	isAllow: boolean;
}

export default defineComponent({
	name: 'AuthorityRoleAssignment',
	components: {},
	setup() {
		const { t } = useI18n();
		const { proxy } = <any>getCurrentInstance();
		const state = reactive<MenuSate>({
			ruleform: {
				accountId: '',
				username: '',
				loading: false,
				list: [],
				removeList: [],
				addList: [],
			},
			selectOptions: [],
			roleList: [],
			oldList: [],
			isAllow: false,
		});

		// 点击查询
		function onSearch() {
			if (!state.ruleform.accountId) {
				ElMessage.error('请先选择操作用户');
				return;
			}
			initTransfer();
		}
		// 点击保存
		function onSave() {
			ElMessageBox.confirm(`是否保存本次修改？`, '提示', {
				confirmButtonText: t('message.resources.common.confirmButtonText'),
				cancelButtonText: t('message.resources.common.cancelButtonText'),
				type: 'warning',
			}).then(() => {
				onSubmit();
			});
		}

		function onSubmit() {
			if (!state.ruleform.accountId) {
				ElMessage.error('请先选择操作用户');
				return;
			}

			state.ruleform.removeList = state.oldList;
			state.ruleform.addList = state.ruleform.list;
			const array = getRepeat(state.ruleform.removeList, state.ruleform.addList);

			for (let i = 0; i < array.length; i++) {
				const element = array[i];
				state.ruleform.removeList = removeElement(element, state.ruleform.removeList);
				state.ruleform.addList = removeElement(element, state.ruleform.addList);
			}

			Promise.all([removeMenu(), addMenu()])
				.then((res) => {
					ElNotification({
						title: '通知',
						message: '保存角色权限修改成功',
						type: 'success',
					});
					initTransfer();
				})
				.catch((err) => {
					ElNotification({
						title: '通知',
						message: '保存角色权限修改失败',
						type: 'error',
					});
					initTransfer();
				});
		}

		async function removeMenu() {
			return new Promise((resolve, reject) => {
				if (state.ruleform.removeList && state.ruleform.removeList.length > 0) {
					SystemAccountApi()
						.removeRoleByList({ id: state.ruleform.accountId, bindList: state.ruleform.removeList })
						.then((res) => {
							resolve(true);
						})
						.catch((err) => {
							reject(true);
						});
				} else {
					resolve(true);
				}
			});
		}

		async function addMenu() {
			return new Promise((resolve, reject) => {
				if (state.ruleform.addList && state.ruleform.addList.length > 0) {
					SystemAccountApi()
						.bindRoleByList({ id: state.ruleform.accountId, bindList: state.ruleform.addList })
						.then((res) => {
							resolve(true);
						})
						.catch((err) => {
							reject(true);
						});
				} else {
					resolve(true);
				}
			});
		}

		// 获取相同数组
		function getRepeat(list1: Array<string>, list2: Array<string>): Array<string> {
			const repeat: Array<string> = [];
			if (list1 && list2) {
				list1.forEach((value, index, array) => {
					if (list2.indexOf(value) > -1) {
						repeat.push(value);
					}
				});
			}
			return repeat;
		}
		// 移除元素
		function removeElement(element: string, array: Array<string>): Array<string> {
			if (element) {
				let index = array.indexOf(element);
				if (index > -1) {
					array.splice(index, 1);
				}
				return array;
			}
			return array;
		}

		// 点击刷新
		function onRefresh() {
			onSearch();
		}

		// select输入搜索
		function onSelectRemote(query: string) {
			if (query) {
				state.ruleform.loading = true;
				ResourceSystemApi()
					.queryList(`account/like/username/${query}`)
					.then((res) => {
						let list = JSON.stringify(res.data);
						list = JSON.parse(list);
						if (list) {
							const array: Array<OptionItem> = [];
							for (let i = 0; i < list.length; i++) {
								const element: any = list[i];
								if (element) {
									array.push({ accountId: element.accountId, username: element.username });
								}
							}
							state.selectOptions = array;
						}
						state.ruleform.loading = false;
					})
					.catch((res) => {
						state.ruleform.loading = false;
					});
			}
		}

		// select 选中
		function onSelectChange(accountId: string) {
			if (accountId) {
				state.isAllow == true;
				const list = state.selectOptions;
				for (let i = 0; i < list.length; i++) {
					const element = list[i];
					if (element.accountId === accountId) {
						state.ruleform.username = element.accountId;
						return;
					}
				}
			}
		}

		// 初始化穿梭框架
		function initTransfer() {
			state.ruleform.list = [];
			state.oldList = [];
			if (state.ruleform.accountId) {
				ResourceSystemApi()
					.queryOne({ name: 'account', type: 'id', arg1: state.ruleform.accountId })
					.then((res) => {
						let account: any = JSON.stringify(res.data);
						account = JSON.parse(account);
						if (account && account.roles && account.roles.length > 0) {
							const array: Array<string> = [];
							for (let i = 0; i < account.roles.length; i++) {
								array.push(account.roles[i].roleId);
							}
							state.ruleform.list = array;
							state.oldList = array;
						}
					});
			}
		}

		// 自定义数据项渲染函数
		function renderFunc(h: (type: string, props: VNodeProps | null, children?: string) => VNode, option: RoleItem) {
			return h('span', null, `${option.roleNameZh}：[${option.roleName}]`);
		}

		// 该百年监听
		function handleChange(value: number | string, direction: 'left' | 'right', movedKeys: string[] | number[]) {
			// console.log(value, direction, movedKeys);
		}

		// 初始化
		onMounted(() => {
			initroleList();
		});

		// 初始访问数据
		function initroleList() {
			state.roleList = [];
			ResourceSystemApi()
				.queryList('role/all')
				.then((res) => {
					let list = JSON.stringify(res.data);
					list = JSON.parse(list);
					if (list) {
						const array: Array<RoleItem> = [];
						for (let i = 0; i < list.length; i++) {
							const element: any = list[i];
							if (element.stated != undefined && element.stated === 0) {
								array.push({
									roleId: element.roleId,
									roleName: element.roleName,
									roleNameZh: element.roleNameZh,
									disabled: element.roleId === '' || element.roleId === `1`,
								});
							}
						}
						state.roleList = array;
					}
				});
		}

		watch(
			() => state.ruleform.accountId,
			() => {
				if (state.ruleform.accountId != undefined && state.ruleform.accountId != '') {
					onSearch();
					state.isAllow = true;
				} else {
					state.isAllow = false;
				}
			},
			{ deep: true, immediate: true }
		);

		return {
			onSearch,
			onSave,
			onRefresh,
			onSelectRemote,
			onSelectChange,
			renderFunc,
			handleChange,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss">
.authority-role-assignment {
	.transfer-footer {
		margin-left: 15px;
		padding: 6px 5px;
	}
	.el-transfer {
		text-align: left;
		display: inline-block;
		width: 100%;

		.el-transfer-panel {
			width: 20%;
		}
	}
}
</style>
