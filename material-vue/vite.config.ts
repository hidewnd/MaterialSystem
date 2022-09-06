import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import { defineConfig, loadEnv, ConfigEnv } from 'vite';

const pathResolve = (dir: string): any => {
	return resolve(__dirname, '.', dir);
};

const alias: Record<string, string> = {
	'/@': pathResolve('./src/'),
	'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js',
};

const viteConfig = defineConfig((mode: ConfigEnv) => {
	const env = loadEnv(mode.mode, process.cwd());
	return {
		plugins: [vue()],
		root: process.cwd(),
		resolve: { alias },
		base: mode.command === 'serve' ? './' : env.VITE_PUBLIC_PATH,
		optimizeDeps: {
			include: ['element-plus/lib/locale/lang/zh-cn', 'element-plus/lib/locale/lang/en', 'element-plus/lib/locale/lang/zh-tw'],
		},
		server: {
			host: '0.0.0.0',
			port: env.VITE_PORT as unknown as number,
			open: env.VITE_OPEN,
			proxy: {
				'/api#': {
					target: 'http://127.0.0.1:8080/',
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/api#/, ''),
				},
				'/api': {
					target: 'http://192.168.128.1:8001/',
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/api/, ''),
					
				},
				'/gitee': {
					target: 'https://gitee.com',
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/gitee/, ''),
				},
				'/sohu': {
					target: 'http://pv.sohu.com/',
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/sohu/, ''),
				},
			},
		},
		build: {
			outDir: 'dist',
			sourcemap: false,
			chunkSizeWarningLimit: 1500,
			rollupOptions: {
				output: {
					entryFileNames: `assets/[name].${new Date().getTime()}.js`,
					chunkFileNames: `assets/[name].${new Date().getTime()}.js`,
					assetFileNames: `assets/[name].${new Date().getTime()}.[ext]`,
					compact: true,
					manualChunks: {
						vue: ['vue', 'vue-router', 'vuex'],
						echarts: ['echarts'],
					},
				},
			},
			terserOptions: {
				compress: {
					drop_console: true,
					drop_debugger: true,
				},
				ie8: true,
				output: {
					comments: true,
				},
			},
		},
		css: {
			postcss: {
				plugins: [
					{
						postcssPlugin: 'internal:charset-removal',
						AtRule: {
							charset: (atRule) => {
								if (atRule.name === 'charset') {
									atRule.remove();
								}
							},
						},
					},
				],
			},
		},
		define: {
			__VUE_I18N_LEGACY_API__: JSON.stringify(false),
			__VUE_I18N_FULL_INSTALL__: JSON.stringify(false),
			__INTLIFY_PROD_DEVTOOLS__: JSON.stringify(false),
		},
	};
});

export default viteConfig;
