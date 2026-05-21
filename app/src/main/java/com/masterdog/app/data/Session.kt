package com.masterdog.app.data

import com.masterdog.app.data.UserUi

/**
 * Sesión en memoria: guarda el usuario logueado durante la vida del proceso.
 * Simple para un trabajo universitario; no persiste entre cierres de la app.
 */
object Session {
    var currentUserId: Long = 0L
    var currentUser: UserUi? = null

    fun set(user: UserUi) {
        currentUser = user
        currentUserId = user.id.toLongOrNull() ?: 0L
    }

    fun clear() {
        currentUserId = 0L
        currentUser = null
    }
}
