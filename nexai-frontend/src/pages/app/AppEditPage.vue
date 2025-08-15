<template>
  <div id="appEditPage">
    <div class="page-header">
      <h1>编辑应用信息</h1>
    </div>

    <div class="edit-container">
      <a-card title="基本信息" :loading="loading">
        <a-form :model="formData" :rules="rules" layout="vertical" @finish="handleSubmit" ref="formRef">
          <a-form-item label="应用名称" name="appName">
            <a-input v-model:value="formData.appName" placeholder="请输入应用名称" :maxlength="50" show-count />
          </a-form-item>

          <a-form-item v-if="isAdmin" label="应用封面" name="cover" extra="支持图片链接，建议尺寸：400x300">
            <a-input v-model:value="formData.cover" placeholder="请输入封面图片链接" />
            <div v-if="formData.cover" class="cover-preview">
              <a-image :src="formData.cover" :width="200" :height="150"
                fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==" />
            </div>
          </a-form-item>

          <a-form-item v-if="isAdmin" label="优先级" name="priority" extra="设置为99表示精选应用">
            <a-input-number v-model:value="formData.priority" :min="0" :max="99" style="width: 200px" />
          </a-form-item>

          <a-form-item label="初始提示词" name="initPrompt">
            <a-textarea v-model:value="formData.initPrompt" placeholder="请输入初始提示词" :rows="4" :maxlength="1000" show-count
              disabled />
            <div class="form-tip">初始提示词不可修改</div>
          </a-form-item>

          <a-form-item label="生成类型" name="codeGenType">
            <a-input :value="formatCodeGenType(formData.codeGenType)" placeholder="生成类型" disabled />
            <div class="form-tip">生成类型不可修改</div>
          </a-form-item>

          <a-form-item v-if="formData.deployKey" label="部署密钥" name="deployKey">
            <a-input v-model:value="formData.deployKey" placeholder="部署密钥" disabled />
            <div class="form-tip">部署密钥不可修改</div>
          </a-form-item>

          <a-form-item>
            <a-space>
              <a-button style="border: 1px solid rgba(0, 0, 0, 0.4);" type="default" html-type="submit"
                :loading="submitting">
                保存修改
              </a-button>
              <a-button style="border: 1px solid rgba(0, 0, 0, 0.4);" @click="resetForm">重置</a-button>
              <a-button style="border: 1px solid rgba(0, 0, 0, 0.4);" type="link" @click="goToChat">进入对话</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <!-- 应用信息展示 -->
      <a-card title="应用信息" style="margin-top: 24px">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="应用ID">
            {{ appInfo?.id }}
          </a-descriptions-item>
          <a-descriptions-item label="创建者">
            <UserInfo :user="user" size="small" />
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">
            {{ formatTime(appInfo?.createTime) }}
          </a-descriptions-item>
          <a-descriptions-item label="更新时间">
            {{ formatTime(appInfo?.updateTime) }}
          </a-descriptions-item>
          <a-descriptions-item label="部署时间">
            {{ appInfo?.deployedTime ? formatTime(appInfo.deployedTime) : '未部署' }}
          </a-descriptions-item>
          <a-descriptions-item label="访问链接">
            <a-button v-if="appInfo?.deployKey" type="link" @click="openPreview" size="small">
              查看预览
            </a-button>
            <span v-else>未部署</span>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getAppById, updateApp, updateAppByAdmin } from '@/api/appController'
import { formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'
import { getStaticPreviewUrl } from '@/config/env'
import type { FormInstance } from 'ant-design-vue'
import { useEditAppStore } from '@/stores/editApp'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()
const editAppStore = useEditAppStore()
const user = computed(() => editAppStore.user)

// 应用信息
const appInfo = ref<API.App>()
const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive({
  appName: '',
  cover: '',
  priority: 0,
  initPrompt: '',
  codeGenType: '',
  deployKey: '',
})

// 是否为管理员
const isAdmin = computed(() => {
  return loginUserStore.loginUser.userRole === 0
})

// 表单验证规则
const rules = {
  appName: [
    { required: true, message: '请输入应用名称', trigger: 'blur' },
    { min: 1, max: 50, message: '应用名称长度在1-50个字符', trigger: 'blur' },
  ],
  cover: [{ type: 'url', message: '请输入有效的URL', trigger: 'blur' }],
  priority: [{ type: 'number', min: 0, max: 99, message: '优先级范围0-99', trigger: 'blur' }],
}

// 获取应用信息
const fetchAppInfo = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('应用ID不存在')
    router.push('/')
    return
  }

  loading.value = true
  try {
    const res = await getAppById({ id: id as unknown as number })
    if (res.data.statusCode === 200 && res.data.data) {
      appInfo.value = res.data.data

      // 检查权限
      if (!isAdmin.value && appInfo.value.userId !== loginUserStore.loginUser.userId) {
        message.error('您没有权限编辑此应用')
        router.push('/')
        return
      }

      // 填充表单数据
      formData.appName = appInfo.value.appName || ''
      formData.cover = appInfo.value.cover || ''
      formData.priority = appInfo.value.priority || 0
      formData.initPrompt = appInfo.value.initPrompt || ''
      formData.codeGenType = appInfo.value.codeGenType || ''
      formData.deployKey = appInfo.value.deployKey || ''
    } else {
      message.error('获取应用信息失败')
      router.push('/')
    }
  } catch (error) {
    console.error('获取应用信息失败：', error)
    message.error('获取应用信息失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!appInfo.value?.id) return

  submitting.value = true
  try {
    let res
    if (isAdmin.value) {
      // 管理员可以修改更多字段
      res = await updateAppByAdmin({
        id: appInfo.value.id,
        appName: formData.appName,
        cover: formData.cover,
        priority: formData.priority,
      })
    } else {
      // 普通用户只能修改应用名称
      res = await updateApp({
        id: appInfo.value.id,
        appName: formData.appName,
      })
    }

    if (res.data.statusCode === 200) {
      message.success('修改成功')
      // 重新获取应用信息
      await fetchAppInfo()
      router.push('/admin/appManage')
    } else {
      message.error('修改失败：' + res.data.message)
    }
  } catch (error) {
    console.error('修改失败：', error)
    message.error('修改失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (appInfo.value) {
    formData.appName = appInfo.value.appName || ''
    formData.cover = appInfo.value.cover || ''
    formData.priority = appInfo.value.priority || 0
  }
  formRef.value?.clearValidate()
}

// 进入对话页面
const goToChat = () => {
  if (appInfo.value?.id) {
    router.push(`/app/chat/${appInfo.value.id}`)
  }
}

// 打开预览
const openPreview = () => {
  if (appInfo.value?.codeGenType && appInfo.value?.id) {
    const url = getStaticPreviewUrl(appInfo.value.codeGenType, String(appInfo.value.id))
    window.open(url, '_blank')
  }
}

// 页面加载时获取应用信息
onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
#appEditPage {
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;
  background: transparent;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.85);
}

.edit-container {
  border-radius: 8px;
}

/* 卡片毛玻璃效果 */
#appEditPage :deep(.ant-card) {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
}

/* 卡片头部 */
#appEditPage :deep(.ant-card-head) {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(15px) !important;
  -webkit-backdrop-filter: blur(15px) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 16px 16px 0 0 !important;
}

#appEditPage :deep(.ant-card-head-title) {
  color: rgba(0, 0, 0, 0.85) !important;
  font-weight: 600 !important;
}

#appEditPage :deep(.ant-form) {
  padding: 25px;
}

/* 毛玻璃只加在输入框主体，不影响按钮 */
#appEditPage :deep(.ant-input-affix-wrapper),
#appEditPage :deep(.ant-input),
#appEditPage :deep(.ant-input-number-input),
#appEditPage :deep(.ant-input-number) {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 8px !important;
  color: rgba(0, 0, 0, 0.9) !important;
  box-shadow: none !important;
  outline: none !important;
}

/* 保留加减按钮样式 */
#appEditPage :deep(.ant-input-number-handler-wrap) {
  backdrop-filter: none !important;
  background: transparent !important;
}

/* 输入框内部的实际输入元素 - 强制完全透明 */
#appEditPage :deep(.ant-input-affix-wrapper > input.ant-input),
#appEditPage :deep(.ant-input-affix-wrapper input),
#appEditPage :deep(.ant-input-group input),
#appEditPage :deep(.ant-input-affix-wrapper > .ant-input),
#appEditPage :deep(.ant-input-affix-wrapper .ant-input),
#appEditPage :deep(.ant-input-number-input) {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
  border: none;
  box-shadow: none !important;
  outline: none !important;
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}

/* 封面预览区域 */
.cover-preview {
  margin-top: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(15px) !important;
  -webkit-backdrop-filter: blur(15px) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 8px !important;
}

.form-tip {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45) !important;
  margin-top: 4px;
}

/* 禁用状态的输入框 */
#appEditPage :deep(.ant-input[disabled]),
#appEditPage :deep(.ant-input-affix-wrapper.ant-input-affix-wrapper-disabled) {
  background: rgba(0, 0, 0, 0.04) !important;
  backdrop-filter: blur(10px) !important;
  -webkit-backdrop-filter: blur(10px) !important;
  color: rgba(0, 0, 0, 0.5) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  cursor: not-allowed !important;
}
</style>
