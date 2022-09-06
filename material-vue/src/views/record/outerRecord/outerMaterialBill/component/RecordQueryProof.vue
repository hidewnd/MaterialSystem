<template>
	<div class="record-query-proof-container">
		<el-dialog title="查询出库凭证" v-model="isShowDialog" width="40%" draggable>
			<el-row :gutter="24">
				<el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
					<el-descriptions direction="vertical" style="width: 100%" :column="1" size="large" border>
						<el-descriptions-item label="出库记录编号" align="”center" autofocus="false">
							<el-input v-model="ruleForm.recordId" readonly autofocus="false" />
						</el-descriptions-item>
						<el-descriptions-item label="出库凭证" align="”center">
							<el-empty description="暂无数据" :image-size="10" v-if="!ruleForm.isQuery" />
							<template v-else>
								<el-input v-model="ruleForm.proof" type="textarea" autosize />
							</template>
						</el-descriptions-item>
					</el-descriptions>
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

interface State {
	isShowDialog: boolean;
	ruleForm: {
		recordId: string;
		proof: string;
		isQuery: boolean;
	};
}

export default defineComponent({
	name: 'RecordQueryProof',
	setup(props, context) {
		const { proxy } = getCurrentInstance() as any;
		const { t } = useI18n();
		const state = reactive<State>({
			isShowDialog: false,
			ruleForm: {
				recordId: '',
				proof: '',
				isQuery: false,
			},
		});

		// 打开弹窗
		function openDialog(recordId: string) {
			state.isShowDialog = true;
			state.ruleForm.recordId = recordId;
			initProof();
		}

		// 关闭弹窗
		function closeDialog() {
			state.ruleForm = {
				recordId: '',
				proof: '',
				isQuery: false,
			};
			state.isShowDialog = false;
		}

		function initProof() {
			if (state.ruleForm.recordId) {
				OuterRecordAPI()
					.getOuterProof(`recordId=${state.ruleForm.recordId}`)
					.then((res) => {
						let data = JSON.stringify(res.data);
						state.ruleForm.proof = JSON.parse(data);
					});
			}
		}

		// 初始化生成二维码
		const initQrcode = () => {
			proxy.$refs.qrcodeRef.innerHTML = '';
			new QRCode(proxy.$refs.qrcodeRef, {
				text: `${state.ruleForm.proof}`,
				width: 150,
				height: 150,
				colorDark: '#000000',
				colorLight: '#ffffff',
			});
		};

		watch(
			() => state.ruleForm.proof,
			() => {
				if (state.ruleForm.proof) {
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
.record-query-proof-container {
	.qrcode {
		margin: auto;
		width: 150px;
		height: 150px;
	}
}
</style>
