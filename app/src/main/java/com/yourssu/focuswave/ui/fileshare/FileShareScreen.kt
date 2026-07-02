package com.yourssu.focuswave.ui.fileshare

import android.net.Uri
import android.net.wifi.WifiManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.net.InetAddress
import java.nio.ByteOrder

data class SharedFileUi(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String?,
    val uriString: String?,
    val lastModified: Long? = null,
    val isSelected: Boolean = false
)



@Composable
fun FileShareOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var receivedFiles by remember {
        mutableStateOf<List<SharedFileUi>>(emptyList())
    }

    var selectedFiles by remember {
        mutableStateOf<List<SharedFileUi>>(emptyList())
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        selectedFiles = uris.mapIndexed { index, uri ->
            uri.toSharedFileUi(
                context = context,
                id = index.toString()
            )
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val serverPort = 8080 // TODO: 백엔드 팀원이 실제 포트로 바꾸기
    val phoneIpAddress = remember { context.getPhoneIpAddress() }
    val pcAccessUrl = remember(phoneIpAddress, serverPort) {
        if (phoneIpAddress.isBlank()) "" else "http://$phoneIpAddress:$serverPort"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
                .background(Color(0xFF1E1E2E), RoundedCornerShape(18.dp))
                .border(
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    RoundedCornerShape(18.dp)
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HeaderSection(onDismiss = onDismiss)

            ConnectionStatusCard()

            PcAccessInfoCard(
                pcAccessUrl = pcAccessUrl,
                onCopyClick = {
                    clipboardManager.setText(AnnotatedString(pcAccessUrl))
                }
            )

            PcToPhoneSection(
                receivedFiles = receivedFiles,
                onRefreshClick = {
                    // TODO: 백엔드 연결 후 PC에서 업로드된 파일 목록 조회
                    // 예: receivedFiles = fileShareRepository.getReceivedFiles()
                },
                onSaveClick = { file ->
                    // TODO: PC에서 받은 파일 저장/열기 처리
                }
            )

            PhoneToPcSection(
                selectedFiles = selectedFiles,
                onPickFileClick = {
                    filePickerLauncher.launch(arrayOf("*/*"))
                },
                onRemoveFileClick = { targetFile ->
                    selectedFiles = selectedFiles.filterNot { it.id == targetFile.id }
                },
                onSendClick = {
                    // TODO: 선택된 파일들을 백엔드 공유 목록에 등록
                    // 예: fileShareRepository.shareFiles(selectedFiles)
                }
            )
        }
    }
}

@Composable
private fun HeaderSection(
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "🚀 Focus Wave 파일공유",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "같은 와이파이의 PC와 파일을 주고받습니다.",
                color = Color.White.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextButton(onClick = onDismiss) {
            Text("닫기")
        }
    }
}

@Composable
private fun ConnectionStatusCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🟡 연결 확인 대기 중",
            color = Color(0xFFFFD700),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PcToPhoneSection(
    receivedFiles: List<SharedFileUi>,
    onRefreshClick: () -> Unit,
    onSaveClick: (SharedFileUi) -> Unit
) {
    SectionCard {
        Text(
            text = "💻 PC → 📱 폰",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "PC 웹페이지에서 폰으로 보낸 파일입니다.",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = onRefreshClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8A86E6),
                contentColor = Color.White
            )
        ) {
            Text("받은 파일 새로고침")
        }

        if (receivedFiles.isEmpty()) {
            EmptyText("아직 PC에서 받은 파일이 없습니다.")
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 160.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(receivedFiles) { file ->
                    FileRow(
                        file = file,
                        actionText = "저장",
                        onActionClick = { onSaveClick(file) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneToPcSection(
    selectedFiles: List<SharedFileUi>,
    onPickFileClick: () -> Unit,
    onRemoveFileClick: (SharedFileUi) -> Unit,
    onSendClick: () -> Unit
) {
    SectionCard {
        Text(
            text = "📱 폰 → 💻 PC",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "PC로 보낼 파일을 선택합니다.",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = onPickFileClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE68A86),
                contentColor = Color.White
            )
        ) {
            Text("파일 선택하기")
        }

        if (selectedFiles.isEmpty()) {
            EmptyText("선택된 파일이 없습니다.")
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 160.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedFiles) { file ->
                    FileRow(
                        file = file,
                        actionText = "삭제",
                        onActionClick = { onRemoveFileClick(file) }
                    )
                }
            }

            Button(
                onClick = onSendClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8A86E6),
                    contentColor = Color.White
                )
            ) {
                Text("PC로 전송 준비")
            }
        }
    }
}

@Composable
private fun SectionCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun PcAccessInfoCard(
    pcAccessUrl: String,
    onCopyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "PC 접속 주소",
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = if (pcAccessUrl.isBlank()) {
                "Wi-Fi 연결을 확인해주세요."
            } else {
                pcAccessUrl
            },
            color = Color(0xFF8A86E6),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "PC 브라우저에서 위 주소로 접속하면 파일 공유 페이지가 열립니다.",
            color = Color.White.copy(alpha = 0.65f),
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = onCopyClick,
            enabled = pcAccessUrl.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8A86E6),
                contentColor = Color.White
            )
        ) {
            Text("주소 복사")
        }
    }
}

@Composable
private fun FileRow(
    file: SharedFileUi,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.24f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = file.sizeBytes.toFileSizeText(),
                color = Color.White.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextButton(onClick = onActionClick) {
            Text(actionText)
        }
    }
}

@Composable
private fun EmptyText(
    text: String
) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.55f),
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

private fun Uri.toSharedFileUi(
    context: android.content.Context,
    id: String
): SharedFileUi {
    val contentResolver = context.contentResolver

    var fileName = "unknown"
    var sizeBytes = 0L
    val mimeType = contentResolver.getType(this)

    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)

        if (cursor.moveToFirst()) {
            if (nameIndex >= 0) {
                fileName = cursor.getString(nameIndex) ?: "unknown"
            }

            if (sizeIndex >= 0) {
                sizeBytes = cursor.getLong(sizeIndex)
            }
        }
    }

    return SharedFileUi(
        id = id,
        name = fileName,
        sizeBytes = sizeBytes,
        mimeType = mimeType,
        uriString = this.toString()
    )
}

fun Long.toFileSizeText(): String {
    return when {
        this >= 1024 * 1024 -> "%.1f MB".format(this / 1024.0 / 1024.0)
        this >= 1024 -> "%.1f KB".format(this / 1024.0)
        else -> "$this B"
    }
}

private fun android.content.Context.getPhoneIpAddress(): String {
    val wifiManager = applicationContext.getSystemService(android.content.Context.WIFI_SERVICE) as WifiManager
    val ipAddress = wifiManager.connectionInfo.ipAddress

    if (ipAddress == 0) return ""

    val fixedIpAddress = if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        Integer.reverseBytes(ipAddress)
    } else {
        ipAddress
    }

    return InetAddress.getByAddress(
        java.math.BigInteger.valueOf(fixedIpAddress.toLong()).toByteArray()
    ).hostAddress ?: ""
}