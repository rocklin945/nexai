<template>
  <div class="app-card" :class="{ 'app-card--featured': featured }" ref="tiltCard" @mousemove="handleMouseMove"
    @mouseleave="handleMouseLeave" @click="addRippleEffect">
    <!-- SVG滤镜库 -->
    <svg style="display: none">
      <filter id="glass-distortion-app" x="0%" y="0%" width="100%" height="100%" filterUnits="objectBoundingBox">
        <feTurbulence type="fractalNoise" baseFrequency="0.001 0.003" numOctaves="1" seed="23" result="turbulence" />
        <feComponentTransfer in="turbulence" result="mapped">
          <feFuncR type="gamma" amplitude="1" exponent="8" offset="0.3" />
          <feFuncG type="gamma" amplitude="0" exponent="1" offset="0" />
          <feFuncB type="gamma" amplitude="0" exponent="1" offset="0.3" />
        </feComponentTransfer>
        <feGaussianBlur in="turbulence" stdDeviation="2" result="softMap" />
        <feSpecularLighting in="softMap" surfaceScale="3" specularConstant="0.8" specularExponent="80"
          lighting-color="white" result="specLight">
          <fePointLight x="-150" y="-150" z="250" />
        </feSpecularLighting>
        <feComposite in="specLight" operator="arithmetic" k1="0" k2="1" k3="1" k4="0" result="litImage" />
        <feDisplacementMap in="SourceGraphic" in2="softMap" scale="120" xChannelSelector="R" yChannelSelector="G" />
      </filter>
    </svg>

    <!-- 毛玻璃效果层 -->
    <div class="glass-effect"></div>
    <div class="glass-tint"></div>
    <div class="glass-shine"></div>

    <!-- 应用内容 -->
    <div class="app-content">
      <div class="app-preview">
        <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
        <div v-else class="empty-cover">
          <a-empty description="暂无封面" :image-style="{ height: '50px', width: '80px' }" />
        </div>
        <div class="app-overlay">
          <a-space>
            <button class="glass-action-btn primary" @click.stop="handleViewChat">查看对话</button>
            <button v-if="app.deployKey" class="glass-action-btn secondary" @click.stop="handleViewWork">查看作品</button>
          </a-space>
        </div>
      </div>

      <div class="app-info">
        <div class="app-info-left">
          <div class="avatar-container">
            <a-avatar :src="userAvatar" :size="40">
              {{ userName?.charAt(0) || 'U' }}
            </a-avatar>
          </div>
        </div>
        <div class="app-info-right">
          <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
          <p class="app-author">
            {{ userName || (featured ? '官方' : '未知用户') }}
          </p>
        </div>
      </div>
    </div>

    <!-- 点击波纹效果 -->
    <div class="click-gradient" ref="ripple"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  app: API.App
  userName: string
  userAvatar: string
  featured?: boolean
}

interface Emits {
  (e: 'view-chat', appId: string | number | undefined): void
  (e: 'view-work', app: API.App): void
}

const props = withDefaults(defineProps<Props>(), {
  featured: false,
})

const emit = defineEmits<Emits>()

const tiltCard = ref<HTMLElement>()
const ripple = ref<HTMLElement>()

/**
 * 鼠标移动处理 - 3D倾斜效果
 */
