<script setup lang="ts">
import { computed, ref, watch } from 'vue'

import { ENTITY_DEFINITIONS } from '@/domain/entitySchemas'
import { showError } from '@/shared/errors'
import type {
  ColumnDefinition,
  DataTabId,
  EntityCollections,
  EntityRecord,
  RecordCreation,
  RecordMutation,
} from '@/domain/types'
import RecordFieldEditor from './RecordFieldEditor.vue'

interface DraftField {
  column: ColumnDefinition
  value: string
}

interface RecordSnapshot {
  entityType: DataTabId
  id: string
  fields: DraftField[]
  originalValues: string[]
}

const props = defineProps<{
  entityType: DataTabId
  recordId: string
  mode: 'edit' | 'create'
  collections: EntityCollections
}>()

const emit = defineEmits<{
  close: []
  save: [mutation: RecordMutation, onSaved: () => void]
  create: [creation: RecordCreation]
  delete: [entityType: DataTabId, id: string, onDeleted: () => void]
}>()

const currentEntityType = ref<DataTabId>(props.entityType)
const currentId = ref(props.recordId)
const fields = ref<DraftField[]>([])
const originalValues = ref<string[]>([])
const history = ref<RecordSnapshot[]>([])

const definition = computed(() => ENTITY_DEFINITIONS[currentEntityType.value])
const isCreatingRoot = computed(() => props.mode === 'create' && history.value.length === 0)
const hasChanges = computed(() =>
  fields.value.some((field, index) => field.value !== originalValues.value[index]),
)

function normalizeId(id: string) {
  return /^\d+$/.test(id) ? id.replace(/^0+(?=\d)/, '') : id
}

function findRecord(entityType: DataTabId, id: string) {
  const normalizedId = normalizeId(id)
  return props.collections[entityType].find((record) => normalizeId(record.id) === normalizedId)
}

function loadRecord(entityType: DataTabId, record: EntityRecord) {
  currentEntityType.value = entityType
  currentId.value = record.id
  fields.value = ENTITY_DEFINITIONS[entityType].columns.map((column) => ({
    column,
    value: record[column.key] ?? '',
  }))
  originalValues.value = fields.value.map((field) => field.value)
}

function loadNewRecord(entityType: DataTabId) {
  currentEntityType.value = entityType
  currentId.value = ''
  fields.value = ENTITY_DEFINITIONS[entityType].columns
    .filter((column) => !column.readonly)
    .map((column) => ({ column, value: '' }))
  originalValues.value = fields.value.map(() => '')
}

function loadInitialRecord() {
  history.value = []
  if (props.mode === 'create') {
    loadNewRecord(props.entityType)
    return
  }
  const record = findRecord(props.entityType, props.recordId)
  if (record) loadRecord(props.entityType, record)
}

function openRelation(field: DraftField) {
  const target = field.column.relation
  if (!target || !field.value) return

  const relatedRecord = findRecord(target, field.value)
  if (!relatedRecord) {
    showError('Связанный объект не найден', 'Запись с ID ' + field.value + ' отсутствует')
    return
  }

  field.value = relatedRecord.id
  history.value.push({
    entityType: currentEntityType.value,
    id: currentId.value,
    fields: fields.value.map((item) => ({ ...item })),
    originalValues: [...originalValues.value],
  })
  loadRecord(target, relatedRecord)
}

function goBack() {
  const previous = history.value.pop()
  if (!previous) return

  currentEntityType.value = previous.entityType
  currentId.value = previous.id
  fields.value = previous.fields.map((field) => ({ ...field }))
  originalValues.value = [...previous.originalValues]
}

function save() {
  const record = fields.value.reduce<EntityRecord>(
    (result, field) => {
      const relatedRecord = field.column.relation
        ? findRecord(field.column.relation, field.value)
        : undefined
      result[field.column.key] = relatedRecord?.id ?? field.value
      return result
    },
    { id: currentId.value },
  )

  if (isCreatingRoot.value) {
    emit('create', { entityType: currentEntityType.value, record })
    return
  }

  const savedValues = fields.value.map((field) => field.value)
  emit(
    'save',
    {
      entityType: currentEntityType.value,
      id: currentId.value,
      record,
    },
    () => {
      originalValues.value = savedValues
    },
  )
}

