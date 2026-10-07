import { ref } from 'vue'

interface AppError {
  id: number
  title: string
  description: string
}

export const errors = ref<AppError[]>([])
let nextId = 0

export function showError(title: string, description: string) {
  errors.value.unshift({ id: ++nextId, title, description })
}

export function closeError(id: number) {
  errors.value = errors.value.filter((error) => error.id !== id)
}
