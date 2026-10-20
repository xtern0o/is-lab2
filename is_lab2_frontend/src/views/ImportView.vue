<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'

import radioactive from '@/assets/img/radioactive-19946_256.gif'
import { apiGet, apiPostFile } from '@/config/api'
import { showError } from '@/shared/errors'
import { useAuthStore } from '@/stores/auth'
import { useDatabaseStore } from '@/stores/database'

type ImportStatus = 'STARTED' | 'SUCCESS' | 'FAILED'
type HistoryScope = 'mine' | 'all'

interface ImportHistory {
  id: string
  createdAt: string
  status: ImportStatus
  username: string
  moviesCount: number | null
  personsCount: number | null
  coordinatesCount: number | null
  locationsCount: number | null
}

const auth = useAuthStore()
const { importHistoryVersion } = storeToRefs(useDatabaseStore())
const file = ref<File | null>(null)
const importing = ref(false)
const result = ref<ImportHistory | null>(null)
const history = ref<ImportHistory[]>([])
const historyScope = ref<HistoryScope>('mine')
const historyPage = ref(0)
const historyPages = ref(0)
const historyLoading = ref(false)
const isAdmin = computed(() => auth.role === 'ADMIN')

const statusLabels: Record<ImportStatus, string> = {
  STARTED: 'в процессе',
  SUCCESS: 'успешно',
  FAILED: 'ошибка',
}

const dateFormatter = new Intl.DateTimeFormat('ru-RU', {
  dateStyle: 'short',
  timeStyle: 'short',
})

async function loadHistory() {
  try {
    historyLoading.value = true
    const path = historyScope.value === 'all' ? '/imports/history/all' : '/imports/history'
    const page = (await apiGet(`${path}?page=${historyPage.value}&size=8`)) as {
      content: ImportHistory[]
      totalPages: number
    }
    history.value = page.content
    historyPages.value = page.totalPages
  } catch (error) {
    showError(
      'не удалось загрузить историю :((',
      error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
    )
  } finally {
    historyLoading.value = false
  }
}

function selectHistory(scope: HistoryScope) {
  historyScope.value = scope
  historyPage.value = 0
  void loadHistory()
}

function changeHistoryPage(offset: number) {
  historyPage.value += offset
  void loadHistory()
}

function selectFile(event: Event) {
  file.value = (event.target as HTMLInputElement).files?.[0] ?? null
  result.value = null
}

async function importFile() {
  if (!file.value) return

  try {
    importing.value = true
    result.value = (await apiPostFile('/imports', file.value)) as ImportHistory
    historyPage.value = 0
    await loadHistory()
  } catch (error) {
    showError(
      'не удалось импортировать файл :((',
      error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
    )
  } finally {
    importing.value = false
  }
}

watch(importHistoryVersion, () => void loadHistory())
onMounted(loadHistory)
</script>

<template>
  <section class="import-view">
    <aside class="panel">
      <header>IMPORT HISTORY</header>

      <div class="history-tabs" role="tablist" aria-label="Import history scope">
        <button
          type="button"
          :class="{ active: historyScope === 'mine' }"
          @click="selectHistory('mine')"
        >
          моя история
        </button>
        <button
          v-if="isAdmin"
          type="button"
          :class="{ active: historyScope === 'all' }"
          @click="selectHistory('all')"
        >
          вся история
        </button>
      </div>

      <div class="history-list">
        <div v-if="historyLoading" class="empty-state">загрузка...</div>
        <div v-else-if="!history.length" class="empty-state">
          <strong>NO IMPORTS YET</strong>
          <span>операции импорта появятся здесь</span>
        </div>

        <article v-for="item in history" v-else :key="item.id" class="history-item">
          <div class="history-summary">
            <span class="status" :class="item.status.toLowerCase()">
              {{ statusLabels[item.status] }}
            </span>
            <time :datetime="item.createdAt">
              {{ dateFormatter.format(new Date(item.createdAt)) }}
            </time>
          </div>
          <div class="history-body">
            <div class="history-details">
              <strong>@{{ item.username }}</strong>
              <code>{{ item.id }}</code>

              <div v-if="item.status === 'SUCCESS'" class="history-counts">
                <span
                  ><b>{{ item.moviesCount }}</b> movies</span
                >
                <span
                  ><b>{{ item.personsCount }}</b> persons</span
                >
                <span
                  ><b>{{ item.coordinatesCount }}</b> coordinates</span
                >
                <span
                  ><b>{{ item.locationsCount }}</b> locations</span
                >
              </div>
              <small v-else-if="item.status === 'FAILED'">объекты не были созданы</small>
              <small v-else>файл обрабатывается</small>
            </div>
            <img class="history-gif" :src="radioactive" alt="" aria-hidden="true" />
          </div>
        </article>
      </div>

      <div class="history-pagination">
        <button
          class="text-button"
          type="button"
          :disabled="historyPage === 0"
          @click="changeHistoryPage(-1)"
        >
          &larr; prev
        </button>
        <span>
          Page: <strong>{{ String(historyPage + 1).padStart(2, '0') }}</strong
          >/{{ String(Math.max(historyPages, 1)).padStart(2, '0') }}
        </span>
        <button
          class="text-button"
          type="button"
          :disabled="historyPage + 1 >= historyPages"
          @click="changeHistoryPage(1)"
        >
          next &rarr;
        </button>
      </div>
    </aside>

    <form class="panel" @submit.prevent="importFile">
      <header>new HOCON (.conf) import</header>

      <div class="file-form">
        <details class="format-help">
          <summary>выберите <strong>.conf</strong> или <strong>.hocon</strong> файл.</summary>
          <p>
            <a
              href="https://github.com/lightbend/config/blob/main/HOCON.md"
              target="_blank"
              rel="noopener noreferrer"
            >
              подробнее о формате hocon
            </a>
          </p>
        </details>

        <input type="file" accept=".conf,.hocon" @change="selectFile" />
        <button
          class="xp-button xp-button--primary import-button"
          type="submit"
          :disabled="!file || importing"
        >
          {{ importing ? 'importing...' : 'import file' }}
        </button>

        <div v-if="result" class="import-result" role="status">
          <strong>import successful!!</strong>
          <span>movies: {{ result.moviesCount }}</span>
          <span>persons: {{ result.personsCount }}</span>
          <span>coordinates: {{ result.coordinatesCount }}</span>
          <span>locations: {{ result.locationsCount }}</span>
        </div>
      </div>
    </form>
  </section>
