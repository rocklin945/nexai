<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { createApp, listCurUserAppPage, listGoodAppByPage } from '@/api/appController'
import { getGoodAppUserInfo } from '@/api/userController'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用户提示词
const userPrompt = ref('')
const creating = ref(false)

// 打字机效果相关
const placeholderText = ref('')
const isDeleting = ref(false)
const loopNum = ref(0)
const typingSpeed = ref(80)
const deletingSpeed = ref(65)
const pauseDelay = ref(2000)

// 预设的四个文案
const textArray = [
  '使用NEXAI创建一个活动宣传页面，活动内容是......',
  '使用NEXAI创建一个精美的网站，网站内容是......',
  '使用NEXAI创建一个产品原型，产品内容是......',
  '使用NEXAI创建一个好玩的小游戏，小游戏的规则是......'
]

// 打字机效果函数
const typeWriter = () => {
  const i = loopNum.value % textArray.length
  const fullText = textArray[i]

  if (isDeleting.value) {
    placeholderText.value = fullText.substring(0, placeholderText.value.length - 1)
  } else {
    placeholderText.value = fullText.substring(0, placeholderText.value.length + 1)
  }

  let delta = isDeleting.value ? deletingSpeed.value : typingSpeed.value

  if (!isDeleting.value && placeholderText.value === fullText) {
    // 完成打字，暂停一段时间后开始删除
    isDeleting.value = true
    delta = pauseDelay.value
  } else if (isDeleting.value && placeholderText.value === '') {
    // 完成删除，切换到下一个文本
    isDeleting.value = false
    loopNum.value++
  }

  setTimeout(typeWriter, delta)
}

// 我的应用数据
const myApps = ref<API.App[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 精选应用数据
const featuredApps = ref<API.App[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

//精选应用用户信息
const featuredAppUserInfo = ref<API.UserGoodAppVo[]>([])

const userMap = computed<Record<string, API.UserGoodAppVo>>(() => {
  const map: Record<string, API.UserGoodAppVo> = {}
  if (featuredAppUserInfo.value && featuredAppUserInfo.value.length > 0) {
    featuredAppUserInfo.value.forEach(user => {
      if (user && user.id) {
        // 确保使用正确的ID字段作为键
        const userId = String(user.id)
        map[userId] = user
      } else {
        console.warn('发现无效的用户信息数据：', user)
      }
    })
  } else {
    console.warn('featuredAppUserInfo为空，无法创建用户映射')
  }
  return map
})

// 设置提示词
const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

// 优化提示词功能已移除

// 创建应用
const generateApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.userId) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await createApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.statusCode === 200 && res.data.data) {
      message.success('应用创建成功')
      // 跳转到对话页面，确保ID是字符串类型
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.userId) {
    return
  }

  try {
    const res = await listCurUserAppPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.statusCode === 200 && res.data.data) {
      myApps.value = res.data.data.list || []
      myAppsPage.total = res.data.data.total || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

// 加载精选应用
const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.statusCode === 200 && res.data.data) {
      featuredApps.value = res.data.data.list || []
      featuredAppsPage.total = res.data.data.total || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

// 加载精选应用用户信息
const loadFeaturedAppUsers = async () => {
  try {
    if (!featuredApps.value || featuredApps.value.length === 0) {
      console.warn('没有精选应用数据，无法加载用户信息')
      return
    }
    const userIds = featuredApps.value.map((app) => app.userId).filter(id => id !== undefined)
    console.log('准备加载精选应用用户信息，用户ID列表：', userIds)
    if (userIds.length === 0) {
      console.warn('没有有效的用户ID，无法加载用户信息')
      return
    }
    const res = await getGoodAppUserInfo(userIds)
    if (res.data.statusCode === 200 && res.data.data) {
      featuredAppUserInfo.value = res.data.data
      console.log('用户信息数据已更新：', featuredAppUserInfo.value)
    } else {
      console.warn('用户信息API返回异常状态：', res.data.statusCode, res.data.message)
    }
  } catch (error) {
    console.error('加载精选应用用户信息失败：', error)
  }
}
watch(featuredApps, (newVal) => {
  if (newVal.length > 0) {
    loadFeaturedAppUsers()
  } else {
  }
})

// 查看对话
const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

// 查看作品
const viewWork = (app: API.App) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

// 格式化时间函数已移除，不再需要显示创建时间

// 页面加载时获取数据
onMounted(() => {
  loadMyApps()
  loadFeaturedApps()

  // 鼠标跟随光效
  const handleMouseMove = (e: MouseEvent) => {
    const { clientX, clientY } = e
    const { innerWidth, innerHeight } = window
    const x = (clientX / innerWidth) * 100
    const y = (clientY / innerHeight) * 100

    document.documentElement.style.setProperty('--mouse-x', `${x}%`)
    document.documentElement.style.setProperty('--mouse-y', `${y}%`)
  }

  document.addEventListener('mousemove', handleMouseMove)

  // 启动打字机效果
  nextTick(() => {
    setTimeout(typeWriter, 1000)
  })

  // 清理事件监听器
  return () => {
    document.removeEventListener('mousemove', handleMouseMove)
  }
})
</script>

<template>
  <div id="homePage">
    <div class="container">
      <!-- 网站标题和描述 -->
      <div class="hero-section">
        <h1 class="hero-title">
          <span class="glassmorphism-gradient-text" data-text="所念即所得">所念即所得</span>
        </h1>
        <p class="hero-description">一句话轻松创建网站应用</p>
      </div>

      <!-- 用户提示词输入框 -->
      <div class="input-section">
        <a-textarea v-model:value="userPrompt" @keydown.enter.prevent="generateApp" :placeholder="placeholderText"
          :rows="4" :maxlength="1000" class="prompt-input" />
        <div class="input-actions">
          <a-button class="circle-submit-btn" type="primary" size="large" @click="generateApp" :loading="creating">
            <template #icon>
              <span>↑</span>
            </template>
          </a-button>
        </div>
      </div>

      <!-- 快捷按钮 -->
      <div class="quick-actions">
        <a-button type="default" @click="
          setPrompt(
            '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
          )
          ">个人博客网站</a-button>
        <a-button type="default" @click="
          setPrompt(
            '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
          )
          ">企业官网</a-button>
        <a-button type="default" @click="
          setPrompt(
            '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
          )
          ">在线商城</a-button>
        <a-button type="default" @click="
          setPrompt(
            '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
          )
          ">作品展示网站</a-button>
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <AppCard v-for="app in myApps" :key="app.id" :app="app" :userName="loginUserStore.loginUser?.userName || ''"
            :userAvatar="loginUserStore.loginUser?.userAvatar || ''" @view-chat="viewChat" @view-work="viewWork" />
        </div>
        <div class="pagination-wrapper">
          <a-pagination v-model:current="myAppsPage.current" v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total" :show-size-changer="false" :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps" />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <template v-if="featuredApps?.length">
            <AppCard v-for="app in featuredApps" :key="app?.id" :app="app"
              :userName="app.userId && userMap[app.userId]?.userName || '未知用户'"
              :userAvatar="app.userId && userMap[app.userId]?.userAvatar || ''" :featured="true" @view-chat="viewChat"
              @view-work="viewWork" />
          </template>
          <div v-else class="no-data-message">
            暂无精选案例
          </div>
        </div>
        <div class="pagination-wrapper">
          <a-pagination v-model:current="featuredAppsPage.current" v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total" :show-size-changer="false" :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  background: transparent;
  position: relative;
}

