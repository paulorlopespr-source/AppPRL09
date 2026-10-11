package com.example.ui.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.model.WorkoutSession
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class BronzeFrame(val name: String, val drawable: Int)

private val bronzeFrames = listOf(
    BronzeFrame("Pintinho", R.drawable.frame_bronze_pintinho),
    BronzeFrame("Frango", R.drawable.frame_bronze_frango),
    BronzeFrame("Lobo", R.drawable.frame_bronze_lobo),
    BronzeFrame("Leão", R.drawable.frame_bronze_leao),
    BronzeFrame("Gorila", R.drawable.frame_bronze_gorila),
    BronzeFrame("Dragão", R.drawable.frame_bronze_dragao)
)

@Composable
fun WorkoutShareStoryDialog(
    session: WorkoutSession,
    userName: String = "Atleta FitPr09",
    prsCount: Int = 0,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedPhotoUri by remember(session.id) { mutableStateOf<Uri?>(null) }
    var cameraUri by remember(session.id) { mutableStateOf<Uri?>(null) }
    var selectedFrame by remember(session.id) { mutableStateOf(bronzeFrames.first()) }
    var frameEnabled by remember(session.id) { mutableStateOf(true) }
    var previewBitmap by remember(session.id) { mutableStateOf<Bitmap?>(null) }
    var isRendering by remember(session.id) { mutableStateOf(false) }
    var isSharing by remember(session.id) { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) selectedPhotoUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) selectedPhotoUri = cameraUri
    }

    val dateLabel = remember(session.startTimeMillis) {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(session.startTimeMillis))
    }
    val timeLabel = remember(session.startTimeMillis) {
        SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(Date(session.startTimeMillis))
    }
    val durationLabel = DateUtils.formatSecondsToTime(session.durationSeconds)
    val volumeLabel = "${formatShareWeight(session.totalWeightLiftedKg)} kg"
    val caption = remember(session, prsCount) {
        buildString {
            append("Treino concluído: ${session.title}\n")
            append("Duração: ${DateUtils.formatSecondsToTime(session.durationSeconds)} · ")
            append("Carga acumulada: ${formatShareWeight(session.totalWeightLiftedKg)} kg\n")
            append("Calorias estimadas: ${session.estimatedCalories} kcal")
            if (prsCount > 0) append("\n$prsCount recorde(s) pessoal(is) novo(s)!")
            append("\n#FitPr09 #TreinoConcluído")
        }
    }

    LaunchedEffect(selectedPhotoUri, selectedFrame, frameEnabled, session.id, userName, prsCount) {
        val photoUri = selectedPhotoUri ?: run { previewBitmap = null; return@LaunchedEffect }
        isRendering = true
        val rendered = withContext(Dispatchers.IO) {
            runCatching {
                val photo = decodeWorkoutPhoto(context, photoUri)
                    ?: error("Não foi possível abrir a foto")
                val frame = if (frameEnabled) BitmapFactory.decodeResource(context.resources, selectedFrame.drawable) else null
                val rendered = try {
                    renderWorkoutShareImage(
                        photo = photo,
                        frame = frame,
                        session = session,
                        userName = userName,
                        prsCount = prsCount,
                        dateLabel = dateLabel,
                        timeLabel = timeLabel,
                        durationLabel = durationLabel,
                        volumeLabel = volumeLabel
                    )
                } finally {
                    photo.recycle()
                    frame?.recycle()
                }
                rendered
            }
        }
        if (rendered.isFailure) {
            Toast.makeText(context, rendered.exceptionOrNull()?.message ?: "Erro ao preparar a foto", Toast.LENGTH_LONG).show()
        }
        previewBitmap = rendered.getOrNull()
        isRendering = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = PurpleDarkest,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth(0.96f).padding(vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Sua foto do treino", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Escolha uma foto e personalize a moldura", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary) }
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showPhotoSourceDialog = true },
                        modifier = Modifier.weight(1f).testTag("btn_choose_workout_photo"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (selectedPhotoUri == null) "Escolher foto" else "Trocar foto", maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = {
                            val uri = createWorkoutCameraUri(context)
                            cameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f).testTag("btn_take_workout_photo"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tirar foto", maxLines = 1)
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Molduras Bronze", color = LilacAccent, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { frameEnabled = !frameEnabled }, modifier = Modifier.testTag("btn_toggle_workout_frame")) {
                        Text(if (frameEnabled) "Sem moldura" else "Usar moldura", color = LilacAccent)
                    }
                }
                if (frameEnabled) {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        bronzeFrames.forEach { frame ->
                            FilterChip(
                                selected = selectedFrame == frame,
                                onClick = { selectedFrame = frame },
                                label = { Text(frame.name) },
                                modifier = Modifier.testTag("frame_${frame.name.lowercase(Locale.ROOT)}")
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(0.78f).aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(listOf(PurpleDarkSurface, PurpleDeepCard, PurpleDarkest)))
                        .border(1.dp, LilacAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    previewBitmap?.let { bitmap ->
                        Image(bitmap.asImageBitmap(), contentDescription = "Prévia da foto com moldura e resumo do treino", modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f))
                    } ?: Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(38.dp))
                        Text(if (isRendering) "Preparando prévia…" else "Escolha ou tire uma foto para ver a arte", color = TextSecondary, textAlign = TextAlign.Center)
                        if (!isRendering) Text("${session.title} · $volumeLabel", color = LilacAccent, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
                Text("${session.title}  ·  $durationLabel  ·  ${session.estimatedCalories} kcal", modifier = Modifier.padding(top = 8.dp), color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)

                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        val image = previewBitmap ?: return@Button
                        isSharing = true
                        scope.launch {
                            val uri = withContext(Dispatchers.IO) { saveWorkoutShareImage(context, image) }
                            isSharing = false
                            if (uri == null) {
                                Toast.makeText(context, "Não foi possível preparar a imagem para compartilhar.", Toast.LENGTH_LONG).show()
                            } else {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    putExtra(Intent.EXTRA_TEXT, caption)
                                    clipData = ClipData.newUri(context.contentResolver, "Foto do treino", uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartilhar foto do treino"))
                            }
                        }
                    },
                    enabled = previewBitmap != null && !isRendering && !isSharing,
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_share_workout_social"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleVibrant)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isSharing) "Preparando…" else "Compartilhar imagem", fontWeight = FontWeight.Bold)
                }
                Text("O Android mostrará os aplicativos disponíveis. Nenhuma conta social é conectada ao FitPr09.", modifier = Modifier.padding(top = 8.dp), color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            title = { Text("Adicionar foto", color = TextPrimary) },
            text = { Text("Escolha uma imagem da galeria ou tire uma foto agora.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Abrir galeria", color = LilacAccent) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    val uri = createWorkoutCameraUri(context)
                    cameraUri = uri
                    cameraLauncher.launch(uri)
                }) { Text("Abrir câmera", color = LilacAccent) }
            },
            containerColor = PurpleDarkSurface
        )
    }
}

