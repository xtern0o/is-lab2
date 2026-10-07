<script setup lang="ts">
import { RouterView } from 'vue-router'

import AppFooter from '@/components/layout/AppFooter.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import ErrorStack from '@/components/layout/ErrorStack.vue'
</script>

<template>
  <div class="site-shell">
    <AppHeader />
    <main class="workspace"><RouterView /></main>
    <AppFooter />
    <ErrorStack />
  </div>
</template>
