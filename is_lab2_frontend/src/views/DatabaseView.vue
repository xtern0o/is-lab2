<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { onMounted, onUnmounted } from 'vue'

import EntityTable from '@/components/entities/EntityTable.vue'
import RecordDialog from '@/components/entities/RecordDialog.vue'
import { ENTITY_DEFINITIONS } from '@/domain/entitySchemas'
import type { DataTabId } from '@/domain/types'
import { useDatabaseStore } from '@/stores/database'
import SpecialOperationsView from './SpecialOperationsView.vue'

const entityTabs = Object.keys(ENTITY_DEFINITIONS) as DataTabId[]
const store = useDatabaseStore()
const {
  collections,
  activeTab,
  query,
  databaseQuery,
  currentPage,
  sortDescending,
  selectedEntityType,
  selectedRecordId,
  isCreating,
  activeDefinition,
  isOperations,
  windowTitle,
  windowStatus,
  databaseRows,
  visibleRows,
  pageCount,
} = storeToRefs(store)

const {
  selectTab,
  searchAll,
  clearSearch,
  changePage,
  toggleSort,
  openRecord,
  startCreateRecord,
  closeRecord,
  saveRecord,
  createRecord,
  deleteRecord,
  startUpdates,
  stopUpdates,
} = store

// ЖЦ SSE
onMounted(startUpdates)
onUnmounted(stopUpdates)

</script>

<template>
  <div class="database-view">
    <section class="window" aria-labelledby="window-title">
      <div class="window-titlebar">
        <div class="window-orb" aria-hidden="true"></div>
        <h1 id="window-title">{{ windowTitle }}</h1>
        <span>{{ windowStatus }}</span>
      </div>

      <nav class="tabs" aria-label="Database sections">
        <button
          v-for="tab in entityTabs"
          :key="tab"
          type="button"
          :class="{ active: activeTab === tab }"
          @click="selectTab(tab)"
        >
          {{ ENTITY_DEFINITIONS[tab].typeName }}
        </button>
        <button
          type="button"
          :class="{ active: activeTab === 'operations' }"
          @click="selectTab('operations')"
        >
          SPECIAL OPS
        </button>
      </nav>

      <SpecialOperationsView v-if="isOperations" />
      <div v-else class="database-content">
        <div class="toolbar">
          <form
            v-if="activeDefinition.searchableKeys.length"
            class="search-cluster"
            @submit.prevent="searchAll"
          >
            <label class="search-box">
              <span>FIND:</span>
              <input v-model="query" type="search" placeholder="filter this page..." />
            </label>
            <button class="xp-button" type="submit" :disabled="!query.trim()">SEARCH ALL</button>
          </form>
          <p v-else class="search-unavailable">no string fields to search</p>

          <div class="toolbar-actions">
            <button class="xp-button" type="button" @click="toggleSort">
              SORT: ID {{ sortDescending ? '↓' : '↑' }}
            </button>
            <button class="xp-button xp-button--primary" type="button" @click="startCreateRecord">
              + NEW RECORD
            </button>
          </div>
        </div>

        <div v-if="databaseQuery" class="search-results" role="status">
          <span>
            DATABASE RESULTS FOR: <strong>“{{ databaseQuery }}”</strong> ·
            {{ databaseRows.length }} FOUND
          </span>
          <button class="text-button" type="button" @click="clearSearch">CLEAR</button>
        </div>

        <EntityTable :columns="activeDefinition.columns" :rows="visibleRows" @open="openRecord" />
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
            next ->
          </button>
        </div>
      </div>
    </section>

    <RecordDialog
      v-if="selectedEntityType"
      :entity-type="selectedEntityType"
      :record-id="selectedRecordId"
      :mode="isCreating ? 'create' : 'edit'"
      :collections="collections"
      @close="closeRecord"
      @save="saveRecord"
      @create="createRecord"
      @delete="deleteRecord"
    />
  </div>
</template>

