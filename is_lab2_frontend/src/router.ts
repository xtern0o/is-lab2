import { createRouter, createWebHistory } from 'vue-router'

import { useAuthStore } from '@/stores/auth'
import AuthView from '@/views/AuthView.vue'
import DatabaseView from '@/views/DatabaseView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: DatabaseView,
      meta: { requiresAuth: true },
    },
    {
      path: '/auth',
      component: AuthView,
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/auth'
  }

  if (to.path === '/auth' && auth.isAuthenticated) {
    return '/'
  }
})
