package com.example.ui.components

import android.net.Uri
import android.content.Context
import androidx.core.content.FileProvider
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ProgressPhoto
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private fun createProgressCameraUri(context: Context): Uri {
    val directory = File(context.cacheDir, "progress_camera").apply { mkdirs() }
    val photo = File(directory, "progress_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photo)
}

@Composable
fun ProgressPhotosSection(
    photos: List<ProgressPhoto>,
    initialPhoto: ProgressPhoto?,
    onAddPhotoClick: (isInitial: Boolean) -> Unit,
    onPhotoClick: (ProgressPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthlyPhotos = photos.filter { !it.isInitial }
    val latestPhoto = monthlyPhotos.firstOrNull()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurpleDarkSurface)
                        .border(1.dp, LilacAccent.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Fotos de Evolução & Shape",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Comparativo do ponto de partida vs atual",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(GradientAction)
                    .clickable { onAddPhotoClick(false) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("btn_add_progress_photo"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Foto Mensal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Before & After Visual Comparison Card (if both initial & another photo exist)
        if (initialPhoto != null && latestPhoto != null) {
            BeforeAfterComparisonCard(
                initialPhoto = initialPhoto,
                latestPhoto = latestPhoto,
                onInitialClick = { onPhotoClick(initialPhoto) },
                onLatestClick = { onPhotoClick(latestPhoto) }
            )
        }

        // Initial Baseline Photo Card if not in comparison or no photo yet
        if (initialPhoto == null) {
            BentoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_upload_initial_photo")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleDarkSurface)
                                .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = LilacAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Foto Inicial (Baseline)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Registre seu ponto de partida para comparar",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PurpleDeepCard)
                            .border(1.dp, LilacAccent, RoundedCornerShape(10.dp))
                            .clickable { onAddPhotoClick(true) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("btn_upload_initial_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Enviar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LilacAccent)
                    }
                }
            }
        }

        // Monthly Progress Photo Timeline
        if (photos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Galeria de Registros (${photos.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    photos.forEach { photo ->
                        ProgressPhotoThumbnailCard(
                            photo = photo,
                            initialWeight = initialPhoto?.weightKg,
                            onClick = { onPhotoClick(photo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BeforeAfterComparisonCard(
    initialPhoto: ProgressPhoto,
    latestPhoto: ProgressPhoto,
    onInitialClick: () -> Unit,
    onLatestClick: () -> Unit
) {
    val initialWeight = initialPhoto.weightKg
    val latestWeight = latestPhoto.weightKg
    val weightDelta = if (initialWeight != null && latestWeight != null) latestWeight - initialWeight else null

    BentoCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_before_after_comparison")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = null,
                    tint = LilacAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Comparação Antes & Depois",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            if (weightDelta != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleDarkSurface)
                        .border(1.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (weightDelta >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (weightDelta >= 0) LilacAccent else EmeraldSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (weightDelta >= 0) "+${String.format(java.util.Locale.US, "%.1f", weightDelta)} kg" else "${String.format(java.util.Locale.US, "%.1f", weightDelta)} kg",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = if (weightDelta >= 0) LilacAccent else EmeraldSuccess
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Two-column side by side comparison
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // BEFORE (Initial Photo)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onInitialClick() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PurpleDarkSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(initialPhoto.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto Inicial",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 10.dp, topEnd = 0.dp, bottomStart = 0.dp),
                        color = PurplePrimary,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "ANTES (INICIAL)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (initialWeight != null) "${initialWeight}kg" else "Inicial",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = DateUtils.formatEpochDayShort(initialPhoto.dateEpochDay),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            // AFTER (Latest Photo)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLatestClick() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PurpleDarkSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(latestPhoto.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto Atual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 10.dp, topEnd = 0.dp, bottomStart = 0.dp),
                        color = EmeraldSuccess,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "DEPOIS (${latestPhoto.monthLabel.ifBlank { "ATUAL" }.uppercase()})",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (latestWeight != null) "${latestWeight}kg" else "Atual",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
                Text(
                    text = DateUtils.formatEpochDayShort(latestPhoto.dateEpochDay),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ProgressPhotoThumbnailCard(
    photo: ProgressPhoto,
    initialWeight: Double?,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PurpleDeepCard,
        border = BorderStroke(1.dp, GlassBorderSubtle),
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("progress_photo_item_${photo.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(PurpleDarkSurface)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.monthLabel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (photo.isInitial) {
                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        color = PurplePrimary,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "INICIAL",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = photo.monthLabel.ifBlank { DateUtils.formatEpochDayShort(photo.dateEpochDay) },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (photo.weightKg != null) "${photo.weightKg}kg" else "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Black,
                        color = if (photo.isInitial) LilacAccent else TextPrimary
                    )

                    if (!photo.isInitial && photo.weightKg != null && initialWeight != null) {
                        val delta = photo.weightKg - initialWeight
                        Text(
                            text = if (delta >= 0) "+${String.format(java.util.Locale.US, "%.1f", delta)}" else String.format(java.util.Locale.US, "%.1f", delta),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (delta >= 0) LilacAccent else EmeraldSuccess
                        )
                    }
                }
                Text(
                    text = DateUtils.formatEpochDayShort(photo.dateEpochDay),
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun AddProgressPhotoDialog(
    initialWeight: Double,
    isSettingInitial: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (uri: Uri, weight: Double?, monthLabel: String, isInitial: Boolean, fat: Double?, notes: String) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var weightStr by remember { mutableStateOf(initialWeight.toString()) }
    var monthLabel by remember {
        mutableStateOf(
            if (isSettingInitial) "Foto Inicial (Baseline)"
            else DateUtils.formatEpochDayToMonthYear(DateUtils.todayEpochDay())
        )
    }
    var isInitialCheck by remember { mutableStateOf(isSettingInitial) }
    var bodyFatStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            errorMessage = null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            errorMessage = null
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { selectedImageUri = cameraUri; errorMessage = null }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = createProgressCameraUri(context)
            cameraUri = uri
            cameraLauncher.launch(uri)
        } else errorMessage = "Permita o uso da câmera para tirar uma foto."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = LilacAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isInitialCheck) "Foto Inicial (Baseline)" else "Adicionar Foto de Evolução",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
                    modifier = Modifier.fillMaxWidth().testTag("btn_take_progress_photo")
                ) { Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(8.dp)); Text("Tirar foto com a câmera") }
                // Photo Picker box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurpleDarkSurface)
                        .border(
                            BorderStroke(
                                1.5.dp,
                                if (selectedImageUri != null) LilacAccent else GlassBorder
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            try {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } catch (_: Exception) {
                                galleryLauncher.launch("image/*")
                            }
                        }
                        .testTag("btn_select_photo_area"),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto selecionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alterar", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = LilacAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toque para escolher da galeria",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = LilacAccent
                            )
                            Text(
                                text = "Foto do seu corpo de frente ou lado",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = RedDestructive,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Weight input
                OutlinedTextField(
                    value = weightStr,
                    onValueChange = { weightStr = it },
                    label = { Text("Peso na Foto (kg) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_photo_weight")
                )

                // Month / Check-in label
                OutlinedTextField(
                    value = monthLabel,
                    onValueChange = { monthLabel = it },
                    label = { Text("Identificação / Mês *") },
                    placeholder = { Text("Ex: Foto Inicial, Mês 1, Agosto 2026") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_photo_month_label")
                )

                // Suggestion chips for month label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Foto Inicial", "Mês 1", "Mês 2", "Mês 3", "Mês 6", "1 Ano").forEach { chipText ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PurpleDarkSurface,
                            border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    monthLabel = chipText
                                    if (chipText == "Foto Inicial") isInitialCheck = true
                                }
                        ) {
                            Text(
                                text = chipText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Initial Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isInitialCheck = !isInitialCheck }
                ) {
                    Checkbox(
                        checked = isInitialCheck,
                        onCheckedChange = { isInitialCheck = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PurplePrimary,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("checkbox_is_initial_photo")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Foto Inicial de Referência (Baseline)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text("Usada como ponto de partida para comparação", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Optional Body Fat %
                OutlinedTextField(
                    value = bodyFatStr,
                    onValueChange = { bodyFatStr = it },
                    label = { Text("% Gordura Corporal (Opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações (Opcional)") },
                    placeholder = { Text("Ex: Em jejum pela manhã, pós-treino") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedImageUri == null) {
                        errorMessage = "Selecione uma foto da galeria antes de salvar."
                        return@Button
                    }
                    val weight = weightStr.toDoubleOrNull()
                    val fat = bodyFatStr.toDoubleOrNull()
                    onSave(selectedImageUri!!, weight, monthLabel, isInitialCheck, fat, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_confirm_save_photo")
            ) {
                Text("Salvar Foto", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun PhotoDetailDialog(
    photo: ProgressPhoto,
    initialPhoto: ProgressPhoto?,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = photo.monthLabel.ifBlank { "Registro de Evolução" },
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = DateUtils.formatEpochDayFull(photo.dateEpochDay),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.testTag("btn_delete_photo_detail")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir foto",
                        tint = RedDestructive
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // High-resolution photo container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photo.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de evolução em alta resolução",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (photo.isInitial) {
                        Surface(
                            shape = RoundedCornerShape(bottomEnd = 10.dp),
                            color = PurplePrimary,
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "FOTO INICIAL (BASELINE)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (photo.weightKg != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Peso", fontSize = 11.sp, color = TextMuted)
                            Text("${photo.weightKg} kg", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                    }

                    if (!photo.isInitial && photo.weightKg != null && initialPhoto?.weightKg != null) {
                        val delta = photo.weightKg - initialPhoto.weightKg
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Variação vs Inicial", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = if (delta >= 0) "+${String.format(java.util.Locale.US, "%.1f", delta)} kg" else "${String.format(java.util.Locale.US, "%.1f", delta)} kg",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (delta >= 0) LilacAccent else EmeraldSuccess
                            )
                        }
                    }

                    if (photo.bodyFatPercentage != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gordura", fontSize = 11.sp, color = TextMuted)
                            Text("${photo.bodyFatPercentage}%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                    }
                }

                if (photo.notes.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PurpleDarkSurface,
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📝 ${photo.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Fechar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(24.dp)
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Foto?", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = { Text("Tem certeza que deseja excluir esta foto do seu histórico?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDestructive)
                ) {
                    Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
