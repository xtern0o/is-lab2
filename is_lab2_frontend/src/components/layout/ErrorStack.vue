<script setup lang="ts">
import danger from '@/assets/img/danger-18465_256.gif'
import { closeError, errors } from '@/shared/errors'
</script>

<template>
  <div class="error-stack" aria-live="assertive">
    <button
      v-for="error in errors"
      :key="error.id"
      class="error-toast"
      type="button"
      role="alert"
      title="Закрыть"
      @click="closeError(error.id)"
    >
      <img :src="danger" alt="" />
      <span class="error-text">
        <strong>{{ error.title }}</strong>
        <span>{{ error.description }}</span>
      </span>
      <img :src="danger" alt="" />
    </button>
  </div>
</template>

<style scoped>
.error-stack {
  position: fixed;
  z-index: 100;
  top: 12px;
  left: 50%;
  width: min(460px, calc(100vw - 24px));
  max-height: calc(100vh - 24px);
  overflow-y: auto;
  display: grid;
  gap: 8px;
  transform: translateX(-50%);
  pointer-events: none;
}

.error-toast {
  width: 100%;
  padding: 7px 9px;
  display: grid;
  grid-template-columns: 38px 1fr 38px;
  align-items: center;
  gap: 8px;
  border: 2px solid #b52c28;
  border-radius: 0;
  color: var(--ink);
  background: #ffd8cf;
  cursor: pointer;
  pointer-events: auto;
}

.error-toast:hover {
  background: #ffc9bd;
}

.error-toast img {
  width: 34px;
  height: 34px;
  object-fit: contain;
}

.error-text {
  min-width: 0;
  display: grid;
  gap: 3px;
  text-align: center;
  font-family: 'Courier New', monospace;
}

.error-text strong {
  color: #b22222;
  font-size: 12px;
}

.error-text span {
  font-size: 10px;
  line-height: 1.3;
}
</style>
