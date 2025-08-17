import { defineStore } from 'pinia'
import { ref } from 'vue'
// 直接导入默认背景图，Vite会处理路径
import defaultBg from '@/assets/defaultBackground/background.png'

// 使用 import.meta.glob 动态导入所有随机背景图片
// { eager: true, as: 'url' } 会让 Vite 直接返回处理后的 URL 数组
const randomBgModules = import.meta.glob('@/assets/randomBackground/*.png', { eager: true, as: 'url' })
const RANDOM_BACKGROUNDS = Object.values(randomBgModules)

// 默认背景图路径现在是导入后的变量
const DEFAULT_BACKGROUND = defaultBg

export const useBackgroundStore = defineStore('background', () => {
  // 当前背景图路径
  const currentBackground = ref(DEFAULT_BACKGROUND)

  // 是否使用随机背景
  const isRandomBackground = ref(false)

  // 切换到默认背景
  function useDefaultBackground() {
    currentBackground.value = DEFAULT_BACKGROUND
    isRandomBackground.value = false
    applyBackground(currentBackground.value)
  }

  // 切换到随机背景
  function useRandomBackground() {
    if (RANDOM_BACKGROUNDS.length === 0) {
      return
    }
    const randomIndex = Math.floor(Math.random() * RANDOM_BACKGROUNDS.length)
    currentBackground.value = RANDOM_BACKGROUNDS[randomIndex]
    isRandomBackground.value = true
    applyBackground(currentBackground.value)
  }

  // 切换背景
  function toggleBackground() {
    if (isRandomBackground.value) {
      useDefaultBackground()
    } else {
      useRandomBackground()
    }
  }

  // 应用背景到HTML元素
  function applyBackground(backgroundPath: string) {
    document.documentElement.style.backgroundImage = `url(${backgroundPath})`
  }

  // 初始化背景
  function initBackground() {
    applyBackground(currentBackground.value)
  }

  return {
    currentBackground,
    isRandomBackground,
    useDefaultBackground,
    useRandomBackground,
    toggleBackground,
    initBackground
  }
})
