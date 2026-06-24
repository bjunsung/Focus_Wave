    package com.yourssu.focuswave

    import android.R.attr.id
    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.selection.selectable
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Button
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.TimeInput
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.tooling.preview.Preview
    import com.yourssu.focuswave.ui.theme.FocusWaveTheme
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import kotlinx.coroutines.delay
    import kotlin.random.Random
    import kotlin.random.nextInt


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
    fun MainScreen() {
        var isRunning by rememberSaveable { mutableStateOf(true) }
        var time by rememberSaveable { mutableStateOf(0) }
        var totalTime by rememberSaveable { mutableStateOf(3600) }

        val timeText = TimeUtil.formatTime(time)
        val totalTimeText = TimeUtil.formatTime(totalTime)
        val progress = time.toFloat() / totalTime.toFloat()

        var pathSeed by rememberSaveable { mutableStateOf(Random.nextInt()) }

        val status = OrbitUtil.getStateByProgress(progress, isRunning)



        LaunchedEffect(isRunning) {
            while(isRunning && time < totalTime) {
                delay(1000L)
                time++
            }
        }
3
        Box(
            modifier = Modifier
                .fillMaxSize()
            ) {
            //배경 이미지
            Image(
                painter = painterResource(id = R.drawable.space1_bg),
                contentDescription = "우주배경",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                containerColor = Color.Transparent
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //상단 정보창 (header)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally)

                    ) {
                        Text(text = "비행 시간 ${timeText}", color = Color.White, fontSize = 18.sp)
                        Text(text = "총 소요 시간 ${totalTimeText}", color = Color.White, fontSize = 18.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "우주선 ${status}", color = Color.White, fontSize = 18.sp)
                    }



                    OrbitSection(
                        progress = progress,
                        pathSeed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color.White.copy(alpha = 0.125f), shape = RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        // 일시정지, 재개 버튼
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { /* 일시정지 로직 */
                                isRunning = !isRunning
                            }) {
                                Text(
                                    if (isRunning)
                                    "일시정지"
                                    else
                                    "재개"
                                )
                            }
                            Button(onClick = {
                            /* 경로변경 로직 */
                                pathSeed = Random.nextInt()
                            }) { Text("경로변경") }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 사운드 믹서 슬라이더 등...
                        Text("SOUND MIXER", color = Color.White)
                    }


                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        FocusWaveTheme{
            MainScreen()
        }
    }


