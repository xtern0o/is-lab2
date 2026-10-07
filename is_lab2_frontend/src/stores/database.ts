import { computed, reactive, ref, watch } from 'vue'
import { defineStore } from 'pinia'

import { apiDelete, apiGet, apiPost, apiPut } from '@/config/api'
import { subscribeToUpdates } from '@/config/events'
import { ENTITY_DEFINITIONS } from '@/domain/entitySchemas'
import type {
  DataTabId,
  EntityCollections,
  EntityRecord,
  RecordCreation,
  RecordMutation,
  TabId,
} from '@/domain/types'
import { showError } from '@/shared/errors'

const PAGE_SIZE = 5
const ENTITY_ENDPOINTS: Record<DataTabId, string> = {
  movies: '/movies',
  persons: '/person',
  locations: '/location',
  coordinates: '/coordinates',
}

interface ApiPage {
  content: Array<Record<string, unknown>>
  totalPages: number
  totalElements: number
}

const NUMBER_FIELDS = new Set([
  'x',
  'y',
  'z',
  'coordinatesId',
  'oscarsCount',
  'budget',
  'totalBoxOffice',
  'directorId',
  'screenwriterId',
  'operatorId',
  'length',
  'goldenPalmCount',
  'locationId',
  'height',
])

export const useDatabaseStore = defineStore('database', () => {
  const collections = reactive<EntityCollections>({
    movies: [],
    persons: [],
    locations: [],
    coordinates: [],
  })
  const activeTab = ref<TabId>('movies')
  const query = ref('')
  const databaseQuery = ref<string | null>(null)
  const currentPage = ref(0)
  const sortDescending = ref(false)
  const pageCount = ref(1)
  const totalElements = ref(0)
  const selectedEntityType = ref<DataTabId | null>(null)
  const selectedRecordId = ref('')
  const isCreating = ref(false)

  const activeEntityType = computed<DataTabId>(() =>
    activeTab.value === 'operations' ? 'movies' : activeTab.value,
  )
  const activeDefinition = computed(() => ENTITY_DEFINITIONS[activeEntityType.value])
  const activeRecords = computed(() => collections[activeEntityType.value])
  const isOperations = computed(() => activeTab.value === 'operations')
  const windowTitle = computed(() =>
    isOperations.value ? 'SPECIAL OPERATIONS' : activeDefinition.value.typeName,
  )
  const windowStatus = computed(() => {
    if (isOperations.value) return '05 actions available'
    const count = String(totalElements.value).padStart(2, '0')
    return `${count} ${activeDefinition.value.statusNoun} online`
  })

  function matchesSearch(record: EntityRecord, search: string) {
    return activeDefinition.value.searchableKeys.some((key) =>
      (record[key] ?? '').toLowerCase().includes(search),
    )
  }

  const databaseRows = computed(() => {
    const search = databaseQuery.value?.toLowerCase()
    return search
      ? activeRecords.value.filter((record) => matchesSearch(record, search))
      : activeRecords.value
  })
  const visibleRows = computed(() => {
    const search = query.value.trim().toLowerCase()
    return !search || search === databaseQuery.value?.toLowerCase()
      ? databaseRows.value
      : databaseRows.value.filter((record) => matchesSearch(record, search))
  })

  function clearSearch() {
    query.value = ''
    databaseQuery.value = null
    currentPage.value = 0
  }

  function selectTab(tab: TabId) {
    activeTab.value = tab
  }

  function searchAll() {
    const search = query.value.trim()
    if (!search) return
    databaseQuery.value = search
    currentPage.value = 0
  }

  function changePage(offset: number) {
    const nextPage = currentPage.value + offset
    if (nextPage >= 0 && nextPage < pageCount.value) currentPage.value = nextPage
  }

  function toggleSort() {
    sortDescending.value = !sortDescending.value
    currentPage.value = 0
  }

  function toEntityRecord(source: Record<string, unknown>): EntityRecord {
    return Object.fromEntries(
      Object.entries(source).map(([key, value]) => {
        let text = value == null ? '' : String(value)
        if (key === 'creationDate') text = text.slice(0, 10)
        if (text && (key === 'id' || key.endsWith('Id'))) text = text.padStart(3, '0')
        return [key, text]
      }),
    ) as EntityRecord
  }

  function toRequest(record: EntityRecord) {
    return Object.fromEntries(
      Object.entries(record)
        .filter(([key]) => key !== 'id' && key !== 'creationDate')
        .map(([key, value]) => [
          key,
          value === '' ? null : NUMBER_FIELDS.has(key) ? Number(value) : value,
        ]),
    )
  }

  let lastRequestId = 0
  let unsubscribeUpdates: (() => void) | null = null

  async function loadPage() {
    const requestId = ++lastRequestId
    if (isOperations.value) return

    const entityType = activeEntityType.value
    const direction = sortDescending.value ? 'desc' : 'asc'

    try {
      const response: ApiPage = await apiGet(
        `${ENTITY_ENDPOINTS[entityType]}?page=${currentPage.value}&size=${PAGE_SIZE}&sort=id,${direction}`,
      )

      // проверка если пользователь решил свичнуться на другой таб
      // чтобы инвалидировать прошлый запрос и выполнить последний
      if (requestId !== lastRequestId) return

      const totalPages = Math.max(1, response.totalPages)
      if (currentPage.value >= totalPages) {
        pageCount.value = totalPages
        currentPage.value = totalPages - 1
        return
      }

      collections[entityType] = response.content.map(toEntityRecord)
      pageCount.value = totalPages
      totalElements.value = response.totalElements
    } catch (error) {
      if (requestId !== lastRequestId) return

      collections[entityType] = []
      pageCount.value = 1
      totalElements.value = 0

      showError(
        'не удалось загрузить страницуу :((',
        error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
      )
    }
  }

  function startUpdates() {
    if (unsubscribeUpdates) return

    unsubscribeUpdates = subscribeToUpdates((entity) => {
      if (!isOperations.value && entity === activeEntityType.value) {
        void loadPage()
      }
    })
  }

  function stopUpdates() {
    unsubscribeUpdates?.()
    unsubscribeUpdates = null
  }

  function openRecord(id: string) {
    isCreating.value = false
    selectedEntityType.value = activeEntityType.value
    selectedRecordId.value = id
  }

  function startCreateRecord() {
    isCreating.value = true
    selectedEntityType.value = activeEntityType.value
    selectedRecordId.value = ''
  }

  function closeRecord() {
    isCreating.value = false
    selectedEntityType.value = null
    selectedRecordId.value = ''
  }

  async function saveRecord({ entityType, id, record }: RecordMutation, onSaved: () => void) {
    try {
      await apiPut(`${ENTITY_ENDPOINTS[entityType]}/${Number(id)}`, toRequest(record))

      onSaved()
      await loadPage()
    } catch (error) {
      showError(
        'не удалось обновить запись :((',
        error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
      )
    }
  }

  async function createRecord({ entityType, record }: RecordCreation) {
    try {
      await apiPost(ENTITY_ENDPOINTS[entityType], toRequest(record))
      closeRecord()
      await loadPage()
    } catch (error) {
      showError(
        'не удалось создать запись :((',
        error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
      )
    }
  }

  async function deleteRecord(entityType: DataTabId, id: string, onDeleted: () => void) {
    try {
      await apiDelete(`${ENTITY_ENDPOINTS[entityType]}/${Number(id)}`)
      onDeleted()
      await loadPage()
    } catch (error) {
      showError(
        'не удалось удалить запись :((',
        error instanceof Error ? error.message : 'нам не сообщили что за ошибка',
      )
    }
  }

  watch(activeTab, () => {
    clearSearch()
    sortDescending.value = false
  })

  watch([activeTab, currentPage, sortDescending], () => void loadPage(), { immediate: true })

  return {
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
    selectTab,
    searchAll,
    clearSearch,
    changePage,
    toggleSort,
    loadPage,
    startUpdates,
    stopUpdates,
    openRecord,
    startCreateRecord,
    closeRecord,
    saveRecord,
    createRecord,
    deleteRecord,
  }
})
