<template>
	<div class="chart-scrollbar layout-view-bg-white" :style="{ height: `calc(100vh - ${initTagViewHeight}` }">
		<div class="chart-warp">
			<div class="chart-warp-top">
				<ChartHead />
			</div>
			<div class="chart-warp-bottom">
				<!-- 左边 -->
				<div class="big-data-down-left">
					<!-- <div class="flex-warp-item">
						<div class="flex-warp-item-box">
							<div class="flex-title">天气预报</div>
							<div class="flex-content">
								<div class="sky">
									<SvgIcon name="ele-Sunny" class="sky-left" />
									<div class="sky-center">
										<div class="mb2">
											<span>多云转晴</span>
											<span>东南风</span>
											<span class="span ml5">良</span>
										</div>
									</div>
									<div class="sky-right">
										<span>25</span>
										<span>°C</span>
									</div>
								</div>
								<div class="sky-dd">
									<div class="sky-dl" v-for="(v, k) in skyList" :key="k" :class="{ 'sky-dl-first': k === 1 }">
										<div>{{ v.v1 }}</div>
										<div v-if="v.type === 'title'">{{ v.v2 }}</div>
										<div v-else>
											<SvgIcon :name="v.v2" />
										</div>
										<div>{{ v.v3 }}</div>
										<div class="tip">{{ v.v5 }}</div>
										<div>{{ v.v7 }}</div>
									</div>
								</div>
							</div>
						</div>
					</div> -->
					<div class="flex-warp-item">
						<div class="flex-warp-item-box">
							<div class="flex-title">出入库记录总数</div>
							<div class="flex-content">
								<div style="height: 100%" ref="chartsWarningRef"></div>
							</div>
						</div>
					</div>
				</div>

				<!-- 中间 -->
				<div class="big-data-down-center">
					<div class="big-data-down-center-one">
						<div class="big-data-down-center-one-content">
							<div style="height: 100%" ref="chartsCenterOneRef"></div>
						</div>
					</div>
					<div class="big-data-down-center-two">
						<div class="flex-warp-item-box">
							<div class="flex-title">
								<span>当前库存容量</span>
								<span class="flex-title-small">单位：容量</span>
							</div>
							<div class="flex-content">
								<div class="flex-content-right">
									<div style="height: 100%" ref="chartsMonitorRef"></div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- 右边 -->
				<div class="big-data-down-right">
					<div class="flex-warp-item">
						<div class="flex-warp-item-box">
							<div class="flex-title">
								<span>出入库物料趋势</span>
								<span class="flex-title-small">单位：容量</span>
							</div>
							<div class="flex-content">
								<div style="height: 100%" ref="chartsSevenDaysRef"></div>
							</div>
						</div>
					</div>
					<div class="flex-warp-item">
						<div class="flex-warp-item-box">
							<div class="flex-title">当前仓库容量</div>
							<div class="flex-content">
								<div class="progress">
									<template v-for="item in depotTotal.proportion" :key="item.key">
										<div class="progress-item">
											<span>{{ item.name }}</span>
											<div class="progress-box">
												<el-progress :percentage="item.percentage" color="#43bdf0"></el-progress>
											</div>
										</div>
									</template>
								</div>
							</div>
						</div>
					</div>
					<div class="flex-warp-item">
						<div class="flex-warp-item-box">
							<div class="flex-title">
								<span>各仓库当前容量</span>
								<span class="flex-title-small">单位：容量</span>
							</div>
							<div class="flex-content">
								<div style="height: 100%" ref="chartsInvestmentRef"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
