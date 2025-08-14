<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
</script>

<template>
  <BasicLayout />
</template>

<style>
/* 全局样式优化 */
* {
  box-sizing: border-box;
}

html {
  overflow-x: hidden;
  background: url('@/assets/background.png') no-repeat center center fixed;
  background-size: cover;
  min-height: 100vh;
}

body {
  padding: 0;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell',
    'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow-x: hidden;
  background: transparent;
  min-height: 100vh;
}

#app {
  min-height: 100vh;
  background: transparent;
}

/* 全局透明背景设置 */
.ant-layout,
.ant-layout-content,
.ant-layout-header,
.ant-layout-footer {
  background: transparent !important;
}

/* 确保所有页面容器都是透明的 */
.page-container,
.content-wrapper,
.main-wrapper {
  background: transparent !important;
}

/* 表单和卡片组件保持半透明背景以提高可读性 */
.ant-card {
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
}

.ant-form,
.ant-table-wrapper,
.ant-modal-content {
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(15px);
}

/* 输入框等保持半透明 */
.ant-input,
.ant-select-selector,
.ant-textarea {
  background: rgba(255, 255, 255, 0.9) !important;
}
</style>
