import { defineStore } from 'pinia'
import { ref } from 'vue'

// 默认背景图路径
const DEFAULT_BACKGROUND = '/src/assets/defaultBackground/background.png'

// 随机背景图路径数组
const RANDOM_BACKGROUNDS = [
  '/src/assets/randomBackground/background1.png',
  '/src/assets/randomBackground/background2.png',
  '/src/assets/randomBackground/background3.png',
  '/src/assets/randomBackground/background4.png',
  '/src/assets/randomBackground/background5.png',
  '/src/assets/randomBackground/background6.png',
  '/src/assets/randomBackground/background7.png',
  '/src/assets/randomBackground/background8.png',
  '/src/assets/randomBackground/background9.png',
  '/src/assets/randomBackground/background10.png',
  '/src/assets/randomBackground/background11.png',
]

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