const handleMouseMove = (e: MouseEvent) => {
  if (!tiltCard.value) return

  const card = tiltCard.value
  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2

  // 最大旋转角度
  const maxTilt = 35
  const rotateY = ((x - centerX) / centerX) * maxTilt
  const rotateX = -((y - centerY) / centerY) * maxTilt

  card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.02)`
}

/**
 * 鼠标离开处理
 */
const handleMouseLeave = () => {
  if (!tiltCard.value) return

  const card = tiltCard.value
  card.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1)'
}

/**
 * 添加波纹效果
 */
const addRippleEffect = (e: MouseEvent) => {
  if (!ripple.value || !tiltCard.value) return

  const rect = tiltCard.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top

  ripple.value.style.left = x + 'px'
  ripple.value.style.top = y + 'px'

  tiltCard.value.classList.add('clicked')

  setTimeout(() => {
    tiltCard.value?.classList.remove('clicked')
  }, 600)
}

const handleViewChat = () => {
  emit('view-chat', props.app.id)
}

const handleViewWork = () => {
  emit('view-work', props.app)
}
</script>

<style scoped lang="scss">
.app-card {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  will-change: transform;
  background: transparent;

  // 特色卡片样式与普通卡片保持一致
  &--featured {
    // 移除特殊样式，保持与普通卡片一致
  }
}

// 毛玻璃效果层
.glass-effect {
  position: absolute;
  inset: 0;
  z-index: 0;
  backdrop-filter: blur(20px) saturate(150%);
  filter: url(#glass-distortion-app);
  isolation: isolate;
  border-radius: 20px;
}

.glass-tint {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 20px;
}

.glass-shine {
  position: absolute;
  inset: 0;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow:
    inset 1px 1px 10px 0 rgba(255, 255, 255, 0.15),
    inset -1px -1px 6px 0 rgba(255, 255, 255, 0.05),
    0 4px 24px 0 rgba(0, 0, 0, 0.12);
  pointer-events: none;
}

// 应用内容
.app-content {
  position: relative;
  z-index: 3;
  height: 100%;
}

.app-preview {
  height: 180px;
  background: rgba(245, 245, 245, 0.15);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  border-radius: 20px 20px 0 0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }

  .empty-cover {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: rgba(0, 0, 0, 0.4);
  }
}

.app-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px 20px 0 0;
}

.app-card:hover .app-overlay {
  opacity: 1;
}

.app-card:hover .app-preview img {
  transform: scale(1.05);
}

// 毛玻璃按钮
.glass-action-btn {
  padding: 10px 18px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);

  &.primary {
    background: rgba(24, 144, 255, 0.25);
    color: white;

    &:hover {
      background: rgba(24, 144, 255, 0.35);
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
    }
  }

  &.secondary {
    background: rgba(255, 255, 255, 0.2);
    color: white;

    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
    }
  }
}

.app-info {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(5px);
}

.app-info-left {
  flex-shrink: 0;

  .avatar-container {
    position: relative;

    &::before {
      content: '';
      position: absolute;
      inset: -3px;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(64, 224, 208, 0.3), rgba(0, 191, 255, 0.3));
      z-index: -1;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }
}

.app-card:hover .avatar-container::before {
  opacity: 1;
}

.app-info-right {
  flex: 1;
  min-width: 0;
}

.app-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
  color: rgba(0, 0, 0, 0.85);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
  transition: color 0.3s ease;
}

.app-card:hover .app-title {
  color: rgba(0, 0, 0, 0.95);
}

.app-author {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.6);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.3);
  transition: color 0.3s ease;
}

.app-card:hover .app-author {
  color: rgba(0, 0, 0, 0.75);
}

// 点击波纹效果
.click-gradient {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle,
      rgba(64, 224, 208, 0.3) 0%,
      rgba(0, 191, 255, 0.2) 40%,
      rgba(100, 149, 237, 0.1) 70%,
      rgba(50, 50, 255, 0) 100%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  pointer-events: none;
  z-index: 4;
}

.app-card.clicked .click-gradient {
  animation: gradient-ripple 0.6s ease-out;
}

@keyframes gradient-ripple {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }

  100% {
    transform: translate(-50%, -50%) scale(2.5);
    opacity: 0;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .app-preview {
    height: 150px;
  }

  .app-info {
    padding: 14px 16px;
  }

  .app-title {
    font-size: 15px;
  }

  .app-author {
    font-size: 13px;
  }

  .glass-action-btn {
    padding: 8px 14px;
    font-size: 13px;
  }
}
</style>