<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeIndex = computed(() => {
  const m: Record<string, string> = {
    '/reception': '1-1',
    '/repair-management': '2-1',
    '/parts': '3-1',
    '/users': '4-1',
    '/suppliers': '5-1',
  }
  return m[route.path] || '1-1'
})

const menuItems = [
  { index: '1-1', path: '/reception', label: '前台接待' },
  { index: '2-1', path: '/repair-management', label: '维修管理' },
  { index: '3-1', path: '/parts', label: '配件查询' },
  { index: '4-1', path: '/users', label: '账号管理' },
  { index: '5-1', path: '/suppliers', label: '供应商管理' },
]

function navigate(path: string) {
  router.push(path)
}

async function logout() {
  try {
    await ElMessageBox.confirm('你确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    userStore.clearUser()
    router.push('/login')
  } catch {
    // cancelled
  }
}
</script>

<template>
  <div class="app-wrapper">
    <el-menu
      :default-active="activeIndex"
      class="nav-menu"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.index"
        :index="item.index"
        @click="navigate(item.path)"
      >
        {{ item.label }}
      </el-menu-item>
      <el-menu-item index="6" @click="logout">退出登录</el-menu-item>
    </el-menu>

    <el-carousel :interval="5000" arrow="" height="450px" style="width: 100%; height: 450px">
      <el-carousel-item v-for="i in 4" :key="i">
        <img :src="`/static/img/brand${i}.jpg`" class="carousel-image" />
      </el-carousel-item>
    </el-carousel>

    <div class="page-body">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.app-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 50px;
}
.carousel-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}
.el-menu {
  border-radius: 4px;
}
</style>
