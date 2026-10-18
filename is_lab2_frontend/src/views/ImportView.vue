<script setup lang="ts">
import { ref } from 'vue'

import { apiPostFile } from '@/config/api'
import { showError } from '@/shared/errors'

interface ImportResult {
  moviesCount: number
  personsCount: number
  coordinatesCount: number
  locationsCount: number
}

const file = ref<File | null>(null)
const importing = ref(false)
const result = ref<ImportResult | null>(null)

function selectFile(event: Event) {
  file.value = (
    event.target as HTMLInputElement
  ).files?.[0] ?? null
  result.value = null
}

async function importFile() {
  if (!file.value) return

  try {
    importing.value = true
    result.value = (
      await apiPostFile('/imports', file.value)
    ) as ImportResult
  } catch (error) {
    showError(
      'не удалось импортировать файл :((',
      error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
    )
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <section class="import-view">
    <aside class="panel">
      <header>IMPORT HISTORY</header>
      <div class="empty-state">
        <strong>NO IMPORTS YET</strong>
        <span>completed operations will appear here</span>
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
          <strong>IMPORT COMPLETED</strong>
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
  grid-template-columns: 1fr 3fr;
  gap: 16px;
}

.panel {
  min-height: 390px;
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

.empty-state strong,
.file-form strong {
  color: var(--brick);
}

.file-form p {
  margin: 0;
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
</style>
