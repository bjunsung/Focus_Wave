package com.yourssu.focuswave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourssu.focuswave.ui.components.SoundMixerPanel
import com.yourssu.focuswave.ui.sound.SoundPlaybackEffect
import com.yourssu.focuswave.ui.state.SoundTrackId
import com.yourssu.focuswave.ui.state.TimerPhase
import com.yourssu.focuswave.ui.state.TimerUiState
import com.yourssu.focuswave.ui.theme.FocusWaveTheme
import com.yourssu.focuswave.ui.timer.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusWaveTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    timerViewModel: TimerViewModel = viewModel()
) {
    val uiState by timerViewModel.uiState.collectAsState()

    MainScreenContent(
        uiState = uiState,
        onStartClick = timerViewModel::startTimer,
        onPauseClick = timerViewModel::pauseTimer,
        onResetClick = timerViewModel::resetTimer,
        onNewPathClick = timerViewModel::increasePathSeed,
        onUpdateFocusMinutes = timerViewModel::updateFocusMinutes,
        onUpdateBreakMinutes = timerViewModel::updateBreakMinutes,
        onSoundEnabledChange = timerViewModel::setSoundEnabled,
        onSoundVolumeChange = timerViewModel::setSoundVolume
    )
}

@Composable
private fun MainScreenContent(
    uiState: TimerUiState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onNewPathClick: () -> Unit,
    onUpdateFocusMinutes: (Int) -> Unit,
    onUpdateBreakMinutes: (Int) -> Unit,
    onSoundEnabledChange: (SoundTrackId, Boolean) -> Unit,
    onSoundVolumeChange: (SoundTrackId, Float) -> Unit
) {
    val playbackSoundTracks = if (uiState.phase == TimerPhase.PAUSED) {
        uiState.soundTracks.map { it.copy(isEnabled = false) }
    } else {
        uiState.soundTracks
    }

    SoundPlaybackEffect(soundTracks = playbackSoundTracks)

    FocusScreen(
        uiState = uiState,
        timerOverlay = {
            TimerControlsPanel(
                uiState = uiState,
                onStartClick = onStartClick,
                onPauseClick = onPauseClick,
                onResetClick = onResetClick,
                onNewPathClick = onNewPathClick,
                onUpdateFocusMinutes = onUpdateFocusMinutes,
                onUpdateBreakMinutes = onUpdateBreakMinutes
            )
        },
        countdownOverlay = {
            BreakCountdownOverlay(
                isVisible = uiState.showBreakCountdown,
                count = uiState.breakCountdownNumber
            )
        },
        soundMixerPanel = {
            SoundMixerPanel(
                soundTracks = uiState.soundTracks,
                onEnabledChange = onSoundEnabledChange,
                onVolumeChange = onSoundVolumeChange
            )
        }
    )
}

@Composable
private fun FocusScreen(
    uiState: TimerUiState,
    timerOverlay: @Composable () -> Unit,
    countdownOverlay: @Composable () -> Unit,
    soundMixerPanel: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        FocusScene()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val compactHeight = maxHeight < 720.dp
                val horizontalPadding = if (compactHeight) 12.dp else 16.dp
                val verticalPadding = if (compactHeight) 8.dp else 12.dp
                val sectionGap = if (compactHeight) 8.dp else 12.dp
                val orbitMinHeight = if (compactHeight) 170.dp else 220.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(sectionGap)
                ) {
                    timerOverlay()

                    OrbitSection(
                        progress = uiState.progress,
                        pathSeed = uiState.pathSeed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .heightIn(min = orbitMinHeight)
                    )

                    soundMixerPanel()
                }
            }
        }

        countdownOverlay()
    }
}

