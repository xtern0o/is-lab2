<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { apiPost } from '@/config/api'
import { showError } from '@/shared/errors'
import { useAuthStore, type AuthTokens } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const mode = ref<'login' | 'register'>('login')
const name = ref('')
const password = ref('')
const repeatedPassword = ref('')
const submitting = ref(false)

async function submit() {
  try {
    submitting.value = true

    const path = mode.value === 'login' ? '/auth/login' : '/auth/register'
    const body =
      mode.value === 'login'
        ? { name: name.value, password: password.value }
        : { name: name.value, password: password.value, repeatedPassword: repeatedPassword.value }
    const tokens = (await apiPost(path, body)) as AuthTokens
    auth.setTokens(tokens.accessToken, tokens.refreshToken)

    await router.push('/')
  } catch (error) {
    showError(
      mode.value === 'login' ? 'не удалось войти :((' : 'не удалось зарегистрироваться :((',
      error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
    )
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-window">
      <header>авторизация</header>

      <div class="auth-tabs">
        <button
          class="xp-button"
          :class="{ 'xp-button--primary': mode === 'login' }"
          type="button"
          @click="mode = 'login'"
        >
          login
        </button>
        <button
          class="xp-button"
          :class="{ 'xp-button--primary': mode === 'register' }"
          type="button"
          @click="mode = 'register'"
        >
          register
        </button>
      </div>

      <form @submit.prevent="submit">
        <label>
          USERNAME
          <input v-model="name" autocomplete="username" required />
        </label>
        <label>
          PASSWORD
          <input
            v-model="password"
            :autocomplete="mode === 'login' ? 'current-password' : 'new-password'"
            type="password"
            minlength="6"
            required
          />
        </label>
        <label v-if="mode === 'register'">
          REPEAT PASSWORD
          <input
            v-model="repeatedPassword"
            autocomplete="new-password"
            type="password"
            minlength="6"
            required
          />
        </label>

        <button
          class="xp-button xp-button--primary submit-button"
          type="submit"
          :disabled="submitting"
        >
          {{ mode === 'login' ? 'войти' : 'зарегистрироваться' }}
        </button>
      </form>
    </div>
  </section>
</template>

<style scoped>
.auth-page {
  min-height: 420px;
  display: grid;
  place-items: center;
}

.auth-window {
  width: min(420px, 100%);
  border: 2px solid var(--ink);
  background: var(--paper);
}

.auth-window > header {
  padding: 9px 11px;
  color: #ffedcb;
  background: var(--burgundy);
  font:
    800 11px/1 'Courier New',
    monospace;
}

.auth-tabs {
  padding: 14px 14px 0;
  display: flex;
  gap: 8px;
}

form {
  padding: 14px;
  display: grid;
  gap: 13px;
}

label {
  display: grid;
  gap: 5px;
  color: var(--brick);
  font:
    800 10px/1 'Courier New',
    monospace;
}

input {
  width: 100%;
  padding: 9px 10px;
  border: 1px solid var(--ink);
  outline: none;
  background: #fffaf0;
  font:
    700 12px/1.2 'Courier New',
    monospace;
}

input:focus {
  box-shadow: 0 0 0 2px var(--yellow);
}

.submit-button {
  justify-self: end;
  margin-top: 3px;
}
</style>