</template>

<style scoped>
.import-view {
  padding: 18px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.panel {
  min-height: 390px;
  display: flex;
  flex-direction: column;
  border: 2px solid var(--ink);
  background: #fff9e8;
}

.import-button {
  font-size: 14px;
}

header {
  padding: 10px;
  color: #ffedcb;
  background: var(--burgundy);
  font:
    800 10px/1 'Courier New',
    monospace;
}

.history-tabs {
  display: flex;
  border-bottom: 2px solid var(--ink);
}

.history-tabs button {
  flex: 1;
  padding: 9px;
  border: 0;
  border-right: 1px solid var(--ink);
  color: var(--brick);
  background: #f6d9a4;
  font:
    800 11px/1 'Courier New',
    monospace;
  cursor: pointer;
}

.history-tabs button:last-child {
  border-right: 0;
}

.history-tabs button.active {
  color: #ffedcb;
  background: var(--burgundy);
}

.history-list {
  min-height: 0;
  height: 430px;
  padding: 12px;
  display: grid;
  align-content: start;
  gap: 10px;
  overflow: auto;
}

.history-item {
  padding: 10px;
  display: grid;
  gap: 7px;
  border: 1px solid var(--ink);
  background: #fffdf3;
  font:
    700 11px/1.25 'Courier New',
    monospace;
}

.history-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 58px;
  align-items: start;
  gap: 10px;
}

.history-details {
  display: grid;
  gap: 7px;
}

.history-details > strong {
  color: var(--brick);
  font-size: 13px;
}

.history-gif {
  width: 58px;
  height: 58px;
  object-fit: contain;
}

.history-item code {
  overflow-wrap: anywhere;
  color: #805a49;
  font-size: 10px;
}

.history-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.history-summary time,
.history-item small {
  color: #805a49;
}

.status {
  padding: 4px 6px;
  border: 1px solid currentColor;
}

.status.success {
  color: #27622f;
  background: #dbedcb;
}

.status.failed {
  color: #8b2823;
  background: #f3c3aa;
}

.status.started {
  color: #7b5716;
  background: #f6dfa2;
}

.history-counts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px 12px;
}

.history-counts b {
  color: var(--brick);
}

.history-pagination {
  margin-top: auto;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 17px;
  border-top: 1px solid var(--ink);
  font:
    800 11px/1 'Courier New',
    monospace;
}

.history-pagination strong {
  padding: 2px 4px;
  color: #fff1ca;
  background: var(--brick);
  font-size: 14px;
}

.import-result {
  padding: 12px 16px;
  display: grid;
  grid-template-columns: repeat(2, auto);
  gap: 6px 20px;
  border: 2px solid var(--ink);
  background: #f6d9a4;
  text-align: left;
}

.import-result strong {
  grid-column: 1 / -1;
}

.empty-state,
.file-form {
  min-height: 350px;
  padding: 24px;
  display: grid;
  place-content: center;
  justify-items: center;
  gap: 14px;
  color: #805a49;
  text-align: center;
  font:
    700 14px/1.4 'Courier New',
    monospace;
}

.history-list > .empty-state {
  min-height: 280px;
}

.empty-state strong,
.file-form strong {
  color: var(--brick);
}

.file-form p {
  margin: 0;
}

.format-help {
  color: var(--brick);
  font-size: 13px;
}

.format-help summary {
  cursor: pointer;
  font-size: 14px;
  font-weight: 800;
  text-decoration: underline;
}

.format-help p {
  margin: 8px 0 0;
  font-size: 12px;
}

.format-help a {
  color: var(--burgundy);
}

@media (max-width: 760px) {
  .import-view {
    padding: 10px;
    grid-template-columns: 1fr;
  }

  .panel,
  .empty-state,
  .file-form {
    min-height: 180px;
  }
}
</style>