.glassmorphism-gradient-text {
  position: relative;
  display: inline-block;
  font-weight: 700;
  font-size: 3.5rem;
  color: transparent;
  background: linear-gradient(120deg,
      #ffeaa7,
      /* 温暖黄 */
      #fab1a0,
      /* 珊瑚橙 */
      #fd79a8,
      /* 甜蜜粉 */
      #fdcb6e,
      /* 金橙色 */
      #6c5ce7,
      /* 柔紫色 */
      #a29bfe,
      /* 淡紫色 */
      #fd79a8,
      /* 甜蜜粉 */
      #00b894,
      /* 翡翠绿 */
      #00cec9,
      /* 青绿色 */
      #0984e3,
      /* 海洋蓝 */
      #ffeaa7
      /* 回到起点 */
    );
  background-size: 600% auto;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: dreamyFlow 12s ease-in-out infinite;
}

@keyframes dreamyFlow {

  0%,
  100% {
    background-position: 0% 50%;
    filter: saturate(1.1) brightness(1.1);
  }

  20% {
    background-position: 80% 20%;
    filter: saturate(1.3) brightness(1.2);
  }

  40% {
    background-position: 160% 80%;
    filter: saturate(0.9) brightness(1);
  }

  60% {
    background-position: 240% 30%;
    filter: saturate(1.2) brightness(1.15);
  }

  80% {
    background-position: 320% 70%;
    filter: saturate(1.4) brightness(1.1);
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 2;
  width: 100%;
  box-sizing: border-box;
}

/* 英雄区域 */
.hero-section {
  text-align: center;
  padding: 80px 0 60px;
  margin-bottom: 28px;
  color: #1e293b;
  position: relative;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  margin: 0 0 20px;
  line-height: 1.2;
  letter-spacing: -1px;
  position: relative;
  z-index: 2;
  display: inline-block;
}

/* 使用外部CSS中定义的毛玻璃渐变文字效果 */

@keyframes titleShimmer {

  0%,
  100% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }
}

.hero-description {
  font-size: 20px;
  margin: 0;
  opacity: 0.8;
  color: rgba(0, 0, 0, 0.7);
  position: relative;
  z-index: 2;
}

/* 输入区域 */
.input-section {
  position: relative;
  margin: 0 auto 24px;
  max-width: 800px;
}

.prompt-input {
  border-radius: 16px;
  border: none;
  font-size: 16px;
  padding: 20px 60px 20px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.prompt-input:focus {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.3);
  transform: translateY(-2px);
}

.input-actions {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.circle-submit-btn {
  border: 1px solid rgba(0, 0, 0, 0.5);
  color: rgba(0, 0, 0, 0.9);

  &:hover:not(:disabled) {
    color: rgba(0, 0, 0, 0.9);
    transform: scale(1.1) rotate(90deg);
    box-shadow: 0 8px 30px rgba(79, 70, 229, 0.6);
  }
}

/* 快捷按钮 */
.quick-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 60px;
  flex-wrap: wrap;
}

.quick-actions .ant-btn {
  border-radius: 25px;
  padding: 8px 20px;
  height: auto;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(59, 130, 246, 0.2);
  color: #475569;
  backdrop-filter: blur(15px);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.quick-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
  transition: left 0.5s;
}

.quick-actions .ant-btn:hover::before {
  left: 100%;
}

.quick-actions .ant-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(59, 130, 246, 0.4);
  color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
}

/* 区域标题 */
.section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #1e293b;
}

/* 我的作品网格 */
.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 精选案例网格 */
.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-description {
    font-size: 16px;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    justify-content: center;
  }
}
</style>