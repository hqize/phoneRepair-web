import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

// Workaround for Edge + Vue Router 4.6+ beforeUnloadListener bug
// Edge fires focus when replaceState is called while page is hidden,
// causing the window to pop back after minimize
// See: https://github.com/vuejs/router/issues/2644
const isEdge = /Edg\//.test(navigator.userAgent)
if (isEdge) {
  const originalReplaceState = history.replaceState.bind(history)
  history.replaceState = function (...args: Parameters<typeof history.replaceState>) {
    if (document.visibilityState === 'hidden') return
    return originalReplaceState.apply(this, args)
  }
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      component: DefaultLayout,
      redirect: '/reception',
      children: [
        {
          path: 'reception',
          name: 'Reception',
          component: () => import('@/views/ReceptionView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'repair-management',
          name: 'RepairManagement',
          component: () => import('@/views/RepairManagementView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'parts',
          name: 'Parts',
          component: () => import('@/views/PartsView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/UserManagementView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'suppliers',
          name: 'Suppliers',
          component: () => import('@/views/SupplierView.vue'),
          meta: { requiresAuth: true },
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return '/login'
  }
  if (to.meta.guest && userStore.isLoggedIn) {
    return '/reception'
  }
})

export default router