private fun createWorkoutCameraUri(context: Context): Uri {
    val directory = File(context.cacheDir, "workout_share").apply { mkdirs() }
    val photo = File(directory, "workout_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photo)
}

private fun saveWorkoutShareImage(context: Context, bitmap: Bitmap): Uri? = runCatching {
    val directory = File(context.cacheDir, "workout_share").apply { mkdirs() }
    val image = File(directory, "fitpr09_workout_${System.currentTimeMillis()}.png")
    image.outputStream().use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", image)
}.getOrNull()

private fun decodeWorkoutPhoto(context: Context, uri: Uri): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    var sample = 1
    while (bounds.outWidth / sample > 1800 || bounds.outHeight / sample > 1800) sample *= 2
    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    return context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
}

private fun renderWorkoutShareImage(
    photo: Bitmap,
    frame: Bitmap?,
    session: WorkoutSession,
    userName: String,
    prsCount: Int,
    dateLabel: String,
    timeLabel: String,
    durationLabel: String,
    volumeLabel: String
): Bitmap {
    val width = 1024
    val height = 1536
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    canvas.drawColor(AndroidColor.rgb(12, 8, 25))
    if (frame != null) {
        // Bronze PNGs include a transparent portrait window; place the photo beneath it.
        drawBitmapCenterCrop(canvas, photo, RectF(88f, 365f, 774f, 1150f))
        canvas.drawBitmap(frame, null, Rect(0, 0, width, height), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        val placeholderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = AndroidColor.rgb(18, 13, 11) }
        canvas.drawRoundRect(RectF(806f, 466f, 964f, 502f), 5f, 5f, placeholderPaint)
        canvas.drawRoundRect(RectF(806f, 580f, 964f, 617f), 5f, 5f, placeholderPaint)
        canvas.drawRoundRect(RectF(799f, 675f, 977f, 787f), 7f, 7f, placeholderPaint)
        canvas.drawRoundRect(RectF(806f, 880f, 964f, 916f), 5f, 5f, placeholderPaint)
        val dataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            textSize = 27f
            typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            setShadowLayer(3f, 0f, 1f, AndroidColor.BLACK)
        }
        val dataX = 884f
        canvas.drawText(dateLabel, dataX, 490f, dataPaint)
        canvas.drawText(timeLabel, dataX, 605f, dataPaint)
        drawFittedText(canvas, session.title, RectF(785f, 660f, 990f, 780f), dataPaint, maxLines = 3)
        canvas.drawText(volumeLabel, dataX, 904f, dataPaint)
    } else {
        drawBitmapCenterCrop(canvas, photo, RectF(0f, 0f, width.toFloat(), height.toFloat()))
        val shade = Paint().apply { shader = android.graphics.LinearGradient(0f, 760f, 0f, height.toFloat(), 0x00000000, 0xE6000000.toInt(), android.graphics.Shader.TileMode.CLAMP) }
        canvas.drawRect(0f, 700f, width.toFloat(), height.toFloat(), shade)
        val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = AndroidColor.WHITE; setShadowLayer(4f, 0f, 2f, AndroidColor.BLACK) }
        white.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        white.textAlign = Paint.Align.CENTER
        white.textSize = 48f
        canvas.drawText("FITPR09  ·  TREINO CONCLUÍDO", 512f, 1040f, white)
        white.textSize = 58f
        drawFittedText(canvas, session.title, RectF(80f, 1070f, 944f, 1195f), white, maxLines = 2)
        white.textSize = 33f
        canvas.drawText("$dateLabel  ·  $timeLabel", 512f, 1245f, white)
        canvas.drawText("$volumeLabel levantados  ·  $durationLabel", 512f, 1300f, white)
        canvas.drawText("${session.estimatedCalories} kcal estimadas${if (prsCount > 0) "  ·  $prsCount PRs" else ""}", 512f, 1355f, white)
        white.textSize = 27f
        white.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
        canvas.drawText(userName.take(36), 512f, 1420f, white)
    }
    return output
}

