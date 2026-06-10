import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginUser } from '@/types/user'

const STORAGE_KEY = 'userInfo'

export const useUserStore = defineStore('user', () => {
  const user = ref<LoginUser | null>(null)

  const isLoggedIn = computed(() => user.value !== null)
  const userId = computed(() => user.value?.userId ?? 0)
  const roleId = computed(() => user.value?.roleId ?? 0)
  const isAdmin = computed(() => user.value?.roleId === 1)

  function restoreFromStorage() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (raw) {
        user.value = JSON.parse(raw)
      }
    } catch {
      user.value = null
    }
  }

  function setUser(u: LoginUser) {
    user.value = u
    localStorage.setItem(STORAGE_KEY, JSON.stringify(u))
  }

  function clearUser() {
    user.value = null
    localStorage.removeItem(STORAGE_KEY)
  }

  return { user, isLoggedIn, userId, roleId, isAdmin, restoreFromStorage, setUser, clearUser }
})
