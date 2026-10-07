export type DataTabId = 'movies' | 'persons' | 'locations' | 'coordinates'
export type TabId = DataTabId | 'operations'

export interface EntityRecord {
  id: string
  [key: string]: string
}

export interface ColumnDefinition {
  key: string
  label: string
  readonly?: boolean
  options?: readonly string[]
  relation?: DataTabId
}

export interface EntityDefinition {
  typeName: string
  statusNoun: string
  searchableKeys: readonly string[]
  columns: readonly ColumnDefinition[]
}

export type EntityDefinitions = Record<DataTabId, EntityDefinition>
export type EntityCollections = Record<DataTabId, EntityRecord[]>

export interface RecordMutation {
  entityType: DataTabId
  id: string
  record: EntityRecord
}

export interface RecordCreation {
  entityType: DataTabId
  record: EntityRecord
}
