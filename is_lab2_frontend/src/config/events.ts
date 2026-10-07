import { fetchEventSource } from '@microsoft/fetch-event-source'

import { API_BASE_URL, refreshAccessToken } from '@/config/api'
import type { DataTabId } from '@/domain/types'
import { useAuthStore } from '@/stores/auth'

export function subscribeToUpdates(onUpdate: (entity: DataTabId) => void) {
  const auth = useAuthStore()
  const controller = new AbortController()

  if (!auth.accessToken) return () => {}

  void fetchEventSource(`${API_BASE_URL}/events`, {
    headers: { Authorization: `Bearer ${auth.accessToken}` },
    signal: controller.signal,
    openWhenHidden: true,
    fetch(input, init) {
      const headers = new Headers(init?.headers)
      headers.set('Authorization', `Bearer ${auth.accessToken}`)
      return window.fetch(input, { ...init, headers })
    },
    async onopen(response) {
      if (response.status === 401) {
        if (await refreshAccessToken()) throw new Error('access token refreshed')
        controller.abort()
        return
      }

      if (!response.ok) throw new Error(`SSE HTTP ${response.status}`)
    },
    onmessage(message) {
      if (message.event === 'updated') {
        onUpdate(message.data as DataTabId)
      }
    },
    onclose() {
      throw new Error('sse connection closed')
    },
    onerror(error) {
      console.error('sse connection error', error)
    },
  })

  return () => controller.abort()
}
