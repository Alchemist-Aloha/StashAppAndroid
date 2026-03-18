package com.github.damontecres.stashapp.ui.components.playback

import java.util.concurrent.ConcurrentHashMap

/**
 * Temporary in-memory playback position handoff between related screens.
 */
object PlaybackPositionCache {
    private val scenePositions = ConcurrentHashMap<String, Long>()

    fun setScenePosition(
        sceneId: String,
        position: Long,
    ) {
        if (position >= 0L) {
            scenePositions[sceneId] = position
        }
    }

    fun getScenePosition(sceneId: String): Long? = scenePositions[sceneId]
}
