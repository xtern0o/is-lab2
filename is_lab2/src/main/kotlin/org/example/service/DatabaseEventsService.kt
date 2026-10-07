package org.example.service

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

@Service
class DatabaseEventsService {
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun subscribe(): SseEmitter {
        val emitter = SseEmitter(30 * 60 * 1000) // 30min

        emitters.add(emitter)

        emitter.onCompletion {
            emitters.remove(emitter)
        }
        emitter.onError {
            emitters.remove(emitter)
        }
        emitter.onTimeout {
            emitters.remove(emitter)
        }

        emitter.send(
            SseEmitter.event()
                .name("connected")
                .data("ok")
        )

        return emitter
    }

    fun publish(entity: String) {
        emitters.forEach {
            try {
                it.send(
                    SseEmitter.event()
                        .name("updated")
                        .data(entity)
                )
            } catch (_: Exception) {
                emitters.remove(it)
            }
        }
    }
}