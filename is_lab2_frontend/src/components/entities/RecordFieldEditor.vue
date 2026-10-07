<script setup lang="ts">
import type { ColumnDefinition } from '@/domain/types'

const props = defineProps<{
  fieldId: string
  column: ColumnDefinition
  modelValue: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  openRelation: []
}>()

function updateValue(event: Event) {
  emit('update:modelValue', (event.target as HTMLInputElement | HTMLSelectElement).value)
}
</script>

<template>
  <div class="record-field">
    <label :for="fieldId">{{ column.label }}</label>
    <div class="field-control">
      <select v-if="column.options" :id="fieldId" :value="modelValue" @change="updateValue">
        <option v-for="option in column.options" :key="option" :value="option">
          {{ option || '--- вы не выбрали(( ---' }}
        </option>
      </select>
      <input
        v-else
        :id="fieldId"
        :value="props.modelValue"
        type="text"
        :readonly="column.readonly"
        @input="updateValue"
      />
      <button
        class="xp-button xp-button--compact relation-button"
        v-if="column.relation && modelValue"
        type="button"
        title="Open related record"
        @click="emit('openRelation')"
      >
        open
      </button>
    </div>
  </div>
</template>

<style scoped>
.record-field {
  display: grid;
  gap: 5px;
}

label {
  color: var(--brick);
  font:
    800 10px/1 'Courier New',
    monospace;
}

.field-control {
  display: flex;
  align-items: stretch;
}

input,
select {
  width: 100%;
  min-width: 0;
  padding: 9px 10px;
  border: 1px solid var(--ink);
  border-radius: 0;
  outline: none;
  color: var(--ink);
  background: #fffaf0;
  font:
    700 12px/1.2 'Courier New',
    monospace;
}

input:focus,
select:focus {
  box-shadow: 0 0 0 2px var(--yellow);
}

input:read-only {
  color: #805a49;
  background: #ead8b8;
}

.relation-button {
  border-left: 0;
}
</style>
