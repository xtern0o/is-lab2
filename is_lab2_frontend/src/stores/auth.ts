import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const ACCESS_TOKEN = 'is_lab1_access_token'
const REFRESH_TOKEN = 'is_lab1_refresh_token'

export interface AuthTokens {
  accessToken: string
  refreshToken: string
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem(ACCESS_TOKEN))
  const refreshToken = ref(localStorage.getItem(REFRESH_TOKEN))

  const isAuthenticated = computed(() => Boolean(accessToken.value && refreshToken.value))
  const username = computed(() => {
    const payload = accessToken.value?.split('.')[1]
    if (!payload) return ''

    try {
      return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/'))).name ?? ''
    } catch {
      return ''
    }
  })

  function setTokens(access: string, refresh: string) {
    accessToken.value = access
    refreshToken.value = refresh
    localStorage.setItem(ACCESS_TOKEN, access)
    localStorage.setItem(REFRESH_TOKEN, refresh)
  }

  function logout() {
    accessToken.value = null
    refreshToken.value = null
    localStorage.removeItem(ACCESS_TOKEN)
    localStorage.removeItem(REFRESH_TOKEN)
  }

  return { accessToken, refreshToken, isAuthenticated, username, setTokens, logout }
})
