<script setup lang="ts">
import { onMounted, ref } from 'vue'

import danger from '@/assets/img/danger-18465_256.gif'
import EntityTable from '@/components/entities/EntityTable.vue'
import { apiGet, apiPatch } from '@/config/api'
import type { ColumnDefinition, EntityRecord } from '@/domain/types'
import { showError } from '@/shared/errors'

type Role = 'USER' | 'ADMIN'

const PAGE_SIZE = 10
const dangerIcons = Array.from({ length: 12 }, (_, index) => index)
const columns: readonly ColumnDefinition[] = [
  { key: 'id', label: 'ID', readonly: true },
  { key: 'name', label: 'NICKNAME' },
  { key: 'role', label: 'ROLE' },
]

const users = ref<EntityRecord[]>([])
const currentPage = ref(0)
const pageCount = ref(1)
const selectedUser = ref<EntityRecord | null>(null)
const selectedRole = ref<Role>('USER')
const saving = ref(false)

async function loadUsers() {
  try {
    const page = (await apiGet(
      `/users?page=${currentPage.value}&size=${PAGE_SIZE}&sort=name,asc`,
    )) as {
      content: Array<{ id: string; name: string; role: string }>
      totalPages: number
    }

    users.value = page.content
    pageCount.value = Math.max(page.totalPages, 1)
  } catch (error) {
    showError(
      'не удалось загрузить пользователей :((',
      error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
    )
  }
}

function changePage(offset: number) {
  currentPage.value += offset
  void loadUsers()
}

function openUser(id: string) {
  selectedUser.value = users.value.find((user) => user.id === id) ?? null
  if (selectedUser.value) selectedRole.value = selectedUser.value.role as Role
}

async function saveRole() {
  if (!selectedUser.value) return

  try {
    saving.value = true
    await apiPatch(`/users/${selectedUser.value.id}/role`, { role: selectedRole.value })
    selectedUser.value = null
    await loadUsers()
  } catch (error) {
    showError(
      'не удалось изменить роль :((',
      error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
    )
  } finally {
    saving.value = false
  }
}

onMounted(loadUsers)
</script>

<template>
  <section class="window" aria-labelledby="admin-title">
    <div class="window-titlebar">
      <div class="window-orb" aria-hidden="true"></div>
      <h1 id="admin-title">ADMIN · USERS</h1>
    </div>

    <div class="danger-strip" aria-hidden="true">
      <img v-for="icon in dangerIcons" :key="`top-${icon}`" :src="danger" alt="" />
    </div>

    <div class="admin-content">
      <EntityTable :columns="columns" :rows="users" @open="openUser" />

      <div class="pagination">
        <button
          class="text-button"
          type="button"
          :disabled="currentPage === 0"
          @click="changePage(-1)"
        >
          &larr; prev
        </button>
        <span>
          Page: <strong>{{ String(currentPage + 1).padStart(2, '0') }}</strong
          >/{{ String(pageCount).padStart(2, '0') }}
        </span>
        <button
          class="text-button"
          type="button"
          :disabled="currentPage + 1 >= pageCount"
          @click="changePage(1)"
        >
          next &rarr;
        </button>
      </div>
    </div>

    <div class="danger-strip danger-strip--bottom" aria-hidden="true">
      <img v-for="icon in dangerIcons" :key="`bottom-${icon}`" :src="danger" alt="" />
    </div>
  </section>

  <div v-if="selectedUser" class="dialog-overlay" @mousedown.self="selectedUser = null">
    <section class="role-dialog" role="dialog" aria-modal="true" aria-labelledby="role-dialog-title">
      <header class="dialog-header">
        <h2 id="role-dialog-title">USER RECORD // @{{ selectedUser.name }}</h2>
      </header>

      <form class="role-form" @submit.prevent="saveRole">
        <label>
          <span>ROLE</span>
          <select v-model="selectedRole">
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </label>

        <div class="dialog-actions">
          <button
            class="xp-button xp-button--primary"
            type="submit"
            :disabled="saving || selectedRole === selectedUser.role"
          >
            {{ saving ? 'saving...' : 'Save' }}
          </button>
          <button class="xp-button" type="button" @click="selectedUser = null">Exit</button>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.window {
  width: min(1000px, 92vw);
  min-height: 535px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  background: var(--paper);
  border: 2px solid var(--ink);
  box-shadow: 7px 7px 0 rgba(92, 25, 24, 0.42);
}

.window-titlebar {
  min-height: 36px;
  padding: 5px 7px;
  display: grid;
  grid-template-columns: 22px 1fr;
  align-items: center;
  gap: 9px;
  color: #ffedc8;
  background: var(--burgundy);
  border-bottom: 2px solid var(--ink);
}

.window-titlebar h1 {
  margin: 0;
  font: 800 13px/1 'Courier New', monospace;
  letter-spacing: 0.08em;
}

.window-orb {
  width: 19px;
  height: 19px;
  border: 2px solid #2c1110;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #fff2ab 0 18%, var(--orange) 19% 55%, #732019 56%);
}

.admin-content {
  padding: 18px;
}

.danger-strip {
  padding: 6px 10px;
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  place-items: center;
  gap: 6px;
  overflow: hidden;
}

.danger-strip--bottom {
  margin-top: auto;
}

.danger-strip img {
  width: min(100%, 52px);
  aspect-ratio: 1;
  object-fit: contain;
}

.pagination {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 22px;
  font: 700 11px/1 'Courier New', monospace;
  text-transform: uppercase;
}

.dialog-overlay {
  position: fixed;
  inset: 0;
  z-index: 20;
  padding: 24px;
  display: grid;
  place-items: center;
  background: rgba(60, 22, 19, 0.62);
}

.role-dialog {
  width: min(460px, 100%);
  border: 2px solid var(--ink);
  background: var(--paper);
  box-shadow: 7px 7px 0 rgba(60, 22, 19, 0.48);
}

.dialog-header {
  padding: 12px;
  color: #fff1ca;
  background: var(--burgundy);
  border-bottom: 2px solid var(--ink);
}

.dialog-header h2 {
  margin: 0;
  font: 800 11px/1.2 'Courier New', monospace;
}

.role-form {
  padding: 20px;
}

.role-form label {
  display: grid;
  gap: 7px;
  color: var(--brick);
  font: 800 10px/1 'Courier New', monospace;
}

.role-form select {
  padding: 10px;
  border: 2px solid var(--ink);
  background: #fffdf3;
  font: 700 12px/1 'Courier New', monospace;
}

.dialog-actions {
  margin-top: 22px;
  padding-top: 14px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px solid #b77855;
}
</style>
