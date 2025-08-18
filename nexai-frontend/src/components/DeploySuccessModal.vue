<template>
  <div>
    <!-- 使用自定义样式的模态框 -->
    <a-modal v-model:open="visible" title="部署成功" :footer="null" width="600px" :dialogStyle="{
      background: 'rgba(255, 255, 255, 0.12)',
      backdropFilter: 'blur(30px) saturate(180%)',
      WebkitBackdropFilter: 'blur(30px) saturate(180%)',
      border: '1px solid rgba(255, 255, 255, 0.2)',
      boxShadow: '0 16px 64px rgba(0, 0, 0, 0.15)',
      borderRadius: '16px'
    }" :maskStyle="{
  background: 'rgba(0, 0, 0, 0.45)',
  backdropFilter: 'blur(8px)'
}" :bodyStyle="{
  padding: '0'
}" destroyOnClose>
      <div class="deploy-success">
        <div class="success-icon">
          <CheckCircleOutlined style="color: #52c41a; font-size: 48px" />
        </div>
        <h3>网站已成功部署</h3>
        <p>你的网站已经成功部署，可以通过以下链接访问：</p>
        <div class="deploy-url">
          <a-input :value="deployUrl" readonly>
            <template #suffix>
              <a-button type="text" @click="handleCopyUrl">
                <CopyOutlined />
              </a-button>
            </template>
          </a-input>
        </div>
        <div class="deploy-actions">
          <a-button @click="handleOpenSite">访问网站</a-button>
          <a-button @click="handleCancelDeploy">取消部署</a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { CheckCircleOutlined, CopyOutlined } from '@ant-design/icons-vue'

interface Props {
  open: boolean
  deployUrl: string
}

interface Emits {
  (e: 'update:open', value: boolean): void
  (e: 'open-site'): void
  (e: 'cancel-deploy'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value),
})

// 监听open属性变化，确保样式立即应用
watch(() => props.open, (newVal) => {
  if (newVal) {
    // 强制应用样式
    setTimeout(() => {
      const modalContent = document.querySelector('.ant-modal-content')
      if (modalContent) {
        modalContent.setAttribute('style', `
          background: rgba(255, 255, 255, 0.12) !important;
          backdrop-filter: blur(30px) saturate(180%) !important;
          -webkit-backdrop-filter: blur(30px) saturate(180%) !important;
          border: 1px solid rgba(255, 255, 255, 0.2) !important;
          box-shadow: 0 16px 64px rgba(0, 0, 0, 0.15) !important;
          transition: none !important;
          animation: none !important;
        `)
      }
    }, 0)
  }
})

const handleCopyUrl = async () => {
  try {
    await navigator.clipboard.writeText(props.deployUrl)
    message.success('链接已复制到剪贴板')
  } catch (error) {
    console.error('复制失败：', error)
    message.error('复制失败')
  }
}

const handleOpenSite = () => {
  emit('open-site')
}

const handleCancelDeploy = () => {
  emit('cancel-deploy')
  visible.value = false
}

// 在组件挂载时添加全局样式
onMounted(() => {
  // 添加全局样式覆盖默认的模态框动画
  const styleId = 'deploy-modal-style'
  if (!document.getElementById(styleId)) {
    const style = document.createElement('style')
    style.id = styleId
    style.innerHTML = `
      .ant-modal {
        animation: none !important;
        transition: none !important;
      }
      .ant-modal-mask {
        animation: none !important;
        transition: none !important;
      }
      .ant-modal-wrap {
        animation: none !important;
        transition: none !important;
      }
      .ant-modal-content {
        animation: none !important;
        transition: none !important;
      }
    `
    document.head.appendChild(style)
  }
})
</script>

<style scoped>
.deploy-success {
  text-align: center;
  padding: 24px;
}

.success-icon {
  margin-bottom: 16px;
}

.deploy-success h3 {
  margin: 0 0 16px;
  font-size: 20px;
  font-weight: 600;
}

.deploy-success p {
  margin: 0 0 24px;
  color: #666;
}

.deploy-url {
  margin-bottom: 24px;
}

.deploy-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>