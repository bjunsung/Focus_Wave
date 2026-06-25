package com.yourssu.focuswave.ui.state

import kotlin.math.roundToInt

data class TimerUiState(
    val focusMinutes: Int = DEFAULT_FOCUS_MINUTES,
    val breakMinutes: Int = DEFAULT_BREAK_MINUTES,
    val totalSeconds: Int = DEFAULT_FOCUS_MINUTES * SECONDS_PER_MINUTE,
    val remainingSeconds: Int = DEFAULT_FOCUS_MINUTES * SECONDS_PER_MINUTE,
    val phase: TimerPhase = TimerPhase.READY,
    val isRunning: Boolean = false,
    val activePhase: TimerPhase = TimerPhase.FOCUS,
    val soundTracks: List<SoundTrackUiState> = defaultSoundTracks
) {
    val formattedTime: String
        get() {
            val minutes = remainingSeconds / SECONDS_PER_MINUTE
            val seconds = remainingSeconds % SECONDS_PER_MINUTE
            return "%02d:%02d".format(minutes, seconds)
        }

    val progress: Float
        get() {
            if (totalSeconds <= 0) return 0f
            val elapsedSeconds = totalSeconds - remainingSeconds
            return (elapsedSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
        }

    val totalFormattedTime: String
        get() {
            val minutes = totalSeconds / SECONDS_PER_MINUTE
            val seconds = totalSeconds % SECONDS_PER_MINUTE
            return "%02d:%02d".format(minutes, seconds)
        }

    val statusText: String
        get() = phase.name

    val canEditDurations: Boolean
        get() = !isRunning

    val showBreakCountdown: Boolean
        get() = phase == TimerPhase.BREAK && remainingSeconds in 1..5

    val breakCountdownNumber: Int
        get() = if (showBreakCountdown) remainingSeconds else 0
}

enum class TimerPhase {
    READY,
    FOCUS,
    BREAK,
    PAUSED,
    FINISHED
}

data class SoundTrackUiState(
    val id: SoundTrackId,
    val title: String,
    val isEnabled: Boolean = false,
    val volume: Float = DEFAULT_VOLUME
) {
    val volumePercent: Int
        get() = (volume.coerceIn(0f, 1f) * 100).roundToInt()
}

enum class SoundTrackId {
    Rain,
    Ocean,
    Cafe,
    Space
}

const val DEFAULT_FOCUS_MINUTES = 25
const val DEFAULT_BREAK_MINUTES = 5
const val SECONDS_PER_MINUTE = 60
private const val DEFAULT_VOLUME = 0.5f

val defaultSoundTracks = listOf(
    SoundTrackUiState(id = SoundTrackId.Rain, title = "Rain"),
    SoundTrackUiState(id = SoundTrackId.Ocean, title = "Ocean"),
    SoundTrackUiState(id = SoundTrackId.Cafe, title = "Cafe"),
    SoundTrackUiState(id = SoundTrackId.Space, title = "Space")
)
