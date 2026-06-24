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
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
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
        var status by remember { mutableStateOf("ASCENDING") }
        var timeText by remember { mutableStateOf("00:00") }
        var totalTimeText by remember { mutableStateOf("02:30") }

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
                        Text(text = "TIME: ${timeText}", color = Color.White, fontSize = 18.sp)
                        Text(text = "TOTAL: ${totalTimeText}", color = Color.White, fontSize = 18.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "STATUS: ${status}", color = Color.White, fontSize = 18.sp)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                            //달 그림
                            Image(
                                painter = painterResource(id = R.drawable.moon),
                                contentDescription = "달",
                                modifier = Modifier.size(40.dp).align(Alignment.TopCenter)
                            )

                            Image(
                                painter = painterResource(id = R.drawable.rocket1),
                                contentDescription = "로켓",
                                modifier = Modifier.size(40.dp)
                            )

                            //지구 그림
                            Image(
                                painter = painterResource(id = R.drawable.earth),
                                contentDescription = "지구",
                                modifier = Modifier.size(40.dp).align(Alignment.BottomCenter)
                            )

                    }

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
                            Button(onClick = { /* 일시정지 로직 */ }) { Text("PAUSE") }
                            Button(onClick = { /* 재개 로직 */ }) { Text("RESUME") }
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