function discardChanges() {
  fields.value.forEach((field, index) => {
    field.value = originalValues.value[index] ?? ''
  })
}

function deleteRecord() {
  if (!window.confirm(`Delete ${definition.value.typeName.toLowerCase()} #${currentId.value}?`)) {
    return
  }

  emit('delete', currentEntityType.value, currentId.value, () => {
    if (history.value.length > 0) goBack()
    else emit('close')
  })
}

watch(() => [props.mode, props.entityType, props.recordId] as const, loadInitialRecord, {
  immediate: true,
})
</script>

<template>
  <div class="record-overlay" @mousedown.self="emit('close')">
    <section
      class="record-dialog"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="`record-title-${currentId || 'new'}`"
    >
      <header class="dialog-header">
        <div>
          <button
            v-if="history.length"
            class="record-back text-button"
            type="button"
            @click="goBack"
          >
            ← back
          </button>
          <h2 :id="`record-title-${currentId || 'new'}`">
            <template v-if="isCreatingRoot">NEW {{ definition.typeName }} RECORD</template>
            <template v-else>{{ definition.typeName }} RECORD // ID {{ currentId }}</template>
          </h2>
        </div>
        <span v-if="hasChanges">UNSAVED CHANGES</span>
      </header>

      <form class="record-form" @submit.prevent="save">
        <div class="record-fields">
          <RecordFieldEditor
            v-for="(field, index) in fields"
            :key="field.column.key"
            v-model="field.value"
            :field-id="`record-field-${index}`"
            :column="field.column"
            @open-relation="openRelation(field)"
          />
        </div>

        <div class="record-actions">
          <button
            v-if="!isCreatingRoot"
            class="xp-button xp-button--danger"
            type="button"
            @click="deleteRecord"
          >
            Delete
          </button>
          <div>
            <button class="xp-button" type="submit" :disabled="!hasChanges">
              {{ isCreatingRoot ? 'Create' : 'Save' }}
            </button>
            <button class="xp-button" type="button" :disabled="!hasChanges" @click="discardChanges">
              Discard changes
            </button>
            <button class="xp-button" type="button" @click="emit('close')">Exit</button>
          </div>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.record-overlay {
  position: fixed;
  inset: 0;
  z-index: 20;
  padding: 24px;
  display: grid;
  place-items: center;
  background: rgba(60, 22, 19, 0.62);
}

.record-dialog {
  width: min(720px, 100%);
  max-height: calc(100vh - 48px);
  overflow: auto;
  border: 2px solid var(--ink);
  background: var(--paper);
  box-shadow: 7px 7px 0 rgba(60, 22, 19, 0.48);
}

.dialog-header {
  min-height: 38px;
  padding: 9px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  color: #fff1ca;
  background: var(--burgundy);
  border-bottom: 2px solid var(--ink);
}

.dialog-header > div {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dialog-header h2,
.dialog-header span {
  margin: 0;
  font:
    800 11px/1.2 'Courier New',
    monospace;
}

.dialog-header span {
  color: var(--yellow);
  font-size: 9px;
}

.record-back {
  font:
    800 9px/1 'Courier New',
    monospace;
}

.record-form {
  padding: 20px;
}

.record-fields {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 15px 18px;
}

.record-actions {
  margin-top: 22px;
  padding-top: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  border-top: 1px solid #b77855;
}

.record-actions > div {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 760px) {
  .record-overlay {
    padding: 10px;
  }

  .record-dialog {
    max-height: calc(100vh - 20px);
    box-shadow: 4px 4px 0 rgba(60, 22, 19, 0.48);
  }

  .record-fields {
    grid-template-columns: 1fr;
  }

  .record-actions {
    align-items: stretch;
    flex-direction: column-reverse;
  }

  .record-actions > div,
  .record-actions button {
    width: 100%;
  }
}
</style>
