package com.yourssu.focuswave

import androidx.compose.ui.graphics.Path
import kotlin.random.Random

object OrbitUtil {

    //pathSeed 기반으로 랜덤 경로를 생성하는 함수
    fun generateRandomOrbitPath(
        pathSeed: Int,
        width: Float,
        height: Float,
        startY: Float,
        endY: Float
    ): Path {
        return Path().apply {
            val random = Random(pathSeed)

            //시작점 : 하단 중앙
            val startX = width / 2f
            moveTo(startX, startY)

            //랜덤 제어점 생성
            val c1x = random.nextFloat() * width
            val c1y = random.nextFloat() * height
            val c2x = random.nextFloat() * width
            val c2y = random.nextFloat() * height

            //도착점 : 상단 중앙
            val endX = width / 2f

            //곡선궤도
            cubicTo(c1x, c1y, c2x, c2y, endX, endY)


            //직선궤도 (테스트용)
            //lineTo(endX, endY)
        }
    }


    // 진행률에 따른 우주선 상태를 String 으로 반환하는 함수
    fun getStateByProgress(progress: Float, isRunning: Boolean) : String {
        return  if (progress == 0f) { "출발 대기중" }
        else if (!isRunning) { "잠시 멈춤" }
        else if (progress < 0.1f) { "대기권 돌파중" }
        else if (progress < 0.2f) { "대기권 이탈중" }
        else if (progress < 0.8f) { "달을 향해 비행중" }
        else if (progress < 0.95f) { "달 궤도 진입중" }
        else if (progress < 1f) { "착륙 하강중" }
        else if (progress == 1f) { "달 착륙 완료" }
        else { "신호 불안정" }
     }
}