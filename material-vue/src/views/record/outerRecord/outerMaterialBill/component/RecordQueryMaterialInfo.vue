<template>
	<div class="record-query-material-info-container">
		<el-dialog title="查询出库凭证" v-model="isShowDialog" width="40%" draggable>
			<el-row :gutter="24">
				<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-descriptions direction="vertical" style="width: 100%" :column="1" size="large" border>
						<el-descriptions-item label="出库记录编号" align="”center" autofocus="false">
							<el-input v-model="ruleForm.recordId" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="物料编号" align="”center" autofocus="false">
							<el-input v-model="ruleForm.materialId" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="物料系统名" align="”center" autofocus="false">
							<el-input v-model="ruleForm.materialName" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="物料中文名" align="”center" autofocus="false">
							<el-input v-model="ruleForm.materialNameZh" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="库存地址" align="”center" autofocus="false">
							<el-input v-model="ruleForm.location" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="物料二维码" align="”center">
							<el-empty description="暂无数据" :image-size="10" v-if="!ruleForm.isQuery" />
							<template v-else>
								<template v-for="(v, k) in ruleForm.qrcode" :key="k">
									<el-input v-model="ruleForm.qrcode[k]" type="textarea" autosize />
								</template>
							</template>
						</el-descriptions-item>
					</el-descriptions>
				</el-col>
				<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb5">
					<el-input v-model="ruleForm.generqrcode" type="textarea" autosize />
				</el-col>
				<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb5">
					<el-button type="primary" size="default" @click="initQrcode">生成二维码</el-button>
					<div class="qrcode" ref="qrcodeRef"></div>
				</el-col>
			</el-row>
			<template #footer>
				<span class="dialog-footer">
					<el-button type="primary" @click="onCancel" size="default">确 认</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { reactive, toRefs, defineComponent, getCurrentInstance, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { OuterRecordAPI } from '/@/api/manager/material/record';
import QRCode from 'qrcodejs2-fixes';
import { ElMessage, ElSteps } from 'element-plus';
import { el } from 'element-plus/lib/locale';

interface State {
	isShowDialog: boolean;
	ruleForm: {
		recordId: string;
		materialId: string;
		materialName: string;
		materialNameZh: string;
		number: number;
		location: string;
		qrcode: Array<string>;
		isQuery: boolean;
		generqrcode: string;
	};
}

export default defineComponent({
	name: 'RecordQueryMaterialInfo',
	setup(props, context) {
		const { proxy } = getCurrentInstance() as any;
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				recordId: '',
				materialId: '',
				materialName: '',
				materialNameZh: '',
				location: '',
				qrcode: [],
				number: 0,
				generqrcode: '',
				isQuery: false,
			},
		});

		// 打开弹窗
		function openDialog(data: any) {
			state.isShowDialog = true;
			if (data) {
				state.ruleForm.recordId = data.recordId;
				state.ruleForm.materialId = data.materialId;
				state.ruleForm.materialName = data.materialName;
				state.ruleForm.materialNameZh = data.materialNameZh;
				state.ruleForm.location = data.location;
				state.ruleForm.number = data.number;
				queryQrcode();
			}
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				recordId: '',
				materialId: '',
				materialName: '',
				materialNameZh: '',
				location: '',
				qrcode: [],
				number: 0,
				generqrcode: '',
				isQuery: false,
			};
			state.isShowDialog = false;
		}

		function queryQrcode() {
			if (state.ruleForm.recordId && state.ruleForm.materialId) {
				OuterRecordAPI()
					.getQrCode(`recordId=${state.ruleForm.recordId}&materialId=${state.ruleForm.materialId}`)
					.then((res) => {
						let data = JSON.stringify(res.data);
						data = JSON.parse(data);
						if (data) {
							const array: Array<string> = [];
							for (let index = 0; index < data.length; index++) {
								const element: any = data[index];
								array.push(element);
							}
							state.ruleForm.qrcode = array;
							state.ruleForm.generqrcode = state.ruleForm.qrcode[0];
						}
					});
			} else {
				ElMessage.error('缺少相关参数');
			}
		}

		// 初始化生成二维码
		const initQrcode = () => {
			proxy.$refs.qrcodeRef.innerHTML = '';
			new QRCode(proxy.$refs.qrcodeRef, {
				text: `${state.ruleForm.generqrcode}`,
				width: 150,
				height: 150,
				correctLevel: 3,
				colorDark: '#000000',
				colorLight: '#ffffff',
			});
		};

		watch(
			() => state.ruleForm.qrcode,
			() => {
				if (state.ruleForm.qrcode) {
					state.ruleForm.isQuery = true;
				}
			},
			{ deep: true, immediate: true }
		);

		// 取消
		function onCancel() {
			closeDialog();
		}

		return {
			openDialog,
			closeDialog,
			onCancel,
			initQrcode,
			...toRefs(state),
		};
	},
});
</script>

<style lang="scss" scoped>
.record-query-material-info-container {
	.qrcode {
		margin: auto;
		width: 150px;
		height: 150px;
	}
}
</style>
