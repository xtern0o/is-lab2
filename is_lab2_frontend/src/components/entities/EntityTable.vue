<script setup lang="ts">
import { computed } from 'vue'

import danger from '@/assets/img/danger-18465_256.gif'
import type { ColumnDefinition, EntityRecord } from '@/domain/types'

const props = defineProps<{
  columns: readonly ColumnDefinition[]
  rows: EntityRecord[]
}>()

const emit = defineEmits<{ open: [id: string] }>()
const tableWidth = computed(() => `${Math.max(780, props.columns.length * 125)}px`)
</script>

<template>
  <div class="table-wrap">
    <table :style="{ minWidth: tableWidth }">
      <thead>
        <tr>
          <th v-for="column in columns" :key="column.key" scope="col">{{ column.label }}</th>
          <th class="action-cell" scope="col">ACTION</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row.id">
          <td v-for="column in columns" :key="column.key">{{ row[column.key] || '--' }}</td>
          <td class="action-cell">
            <button
              class="row-action"
              type="button"
              aria-label="open record"
              @click="emit('open', row.id)"
            >
              <img :src="danger" alt="" />
            </button>
          </td>
        </tr>
        <tr v-if="rows.length === 0">
          <td :colspan="columns.length + 1" class="empty-state">NO RECORDS FOUND</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.table-wrap {
  overflow-x: auto;
  border: 2px solid var(--ink);
}

table {
  width: 100%;
  border-collapse: collapse;
  background: #fff9e8;
  font:
    700 12px/1.25 'Courier New',
    monospace;
}

th,
td {
  padding: 13px 12px;
  text-align: left;
  border-right: 1px solid #a35237;
  border-bottom: 1px solid #a35237;
  white-space: nowrap;
}

th:last-child,
td:last-child {
  border-right: 0;
}

th {
  color: #ffedcb;
  background: var(--brick);
  font-size: 10px;
  letter-spacing: 0.05em;
}

tbody tr:nth-child(even) {
  background: #f6d9a4;
}

tbody tr:hover {
  color: #fff2cb;
  background: #cb5833;
}

.action-cell {
  width: 64px;
  padding: 4px 10px;
  text-align: center;
}

.row-action {
  width: 38px;
  height: 38px;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
  transition: transform 200ms ease;
}

.row-action:hover {
  transform: rotate(-90deg) scale(1.25);
}

.row-action img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: contain;
}

.empty-state {
  padding: 42px;
  text-align: center;
}
</style>
