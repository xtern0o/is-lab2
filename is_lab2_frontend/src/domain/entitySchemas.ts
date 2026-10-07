import type { EntityDefinitions } from './types'

const COLORS = ['', 'GREEN', 'BLACK', 'YELLOW', 'ORANGE'] as const
const COUNTRIES = ['', 'FRANCE', 'INDIA', 'VATICAN', 'SOUTH_KOREA', 'NORTH_KOREA'] as const
const RATINGS = ['', 'PG_13', 'R', 'NC_17'] as const
const GENRES = ['', 'WESTERN', 'TRAGEDY', 'THRILLER', 'HORROR', 'FANTASY'] as const

// AI generated definitions: я проверил все верно

export const ENTITY_DEFINITIONS: EntityDefinitions = {
  movies: {
    typeName: 'MOVIE',
    statusNoun: 'records',
    searchableKeys: ['name'],
    columns: [
      { key: 'id', label: 'ID', readonly: true },
      { key: 'name', label: 'NAME' },
      { key: 'coordinatesId', label: 'COORDINATES ID', relation: 'coordinates' },
      { key: 'creationDate', label: 'CREATION DATE', readonly: true },
      { key: 'oscarsCount', label: 'OSCARS' },
      { key: 'budget', label: 'BUDGET' },
      { key: 'totalBoxOffice', label: 'TOTAL BOX OFFICE' },
      { key: 'mpaaRating', label: 'MPAA RATING', options: RATINGS },
      { key: 'directorId', label: 'DIRECTOR ID', relation: 'persons' },
      { key: 'screenwriterId', label: 'SCREENWRITER ID', relation: 'persons' },
      { key: 'operatorId', label: 'OPERATOR ID', relation: 'persons' },
      { key: 'length', label: 'LENGTH' },
      { key: 'goldenPalmCount', label: 'GOLDEN PALM COUNT' },
      { key: 'genre', label: 'GENRE', options: GENRES },
    ],
  },
  persons: {
    typeName: 'PERSON',
    statusNoun: 'people',
    searchableKeys: ['name'],
    columns: [
      { key: 'id', label: 'ID', readonly: true },
      { key: 'name', label: 'NAME' },
      { key: 'eyeColor', label: 'EYE COLOR', options: COLORS },
      { key: 'hairColor', label: 'HAIR COLOR', options: COLORS.slice(1) },
      { key: 'locationId', label: 'LOCATION ID', relation: 'locations' },
      { key: 'height', label: 'HEIGHT' },
      { key: 'nationality', label: 'NATIONALITY', options: COUNTRIES },
    ],
  },
  locations: {
    typeName: 'LOCATION',
    statusNoun: 'locations',
    searchableKeys: ['name'],
    columns: [
      { key: 'id', label: 'ID', readonly: true },
      { key: 'x', label: 'X' },
      { key: 'y', label: 'Y' },
      { key: 'z', label: 'Z' },
      { key: 'name', label: 'NAME' },
    ],
  },
  coordinates: {
    typeName: 'COORDINATES',
    statusNoun: 'points',
    searchableKeys: [],
    columns: [
      { key: 'id', label: 'ID', readonly: true },
      { key: 'x', label: 'X' },
      { key: 'y', label: 'Y' },
    ],
  },
}
