<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useBackgroundStore } from '@/stores/background'
import { onMounted } from 'vue'

const backgroundStore = useBackgroundStore()

onMounted(() => {
  backgroundStore.initBackground()
})
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
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  background-attachment: fixed;
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

/* 针对 Chrome / Edge / Safari */
::-webkit-scrollbar {
  width: 10px;
  /* 滚动条宽度 */
  height: 8px;
  /* 横向滚动条高度 */
}

::-webkit-scrollbar-track {
  background: transparent;
  /* 滚动条轨道透明 */
}

::-webkit-scrollbar-thumb {
  background-color: rgba(54, 52, 52, 0.3);
  /* 滚动条滑块半透明 */
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.5);
  /* 悬停时更明显 */
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

/* 毛玻璃材质效果 - 所有白色背景组件 */
.ant-card {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

.ant-form,
.ant-table-wrapper,
.ant-modal-content {
  background: rgba(255, 255, 255, 0.12) !important;
  backdrop-filter: blur(25px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border-radius: 16px;
}

/* 输入框毛玻璃效果 */
.ant-input,
.ant-select-selector,
.ant-textarea {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(15px) saturate(150%);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 8px;
  color: rgba(0, 0, 0, 0.85) !important;
}

.ant-input:hover,
.ant-select-selector:hover,
.ant-textarea:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.35);
}

.ant-input:focus,
.ant-select-focused .ant-select-selector,
.ant-textarea:focus {
  background: rgba(255, 255, 255, 0.25) !important;
  border-color: var(--c-cyan);
  box-shadow: 0 0 0 2px rgba(64, 224, 208, 0.2);
}

/* 输入框占位符文字 */
.ant-input::placeholder,
.ant-select-selector .ant-select-selection-placeholder {
  color: rgba(0, 0, 0, 0.45) !important;
}

/* 选择框内部文字 */
.ant-select-selection-item {
  color: rgba(0, 0, 0, 0.85) !important;
}

/* 清除按钮 */
.ant-input-clear-icon,
.ant-select-clear {
  color: rgba(0, 0, 0, 0.45) !important;
}

.ant-input-clear-icon:hover,
.ant-select-clear:hover {
  color: rgba(0, 0, 0, 0.65) !important;
}

/* 按钮毛玻璃效果 */
.ant-btn {
  backdrop-filter: blur(15px) saturate(150%);
  border-radius: 8px;
}

.ant-btn-primary {
  background: linear-gradient(135deg, var(--c-cyan), var(--c-blue)) !important;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 16px rgba(64, 224, 208, 0.3);
}

.ant-btn-default {
  background: rgba(255, 255, 255, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: rgba(0, 0, 0, 0.8);
}

.ant-btn-default:hover {
  background: rgba(255, 255, 255, 0.25) !important;
  border-color: rgba(255, 255, 255, 0.4);
}

/* 表格毛玻璃效果 */
.ant-table {
  background: transparent !important;
}

.ant-table-thead>tr>th {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.ant-table-tbody>tr>td {
  background: rgba(255, 255, 255, 0.08) !important;
  backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.ant-table-tbody>tr:hover>td {
  background: rgba(255, 255, 255, 0.15) !important;
}

/* 下拉菜单毛玻璃效果 */
.ant-select-dropdown,
.ant-dropdown-menu {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(25px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.ant-select-item {
  background: transparent !important;
}

.ant-select-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

.ant-select-item-option-selected {
  background: rgba(64, 224, 208, 0.2) !important;
}

/* 模态框毛玻璃效果 */
.ant-modal-content {
  background: rgba(255, 255, 255, 0.12) !important;
  backdrop-filter: blur(30px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 16px 64px rgba(0, 0, 0, 0.15);
}

.ant-modal-header {
  background: transparent !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.ant-modal-footer {
  background: transparent !important;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

/* 分页器毛玻璃效果 */
.ant-pagination {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.ant-pagination-item {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.ant-pagination-item:hover {
  background: rgba(255, 255, 255, 0.25) !important;
}

.ant-pagination-item-active {
  background: linear-gradient(135deg, var(--c-cyan), var(--c-blue)) !important;
  border-color: var(--c-cyan);
}

/* 消息提示毛玻璃效果 */
.ant-message-notice-content {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* 标签页毛玻璃效果 */
.ant-tabs-nav {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(15px);
  border-radius: 12px 12px 0 0;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-bottom: none;
}

.ant-tabs-tab {
  background: transparent !important;
}

.ant-tabs-tab:hover {
  background: rgba(255, 255, 255, 0.15) !important;
}

.ant-tabs-tab-active {
  background: rgba(255, 255, 255, 0.2) !important;
}

.ant-tabs-content-holder {
  background: rgba(255, 255, 255, 0.08) !important;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 0 0 12px 12px;
  border-top: none;
}
</style>
