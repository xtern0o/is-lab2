<script setup lang="ts">
import moneySeparator from '@/assets/img/money-18548_256.gif'
import { RouterLink, useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  void router.replace('/auth')
}
</script>

<template>
  <header class="masthead">
    <div class="identity-bar">
      <p class="identity">Карнажицкий М. Р.</p>
      <div class="money-separator" aria-hidden="true">
        <img :src="moneySeparator" alt="" />
      </div>
      <RouterLink class="brand-lockup" to="/"><strong>is_lab1</strong></RouterLink>
      <div class="money-separator" aria-hidden="true">
        <img :src="moneySeparator" alt="" />
      </div>
      <div class="account-block">
        <div v-if="auth.isAuthenticated" class="account-user">
          <strong class="account-name">{{ auth.username || 'authorized' }}</strong>
          <button class="xp-button" type="button" @click="logout">выйти из аккаунта</button>
        </div>
        <button v-else class="xp-button" type="button" @click="router.push('/auth')">
          войти в аккаунт
        </button>
        <p class="variant">VARIANT: <strong>55995</strong></p>
      </div>
    </div>
  </header>
</template>

<style scoped>
.masthead {
  background: var(--cream);
}

.identity-bar {
  min-height: 88px;
  max-width: 1180px;
  margin: 0 auto;
  padding: 8px 28px;
  display: grid;
  grid-template-columns: 1fr 48px 1.1fr 48px 1fr;
  align-items: center;
  gap: 18px;
}

.identity,
.variant {
  margin: 0;
  font:
    700 13px/1.35 'Courier New',
    monospace;
  text-transform: uppercase;
}

.variant {
  text-align: right;
}

.variant strong {
  color: var(--brick);
  font-size: 15px;
}

.account-block {
  display: grid;
  justify-items: end;
  gap: 5px;
}

.account-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.account-name {
  color: var(--brick);
  font:
    800 14px/1.2 'Courier New',
    monospace;
}

.brand-lockup,
.brand-lockup:visited,
.brand-lockup:hover,
.brand-lockup:active {
  color: var(--ink);
  text-align: center;
  text-decoration: none;
}

.brand-lockup strong {
  display: block;
  font:
    900 32px/0.95 Georgia,
    serif;
  letter-spacing: -0.045em;
  text-shadow: 2px 2px 0 var(--yellow);
}

.money-separator {
  width: 46px;
  height: 46px;
}

.money-separator img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

@media (max-width: 760px) {
  .identity-bar {
    min-height: auto;
    padding: 10px 14px;
    grid-template-columns: 1fr 40px 1fr;
    gap: 10px;
  }

  .brand-lockup {
    grid-column: 1 / -1;
    grid-row: 1;
  }

  .brand-lockup strong {
    font-size: 30px;
  }

  .identity,
  .account-block {
    grid-row: 2;
    font-size: 10px;
  }

  .money-separator {
    grid-row: 2;
    width: 38px;
    height: 38px;
  }

  .money-separator:nth-of-type(3) {
    display: none;
  }
}
</style>
