package com.yourssu.focuswave

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.random.Random

@Composable
fun OrbitSection(
    progress: Float,
    pathSeed: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        //0. 사이즈 설정
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val moonSize = 40.dp
        val earthSize = 40.dp
        val rocketSize = 80.dp

        //1. S자 궤도 한 번만 생성
        val density = LocalDensity.current

        val path = remember(width, height, pathSeed) {
            with(density) {
                OrbitUtil.generateRandomOrbitPath(
                    pathSeed,
                    width,
                    height,
                    height - earthSize.toPx() / 2f,
                    moonSize.toPx() / 2f,
                )
            }
        }

        //2. 궤도 측량 및 좌표/각도 계산
        val pathMeasure = remember(path) {
            PathMeasure().apply {
                //마지막 점에서 시작점으로 직선으로 닫는 선 제거 (forceClosed = false)
                setPath(path, false)
            }
        }

        val currentDistance = pathMeasure.length * progress
        val position = pathMeasure.getPosition(currentDistance)
        val tangent = pathMeasure.getTangent(currentDistance)
        val angleInDegrees = if (tangent.x != 0f || tangent.y != 0f) {
            (atan2(tangent.y, tangent.x) * (180f / PI.toFloat()))
        } else {
            0f
        }

        //3. 우주에 궤도 선 그리기 (실선 + 점선)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val traveledPath = Path()
            val remainingPath = Path()

            pathMeasure.getSegment(0f, currentDistance, traveledPath, true)
            pathMeasure.getSegment(currentDistance, pathMeasure.length, remainingPath, true)

            //진행한 경로 그리기
            drawPath(
                path = traveledPath,
                color = Color.White.copy(alpha = 0.8f),
                style = Stroke(width = 6f)
            )

            //남은 경로 그리기
            drawPath(
                path = remainingPath,
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(width = 5.0f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))
            )
        }

        // 4. 행성과 로켓 배치
        Image(
            painter = painterResource(id = R.drawable.moon),
            contentDescription = "달",
            modifier = Modifier.size(moonSize).align(Alignment.TopCenter)
        )

        Image(
            painter = painterResource(id = R.drawable.earth),
            contentDescription = "지구",
            modifier = Modifier.size(earthSize).align(Alignment.BottomCenter)
        )

        Image(
            painter = painterResource(id = R.drawable.rocket1),
            contentDescription = "로켓",
            modifier = Modifier
                .size(rocketSize)
                // graphicsLayer 사용
                .graphicsLayer {
                    // 1. Int로 변환하지 않고 Float(소수점) 값을 써서 오차 방지
                    translationX = position.x - (size.width / 2f)
                    translationY = position.y - (size.height / 2f)

                    // 2. 중심축을 기준으로 완벽하게 회전
                    rotationZ = when(progress) {
                        in 0f .. 0.95f ->  angleInDegrees + 90f
                        else -> angleInDegrees + 270f // 달 착륙시에 우주선 방향을 반대로 회전
                    }
                }
        )
    }
}