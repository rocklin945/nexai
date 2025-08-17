<template>
  <a-layout-header class="header">
    <div class="header-container">
      <!-- 左侧：Logo和标题 -->
      <div class="header-left">
        <RouterLink to="/">
          <div class="logo-container">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">NEXAI</h1>
          </div>
        </RouterLink>
      </div>

      <!-- 移动端菜单按钮 -->
      <div class="mobile-menu-button" @click="toggleMobileMenu">
        <MenuOutlined v-if="!mobileMenuVisible" />
        <CloseOutlined v-else />
      </div>

      <!-- 中间：导航菜单 (PC端显示) -->
      <div class="header-center desktop-only">
        <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" :items="menuItems" @click="handleMenuClick" />
      </div>

      <!-- 右侧：用户操作区域 -->
      <div class="header-right">
        <div class="user-login-status">
          <div class="header-right-content">
            <a-button type="primary" @click="backgroundStore.toggleBackground()">
              切换背景
            </a-button>
            <div v-if="loginUserStore.loginUser.userId" class="user-info">
              <a-dropdown>
                <a-space>
                  <a-avatar :src="loginUserStore.loginUser.userAvatar">
                    {{ loginUserStore.loginUser?.userName?.charAt(0) || 'U' }}
                  </a-avatar>
                  <span class="user-name desktop-only">{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
                </a-space>
                <template #overlay>
                  <a-menu>
                    <a-menu-item class="username-menu-item">
                      <UserOutlined />
                      {{ loginUserStore.loginUser.userName ?? '无名' }}
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item @click="doLogout">
                      <LogoutOutlined />
                      退出登录
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
            <div v-else>
              <a-button type="primary" href="/user/login">登录</a-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 移动端菜单 (点击汉堡按钮后显示) -->
    <div class="mobile-menu" v-if="mobileMenuVisible">
      <a-menu v-model:selectedKeys="selectedKeys" mode="inline" :items="menuItems" @click="handleMobileMenuClick" />
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useBackgroundStore } from '@/stores/background.ts'
import { logout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined, PictureOutlined, MenuOutlined, CloseOutlined, UserOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const backgroundStore = useBackgroundStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 移动端菜单显示状态
const mobileMenuVisible = ref(false)

// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
  // 路由变化时关闭移动端菜单
  mobileMenuVisible.value = false
})

// 切换移动端菜单显示状态
const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: '/admin/chatManage',
    label: '对话管理',
    title: '对话管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/rocklin945/nexai', target: '_blank' }, '关于我'),
    title: '关于我',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 0) {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 处理移动端菜单点击
const handleMobileMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
  // 点击后关闭移动端菜单
  mobileMenuVisible.value = false
}

// 退出登录
const doLogout = async () => {
  const res = await logout();
  if (res.data.statusCode === 200) {
    // 清除本地 JWT token
    localStorage.removeItem('token');

    // 重置用户信息
    loginUserStore.setLoginUser({
      userName: '未登录',
    });

    message.success('退出登录成功');
    await router.push('/user/login');
  } else {
    message.error('退出登录失败，' + res.data.message);
  }
};

</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  width: 100%;
}

.header-left {
  flex: 0 0 200px;
  display: flex;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: flex-start;
  margin-left: 20px;
}

.header-center :deep(.ant-menu) {
  line-height: 64px;
  min-width: 400px;
}

.header-center :deep(.ant-menu-overflow-item) {
  padding: 0 15px;
}

.header-right {
  display: flex;
  justify-content: flex-end;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 30px;
  font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  font-weight: 650;
  letter-spacing: 1px;
  color: #222;
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent !important;
}

/* 登录按钮样式 */
.user-login-status :deep(.ant-btn-primary) {
  background: transparent !important;
  border: 1px solid rgba(0, 0, 0, 0.5) !important;
  color: #000 !important;
  font-weight: 500 !important;
  border-radius: 8px !important;
  backdrop-filter: blur(10px) !important;
  -webkit-backdrop-filter: blur(10px) !important;
  transition: all 0.3s ease !important;
}

.user-login-status :deep(.ant-btn-primary:hover) {
  background: rgba(0, 0, 0, 0.1) !important;
  border-color: rgba(0, 0, 0, 0.8) !important;
  color: #000 !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.user-login-status :deep(.ant-btn-primary:active) {
  background: rgba(0, 0, 0, 0.15) !important;
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
}

.user-login-status :deep(.ant-btn-primary:focus) {
  background: transparent !important;
  color: #000 !important;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1) !important;
}

/* 右侧内容布局 */
.header-right-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 8px;
}

.username-menu-item {
  font-weight: 500;
}

.username-menu-item:hover {
  background: transparent !important;
  cursor: default;
}


/* 移动端菜单按钮 */
.mobile-menu-button {
  display: none;
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.mobile-menu-button:hover {
  background: rgba(0, 0, 0, 0.05);
}

/* 移动端菜单 */
.mobile-menu {
  display: none;
  width: 100%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 16px 16px;
  padding: 10px;
  margin-top: 5px;
  overflow: hidden;
}

.mobile-menu :deep(.ant-menu) {
  background: transparent;
  border-right: none;
}

.mobile-menu :deep(.ant-menu-item) {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(25px) saturate(180%);
  -webkit-backdrop-filter: blur(25px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  margin: 8px 0;
  transition: all 0.3s ease;
}

.mobile-menu :deep(.ant-menu-item:hover) {
  background: rgba(255, 255, 255, 0.2);
}

.mobile-menu :deep(.ant-menu-item-selected) {
  background: rgba(64, 224, 208, 0.2) !important;
  border-color: rgba(64, 224, 208, 0.4);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .header {
    padding: 0 12px;
  }

  .header-container {
    padding: 10px 0;
  }

  .header-left {
    flex: 1;
  }
  
  /* 隐藏PC端菜单 */
  .desktop-only {
    display: none;
  }
  
  /* 显示移动端菜单按钮 */
  .mobile-menu-button {
    display: flex;
    align-items: center;
    margin-right: 10px;
  }
  
  /* 显示移动端菜单 */
  .mobile-menu {
    display: block;
  }
  
  .header-right {
    flex: 0 0 auto;
  }
  
  .header-right-content {
    justify-content: flex-end;
  }
  
  .logo {
    height: 36px;
    width: 36px;
  }
  
  .site-title {
    font-size: 24px;
  }
}
</style>