private fun drawBitmapCenterCrop(canvas: Canvas, bitmap: Bitmap, destination: RectF) {
    val sourceRatio = bitmap.width.toFloat() / bitmap.height
    val targetRatio = destination.width() / destination.height()
    val source = if (sourceRatio > targetRatio) {
        val cropWidth = (bitmap.height * targetRatio).toInt().coerceAtLeast(1)
        val left = (bitmap.width - cropWidth) / 2
        Rect(left, 0, left + cropWidth, bitmap.height)
    } else {
        val cropHeight = (bitmap.width / targetRatio).toInt().coerceAtLeast(1)
        val top = (bitmap.height - cropHeight) / 2
        Rect(0, top, bitmap.width, top + cropHeight)
    }
    canvas.drawBitmap(bitmap, source, destination, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
}

private fun drawFittedText(canvas: Canvas, text: String, bounds: RectF, paint: Paint, maxLines: Int) {
    val words = text.trim().split(Regex("\\s+")).filter(String::isNotBlank)
    val lines = mutableListOf<String>()
    var current = ""
    for (word in words) {
        val candidate = if (current.isEmpty()) word else "$current $word"
        if (paint.measureText(candidate) <= bounds.width()) current = candidate
        else {
            if (current.isNotEmpty()) lines += current
            current = word
        }
    }
    if (current.isNotEmpty()) lines += current
    val lineHeight = paint.textSize * 1.1f
    val visible = lines.take(maxLines)
    val firstBaseline = bounds.centerY() - (visible.size - 1) * lineHeight / 2f + paint.textSize * 0.35f
    visible.forEachIndexed { index, line ->
        val clipped = if (index == maxLines - 1 && lines.size > maxLines) {
            TextUtils.ellipsize(line, android.text.TextPaint(paint), bounds.width(), TextUtils.TruncateAt.END).toString()
        } else line
        canvas.drawText(clipped, bounds.centerX(), firstBaseline + index * lineHeight, paint)
    }
}

private fun formatShareWeight(weight: Double): String =
    if (weight % 1.0 == 0.0) weight.toLong().toString() else String.format(Locale("pt", "BR"), "%.1f", weight)
