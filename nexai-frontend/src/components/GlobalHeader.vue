<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">NEXAI</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" :items="menuItems" @click="handleMenuClick" />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <!-- 背景切换按钮和用户信息在同一行 -->
          <div class="header-right-content">
            <a-button type="primary" @click="backgroundStore.toggleBackground()">
              切换背景
            </a-button>
            <div v-if="loginUserStore.loginUser.userId">
              <a-dropdown>
                <a-space>
                  <a-avatar :src="loginUserStore.loginUser.userAvatar">
                    {{ loginUserStore.loginUser?.userName?.charAt(0) || 'U' }}
                  </a-avatar>
                  {{ loginUserStore.loginUser.userName ?? '无名' }}
                </a-space>
                <template #overlay>
                  <a-menu>
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
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useBackgroundStore } from '@/stores/background.ts'
import { logout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined, PictureOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const backgroundStore = useBackgroundStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

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

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 30px;
  font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  /* 优雅现代感字体 */
  font-weight: 650;
  /* 半粗体，更醒目 */
  letter-spacing: 1px;
  /* 增加字间距 */
  color: #222;
  /* 比纯黑柔和 */
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
</style>