<style scoped>
.window {
  width: min(1320px, 92vw);
  min-height: 535px;
  margin: 0 auto;
  background: var(--paper);
  border: 2px solid var(--ink);
  box-shadow: 7px 7px 0 rgba(92, 25, 24, 0.42);
}

.window-titlebar {
  min-height: 36px;
  padding: 5px 7px;
  display: grid;
  grid-template-columns: 22px 1fr auto;
  align-items: center;
  gap: 9px;
  color: #ffedc8;
  background: var(--burgundy);
  border-bottom: 2px solid var(--ink);
}

.window-titlebar h1 {
  margin: 0;
  font:
    800 13px/1 'Courier New',
    monospace;
  letter-spacing: 0.08em;
}

.window-titlebar > span {
  font:
    700 10px/1 'Courier New',
    monospace;
  text-transform: uppercase;
}

.window-orb {
  width: 19px;
  height: 19px;
  border: 2px solid #2c1110;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #fff2ab 0 18%, var(--orange) 19% 55%, #732019 56%);
}

.database-content {
  padding: 18px;
}

.tabs {
  display: flex;
  overflow-x: auto;
  background: #df8a53;
  border-bottom: 2px solid var(--ink);
}

.tabs button {
  min-width: 120px;
  padding: 10px 18px;
  border: 0;
  border-right: 1px solid var(--ink);
  color: var(--ink);
  background: linear-gradient(#f2cb91, #daa064);
  box-shadow:
    inset 1px 1px #fff0cc,
    inset -1px -1px #a76848;
  font:
    800 12px/1 'Courier New',
    monospace;
  text-transform: uppercase;
  cursor: pointer;
}

.tabs button:hover {
  background: linear-gradient(#f8dba7, #e2ac70);
}

.tabs button.active {
  color: #fff1cb;
  background: linear-gradient(#b9422e, #8f2824);
  box-shadow:
    inset 1px 1px #d97850,
    inset -1px -1px #641e22;
}

.toolbar {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 8px;
}

.search-cluster {
  min-width: 0;
  display: flex;
  gap: 8px;
}

.search-unavailable {
  align-self: center;
  margin: 0;
  color: var(--brick);
  font:
    700 10px/1.4 'Courier New',
    monospace;
  text-transform: uppercase;
}

.search-box {
  min-width: min(430px, 100%);
  display: flex;
  align-items: center;
  border: 2px solid var(--ink);
  background: white;
}

.search-box span {
  align-self: stretch;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  color: #fff1ca;
  background: var(--brick);
  border-right: 2px solid var(--ink);
  font:
    800 11px/1 'Courier New',
    monospace;
}

.search-box input {
  min-width: 0;
  flex: 1;
  padding: 9px 11px;
  border: 0;
  outline: none;
  color: var(--ink);
  background: white;
  font:
    700 12px/1 'Courier New',
    monospace;
}

.search-box:focus-within {
  box-shadow: 0 0 0 3px var(--yellow);
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.search-results {
  margin: 0 0 11px;
  padding: 7px 9px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #fff1ca;
  background: var(--brick);
  border: 2px solid var(--ink);
  font:
    700 10px/1.3 'Courier New',
    monospace;
  text-transform: uppercase;
}

.pagination {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 17px;
  font:
    800 11px/1 'Courier New',
    monospace;
}

.pagination button:hover:not(:disabled) {
  color: var(--ink);
}

.pagination strong {
  padding: 2px 4px;
  color: #fff1ca;
  background: var(--brick);
  font-size: 14px;
}

@media (max-width: 760px) {
  .window {
    box-shadow: 6px 6px 0 rgba(92, 25, 24, 0.5);
  }

  .window-titlebar > span {
    display: none;
  }

  .toolbar,
  .search-cluster {
    align-items: stretch;
    flex-direction: column;
  }

  .search-box {
    min-width: 0;
  }

  .toolbar-actions button {
    flex: 1;
  }
}
</style>