import { toRefs, reactive, computed, onMounted, getCurrentInstance, watch, nextTick, onActivated, defineComponent } from 'vue';
import { useStore } from '/@/store/index';
import ChartHead from '/@/views/visualization/chart/head.vue';
import { StatisticAPI } from '/@/api/manager/material/statistic';
import * as echarts from 'echarts';
import 'echarts-wordcloud';
import { skyList, chartData4List } from '/@/views/visualization/chart/chart';
export default defineComponent({
	name: 'chartIndex',
	components: { ChartHead },
	setup() {
		const { proxy } = getCurrentInstance() as any;
		const store = useStore();
		const state = reactive({
			skyList,
			chartData4List,
			myCharts: [],
			stockTotal: {
				applyEnterTotal: '',
				applyOutTotal: '',
				materialTotal: '',
				temporaryEnterTotal: '',
				temporaryOutTotal: '',
			},
			recordTotal: {
				total: '', // 出入库总数
				outerSize: '', //  出库记录总数
				enterSize: '', // 入库记录总数
				enterNoApprove: '', // 入库未审批
				outerNoApprove: '', // 出库未审批
			},
			typeTotal: {
				number: [],
				typeName: [],
			},
			recordTypeTotal: {
				outerNumber: [],
				enterNumber: [],
				materialName: [],
			},
			depotTotal: {
				nameZhList: [],
				nameList: [],
				maxCapacityList: [],
				thresholdList: [],
				capacityList: [],
				proportion: [],
			},
			loading: false,
		});

		// 设置主内容的高度
		const initTagViewHeight = computed(() => {
			let { isTagsview } = store.state.themeConfig.themeConfig;
			let { isTagsViewCurrenFull } = store.state.tagsViewRoutes;
			if (isTagsViewCurrenFull) {
				return `30px`;
			} else {
				if (isTagsview) return `114px`;
				else return `80px`;
			}
		});

		// 初始化中间图表1
		const initChartsCenterOne = () => {
			const myChart = echarts.init(proxy.$refs.chartsCenterOneRef);
			const nameArray = [];
			if (state.typeTotal.typeName) {
				for (let index = 0; index < state.typeTotal.typeName.length; index++) {
					nameArray.push({
						name: state.typeTotal.typeName[index],
						value: state.typeTotal.number[index],
					});
				}
			}
			const option = {
				grid: {
					top: 15,
					right: 15,
					bottom: 20,
					left: 30,
				},
				tooltip: {},
				series: [
					{
						type: 'wordCloud',
						sizeRange: [12, 40],
						rotationRange: [0, 0],
						rotationStep: 45,
						gridSize: Math.random() * 20 + 5,
						shape: 'circle',
						width: '100%',
						height: '100%',
						textStyle: {
							fontFamily: 'sans-serif',
							fontWeight: 'bold',
							color: function () {
								return `rgb(${[Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(',')})`;
							},
						},
						data: nameArray,
					},
				],
			};
			myChart.setOption(option);
			(<any>state.myCharts).push(myChart);
		};

		// 初始化出入库物料类型趋势
		const initChartsSevenDays = () => {
			const myChart = echarts.init(proxy.$refs.chartsSevenDaysRef);
			const option = {
				grid: {
					top: 15,
					right: 15,
					bottom: 20,
					left: 30,
				},
				tooltip: {
					trigger: 'axis',
				},
				xAxis: {
					type: 'category',
					boundaryGap: false,
					data: state.recordTypeTotal.materialName,
				},
				yAxis: {
					type: 'value',
				},
				series: [
					{
						name: '入库记录',
						type: 'line',
						stack: '总量',
						data: state.recordTypeTotal.enterNumber,
					},
					{
						name: '出库记录',
						type: 'line',
						stack: '总量',
						data: state.recordTypeTotal.outerNumber,
					},
				],
			};
			myChart.setOption(option);
			(<any>state.myCharts).push(myChart);
		};

		// 初始化出入库记录总数
		const initChartsWarning = () => {
			const myChart = echarts.init(proxy.$refs.chartsWarningRef);
			const option = {
				grid: {
					top: 50,
					right: 20,
					bottom: 30,
					left: 30,
				},
				tooltip: {
					trigger: 'item',
				},
				series: [
					{
						name: '面积模式',
						type: 'pie',
						radius: [20, 50],
						center: ['50%', '50%'],
						roseType: 'area',
						itemStyle: {
							borderRadius: 8,
						},
						data: [
							{ value: state.recordTotal.enterNoApprove, name: '待审批入库记录' },
							{ value: state.recordTotal.outerNoApprove, name: '待审批出库记录' },
							{ value: state.recordTotal.outerSize, name: '出库记录总数' },
							{ value: state.recordTotal.enterSize, name: '入库记录总数' },
						],
					},
				],
			};
			myChart.setOption(option);
			(<any>state.myCharts).push(myChart);
		};

		// 初始化当前库存容量
		const initChartsMonitor = () => {
			const myChart = echarts.init(proxy.$refs.chartsMonitorRef);
			const option = {
				grid: {
					top: 15,
					right: 15,
					bottom: 20,
					left: 30,
				},
				tooltip: {
					trigger: 'axis',
				},
				xAxis: {
					type: 'category',
					boundaryGap: false,
					data: state.typeTotal.typeName,
				},
				yAxis: {
					type: 'value',
				},
				series: [
					{
						itemStyle: {
							color: '#289df5',
							borderColor: '#289df5',
							areaStyle: {
								type: 'default',
								opacity: 0.1,
							},
						},
						data: state.typeTotal.number,
						type: 'line',
						areaStyle: {},
					},
				],
			};
			myChart.setOption(option);
			(<any>state.myCharts).push(myChart);
		};

		// 初始化仓库容量
		const initChartsInvestment = () => {
			const myChart = echarts.init(proxy.$refs.chartsInvestmentRef);
			const option = {
				grid: {
					top: 15,
					right: 15,
					bottom: 20,
					left: 30,
				},
				tooltip: {
					trigger: 'axis',
				},
				xAxis: {
					type: 'category',
					data: state.depotTotal.nameZhList,
				},
				yAxis: {
					type: 'value',
				},
				series: [
					{
						data: state.depotTotal.capacityList,
						type: 'bar',
					},
				],
			};
			myChart.setOption(option);
			(<any>state.myCharts).push(myChart);
		};

		// 批量设置 echarts resize
		const initEchartsResizeFun = () => {
			nextTick(() => {
				for (let i = 0; i < state.myCharts.length; i++) {
					(<any>state.myCharts[i]).resize();
				}
			});
		};

		// 批量设置 echarts resize
		const initEchartsResize = () => {
			window.addEventListener('resize', initEchartsResizeFun);
		};

		// 初始化数据
		function initData() {
			if (!state.loading) {
				state.loading = true;
				StatisticAPI()
					.queryStockTotal()
					.then((res) => {
						let data: any = JSON.stringify(res.data);
						data = JSON.parse(data);
						if (data) {
							state.stockTotal = {
								applyEnterTotal: data.applyEnterTotal,
								applyOutTotal: data.applyOutTotal,
								materialTotal: data.materialTotal,
								temporaryEnterTotal: data.temporaryEnterTotal,
								temporaryOutTotal: data.temporaryOutTotal,
							};
						}
					});
				setTimeout(() => {
					StatisticAPI()
						.queryRecordTotal()
						.then((res) => {
							let data: any = JSON.stringify(res.data);
							data = JSON.parse(data);
							if (data) {
								state.recordTotal = {
									total: data.total,
									enterSize: data.enterSize,
									outerSize: data.outerSize,
									enterNoApprove: data.enterNoApprove,
									outerNoApprove: data.outerNoApprove,
								};
							}
						});
				}, 100);
				setTimeout(() => {
					StatisticAPI()
						.queryTypeStockTotal()
						.then((res) => {
							let data: any = JSON.stringify(res.data);
							data = JSON.parse(data);
							if (data) {
								state.typeTotal.number = data.number;
								state.typeTotal.typeName = data.typeName;
							}
						});
				}, 300);

				setTimeout(() => {
					StatisticAPI()
						.queryRecordTypeTotal()
						.then((res) => {
							let data: any = JSON.stringify(res.data);
							data = JSON.parse(data);
							if (data) {
								state.recordTypeTotal.enterNumber = data.enterNumber;
								state.recordTypeTotal.outerNumber = data.outerNumber;
								state.recordTypeTotal.materialName = data.typeName;
							}
						});
				}, 400);
				setTimeout(() => {
					StatisticAPI()
						.queryDepotTotal()
						.then((res) => {
							let data: any = JSON.stringify(res.data);
							data = JSON.parse(data);
							if (data) {
								state.depotTotal.nameList = data.nameList;
								state.depotTotal.nameZhList = data.nameZhList;
								state.depotTotal.maxCapacityList = data.maxCapacityList;
								state.depotTotal.capacityList = data.capacityList;
								state.depotTotal.thresholdList = data.thresholdList;
							}
							if (state.depotTotal.nameZhList.length > 0) {
								const array: any = [];
								for (let index = 0; index < state.depotTotal.nameList.length; index++) {
									array.push({
										key: index,
										name: state.depotTotal.nameZhList[index],
										percentage: numberDiv(state.depotTotal.capacityList[index], state.depotTotal.thresholdList[index]),
									});
								}
								state.depotTotal.proportion = array;
							}
						});
				}, 700);
				setTimeout(() => {
					state.loading = false;
				}, 1500);
			}
		}

		function numberDiv(arg1: any, arg2: any) {
			var t1 = 0,
				t2 = 0,
				r1,
				r2;
			try {
				t1 = arg1.toString().split('.')[1].length;
			} catch (e) {
				// console.log(e);
			}
			try {
				t2 = arg2.toString().split('.')[1].length;
			} catch (e) {
				// console.log(e);
			}
			r1 = Number(arg1.toString().replace('.', ''));
			r2 = Number(arg2.toString().replace('.', ''));
			//获取小数点后的计算值
			var result = ((r1 / r2) * Math.pow(10, t2 - t1)).toString();
			var result2 = parseFloat(result).toFixed(2);
			return Number(result2);
		}

		// 页面加载时
		onMounted(() => {
			initData();
			setTimeout(() => {
				initChartsCenterOne();
				initChartsSevenDays();
				initChartsWarning();
				initChartsMonitor();
				initChartsInvestment();
				initEchartsResize();
			}, 1200);
		});
		// 由于页面缓存原因，keep-alive
		onActivated(() => {
			initData();
			setTimeout(() => {
				initEchartsResizeFun();
			}, 1200);
		});
		// 监听 vuex 中的 tagsview 开启全屏变化，重新 resize 图表，防止不出现/大小不变等
		watch(
			() => store.state.tagsViewRoutes.isTagsViewCurrenFull,
			() => {
				initEchartsResizeFun();
			}
		);
		return {
			initTagViewHeight,
			...toRefs(state),
		};
	},
});
</script>

<style scoped lang="scss">
@import './chart.scss';
</style>
