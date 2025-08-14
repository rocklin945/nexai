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
</style>
