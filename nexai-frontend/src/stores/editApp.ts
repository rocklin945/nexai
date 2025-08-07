// stores/editApp.ts
import { defineStore } from 'pinia'

export const useEditAppStore = defineStore('editApp', {
  state: () => ({
    user: null as API.User,
  }),
  actions: {
    setUser(user: API.User) {
      this.user = user
    },
    clearUser() {
      this.user = null
    }
  }
})