@Composable
private fun FocusScene(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.space1_bg),
            contentDescription = "Space background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimerControlsPanel(
    uiState: TimerUiState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onNewPathClick: () -> Unit,
    onUpdateFocusMinutes: (Int) -> Unit,
    onUpdateBreakMinutes: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val journeyText = OrbitUtil.getStateByProgress(
        progress = uiState.progress,
        phase = uiState.phase,
        isRunning = uiState.isRunning
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.12f), shape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = uiState.formattedTime,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "TOTAL ${uiState.totalFormattedTime} / ${uiState.statusText}",
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = journeyText,
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                text = uiState.activePhase.name,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelMedium
            )
        }

        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.24f)
        )

        TimerDurationSettings(
            focusMinutes = uiState.focusMinutes,
            breakMinutes = uiState.breakMinutes,
            enabled = uiState.canEditDurations,
            onUpdateFocusMinutes = onUpdateFocusMinutes,
            onUpdateBreakMinutes = onUpdateBreakMinutes
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 4
        ) {
            TimerActionButton(
                text = if (uiState.phase == TimerPhase.PAUSED) "RESUME" else "START",
                onClick = onStartClick,
                enabled = !uiState.isRunning
            )
            TimerActionButton(
                text = "PAUSE",
                onClick = onPauseClick,
                enabled = uiState.isRunning
            )
            TimerActionButton(text = "RESET", onClick = onResetClick)
            TimerActionButton(text = "NEW PATH", onClick = onNewPathClick)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimerDurationSettings(
    focusMinutes: Int,
    breakMinutes: Int,
    enabled: Boolean,
    onUpdateFocusMinutes: (Int) -> Unit,
    onUpdateBreakMinutes: (Int) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2
    ) {
        DurationStepper(
            label = "FOCUS",
            minutes = focusMinutes,
            enabled = enabled,
            onDecrease = { onUpdateFocusMinutes(focusMinutes - 1) },
            onIncrease = { onUpdateFocusMinutes(focusMinutes + 1) }
        )
        DurationStepper(
            label = "BREAK",
            minutes = breakMinutes,
            enabled = enabled,
            onDecrease = { onUpdateBreakMinutes(breakMinutes - 1) },
            onIncrease = { onUpdateBreakMinutes(breakMinutes + 1) }
        )
    }
}

@Composable
private fun DurationStepper(
    label: String,
    minutes: Int,
    enabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .width(146.dp)
            .background(Color.White.copy(alpha = 0.1f), shape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), shape)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(text = "-", enabled = enabled, onClick = onDecrease)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "${minutes}m",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        StepperButton(text = "+", enabled = enabled, onClick = onIncrease)
    }
}

@Composable
private fun StepperButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val contentAlpha = if (enabled) 1f else 0.38f

    Box(
        modifier = Modifier
            .size(44.dp)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = shape,
                clip = false
            )
            .background(Color.White.copy(alpha = if (enabled) 0.20f else 0.08f), shape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = if (enabled) 0.34f else 0.12f)), shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = contentAlpha),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimerActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(8.dp)

    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .width(108.dp)
            .height(44.dp)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), shape),
        shape = shape,
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 1.dp,
            hoveredElevation = 7.dp,
            focusedElevation = 7.dp
        ),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            contentColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.1f),
            disabledContentColor = Color.White.copy(alpha = 0.42f)
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BreakCountdownOverlay(
    isVisible: Boolean,
    count: Int,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(168.dp)
                .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(84.dp))
                .border(
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)),
                    RoundedCornerShape(84.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Break ends in",
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    backgroundColor = 0xFF000000
)
@Composable
fun MainScreenPreview() {
    val previewState = TimerUiState()

    FocusWaveTheme {
        FocusScreen(
            uiState = previewState,
            timerOverlay = {
                TimerControlsPanel(
                    uiState = previewState,
                    onStartClick = {},
                    onPauseClick = {},
                    onResetClick = {},
                    onNewPathClick = {},
                    onUpdateFocusMinutes = {},
                    onUpdateBreakMinutes = {}
                )
            },
            countdownOverlay = {},
            soundMixerPanel = {}
        )
    }
}
