<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  userStore.restoreFromStorage()
})
</script>

<template>
  <router-view />
</template>
