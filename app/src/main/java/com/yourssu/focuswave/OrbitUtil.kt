package com.yourssu.focuswave

import androidx.compose.ui.graphics.Path
import com.yourssu.focuswave.ui.state.TimerPhase
import kotlin.random.Random

object OrbitUtil {
    fun generateJourneyPath(
        pathSeed: Int,
        width: Float,
        height: Float,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ): Path {
        return Path().apply {
            val random = Random(pathSeed)
            moveTo(startX, startY)

            val horizontalDrift = width * (0.12f + random.nextFloat() * 0.08f)
            val verticalLift = height * (0.12f + random.nextFloat() * 0.10f)
            val c1x = (startX + width * 0.20f + horizontalDrift).coerceIn(0f, width)
            val c1y = (startY - height * 0.36f - verticalLift * 0.2f).coerceIn(0f, height)
            val c2x = (endX - width * 0.24f - horizontalDrift * 0.45f).coerceIn(0f, width)
            val c2y = (endY + height * 0.34f + verticalLift).coerceIn(0f, height)

            cubicTo(c1x, c1y, c2x, c2y, endX, endY)
        }
    }

    fun getStateByProgress(
        progress: Float,
        phase: TimerPhase,
        isRunning: Boolean
    ): String {
        return when (phase) {
            TimerPhase.READY -> "Spaceship standing by"
            TimerPhase.PAUSED -> "Mission paused"
            TimerPhase.BREAK -> "Entering rest orbit"
            TimerPhase.FINISHED -> "Goal arrival complete"
            TimerPhase.FOCUS -> when {
                !isRunning -> "Mission paused"
                progress < 0.34f -> "Leaving Earth orbit"
                progress < 0.67f -> "Flying toward the Moon"
                else -> "Preparing Moon landing"
            }
        }
    }
}
