<template>
  <div class="login-container animated-background">
    <!-- SVG滤镜库 -->
    <svg style="display: none">
      <filter id="glass-distortion" x="0%" y="0%" width="100%" height="100%" filterUnits="objectBoundingBox">
        <feTurbulence type="fractalNoise" baseFrequency="0.001 0.005" numOctaves="1" seed="17" result="turbulence" />
        <feComponentTransfer in="turbulence" result="mapped">
          <feFuncR type="gamma" amplitude="1" exponent="10" offset="0.5" />
          <feFuncG type="gamma" amplitude="0" exponent="1" offset="0" />
          <feFuncB type="gamma" amplitude="0" exponent="1" offset="0.5" />
        </feComponentTransfer>
        <feGaussianBlur in="turbulence" stdDeviation="3" result="softMap" />
        <feSpecularLighting in="softMap" surfaceScale="5" specularConstant="1" specularExponent="100"
          lighting-color="white" result="specLight">
          <fePointLight x="-200" y="-200" z="300" />
        </feSpecularLighting>
        <feComposite in="specLight" operator="arithmetic" k1="0" k2="1" k3="1" k4="0" result="litImage" />
        <feDisplacementMap in="SourceGraphic" in2="softMap" scale="200" xChannelSelector="R" yChannelSelector="G" />
      </filter>
    </svg>

    <!-- 登录卡片 -->
    <div class="glass-component login-card" ref="tiltCard" @mousemove="handleMouseMove" @mouseleave="handleMouseLeave">
      <div class="glass-effect"></div>
      <div class="glass-tint"></div>
      <div class="glass-shine"></div>
      <div class="glass-content">
        <h2 class="login-title">NEXAI</h2>
        <div class="login-subtitle">不写一行代码，生成完整应用</div>
        <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit" class="login-form">
          <a-form-item name="userAccount" :rules="[
            { required: true, message: '请输入账号' },
            { min: 3, max: 12, message: '账号长度不能小于3位，不能大于12位' },
          ]">
            <input v-model="formState.userAccount" type="text" placeholder="请输入账号" class="glass-input" />
          </a-form-item>
          <a-form-item name="userPassword" :rules="[
            { required: true, message: '请输入密码' },
            { min: 8, max: 16, message: '密码长度不能小于8位，不能大于16位' },
          ]">
            <input v-model="formState.userPassword" type="password" placeholder="请输入密码" class="glass-input" />
          </a-form-item>
          <div class="tips">
            没有账号？
            <RouterLink to="/user/register" class="register-link">去注册</RouterLink>
          </div>
          <a-form-item>
            <button type="submit" class="glass-button" @click="addRippleEffect">
              登录
            </button>
          </a-form-item>
        </a-form>
      </div>
      <div class="click-gradient" ref="ripple"></div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { login } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

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
  const maxTilt = 18
  const rotateY = ((x - centerX) / centerX) * maxTilt
  const rotateX = -((y - centerY) / centerY) * maxTilt

  card.style.transform = `perspective(600px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.03)`
}

/**
 * 鼠标离开处理
 */
const handleMouseLeave = () => {
  if (!tiltCard.value) return

  const card = tiltCard.value
  card.style.transform = 'perspective(600px) rotateX(0deg) rotateY(0deg) scale(1)'
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

/**
 * 提交表单
 */
const handleSubmit = async (values: any) => {
  const res = await login(formState)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.statusCode === 200 && res.data.data) {
    const token = res.data.data.token;
    if (token) {
      localStorage.removeItem('token');
      localStorage.setItem('token', token);
    } else {
      message.error('登录失败，请检查用户名和密码')
    }
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    const redirect = route.query.redirect as string
    router.replace(redirect || '/')
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.animated-background {
  width: 100%;
  height: 100%;
  background: transparent;
}

.login-card {
  width: 420px;
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.10), 0 1.5px 6px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.6);
  cursor: pointer;
  background: transparent;
}

.glass-effect {
  position: absolute;
  inset: 0;
  z-index: 0;
  backdrop-filter: blur(25px) saturate(180%);
  filter: url(#glass-distortion);
  isolation: isolate;
  border-radius: 24px;
}

.glass-tint {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 24px;
}

.glass-shine {
  position: absolute;
  inset: 0;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 24px;
  box-shadow:
    inset 1px 1px 8px 0 rgba(255, 255, 255, 0.18),
    inset -1px -1px 8px 0 rgba(255, 255, 255, 0.08);
  pointer-events: none;
}

.glass-content {
  position: relative;
  z-index: 3;
  padding: 2.5rem;
  color: white;
}

.login-title {
  text-align: center;
  color: rgba(0, 0, 0, 0.8);
  margin-bottom: 0.5rem;
  font-size: 1.8rem;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: -0.5px;
}

.login-subtitle {
  text-align: center;
  color: rgba(0, 0, 0, 0.9);
  margin-bottom: 2rem;
  font-size: 0.9rem;
  font-weight: 400;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.login-form {
  .ant-form-item {
    margin-bottom: 1.5rem;
  }

  .ant-form-item-explain-error {
    color: rgba(255, 100, 100, 0.9);
    font-size: 0.85rem;
    margin-top: 0.5rem;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  }
}

.glass-input {
  width: 100%;
  padding: 14px 20px;
  border: none;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  color: rgba(0, 0, 0, 0.95);
  font-size: 1rem;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);

  &::placeholder {
    color: rgba(0, 0, 0, 0.5);
  }

  &:focus {
    outline: none;
    background: rgba(255, 255, 255, 0.25);
    border-color: rgba(64, 224, 208, 0.6);
    box-shadow:
      0 0 0 2px rgba(64, 224, 208, 0.2),
      inset 0 1px 3px rgba(0, 0, 0, 0.1);
    transform: translateY(-1px);
  }

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.3);
  }
}

.glass-button {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(64, 224, 208, 0.3), rgba(0, 191, 255, 0.3));
  color: rgba(0, 0, 0, 0.7);
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.25);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  box-shadow: 0 4px 15px rgba(64, 224, 208, 0.2);

  &:hover {
    background: linear-gradient(135deg, rgba(64, 224, 208, 0.4), rgba(0, 191, 255, 0.4));
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(64, 224, 208, 0.3);
    border-color: rgba(255, 255, 255, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.tips {
  text-align: center;
  color: rgba(0, 0, 0, 0.9);
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.register-link {
  color: rgba(55, 132, 219, 0.9);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover {
    color: rgba(64, 224, 208, 1);
    text-shadow: 0 0 8px rgba(64, 224, 208, 0.4);
  }
}

// 点击波纹效果
.click-gradient {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(64, 224, 208, 0.4) 0%, rgba(0, 191, 255, 0.2) 40%, rgba(100, 149, 237, 0.1) 70%, rgba(50, 50, 255, 0) 100%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  pointer-events: none;
  z-index: 4;
}

.glass-component.clicked .click-gradient {
  animation: gradient-ripple 0.6s ease-out;
}

@keyframes gradient-ripple {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }

  100% {
    transform: translate(-50%, -50%) scale(3);
    opacity: 0;
  }
}

.glass-component {
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform;
}

// 响应式设计
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    max-width: 380px;
  }

  .glass-content {
    padding: 2rem;
  }

  .login-title {
    font-size: 1.6rem;
  }
}
</style>
