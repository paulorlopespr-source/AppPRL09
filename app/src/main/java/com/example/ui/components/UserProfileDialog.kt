package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.FitnessGoal
import com.example.data.model.GamificationOverview
import com.example.data.model.UserProfile
import com.example.util.PhotoStorageHelper
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.GradientCardDeep
import com.example.ui.theme.GradientHeroPrimary
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
import java.io.File

/**
 * Enhanced Athlete Avatar Component supporting local file paths and URIs,
 * with fallback to stylized letter initials.
 */
@Composable
fun UserAvatarView(
    photoUri: String?,
    userName: String,
    size: Dp = 44.dp,
    onClick: (() -> Unit)? = null,
    showEditBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageModel = remember(photoUri) {
        if (!photoUri.isNullOrBlank()) {
            if (photoUri.startsWith("/")) File(photoUri) else Uri.parse(photoUri)
        } else null
    }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurplePrimary.copy(alpha = 0.8f), PurpleDeepCard)
                    )
                )
                .border(1.5.dp, LilacAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil de $userName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                )
            } else {
                val initial = userName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "A"
                Text(
                    text = initial,
                    fontSize = (size.value * 0.44f).sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        if (showEditBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((size.value * 0.35f).coerceAtLeast(18f).dp)
                    .clip(CircleShape)
                    .background(GradientAction)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Alterar foto",
                    tint = Color.White,
                    modifier = Modifier.size((size.value * 0.22f).coerceAtLeast(11f).dp)
                )
            }
        }
    }
}

/**
 * Full Athlete Profile Menu & Edit Sheet.
 * Allows viewing complete athlete stats, modifying name, photo, weights, goals, and triggering quick tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileMenuSheet(
    userProfile: UserProfile,
    gamificationOverview: GamificationOverview? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onOpenEditDialog: () -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: () -> Unit,
    onOpenHealthConnect: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onOpenDataExport: () -> Unit = {},
    onNavigateToEvolution: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    val contentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PurpleDarkSurface,
        contentColor = TextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(LilacSoft.copy(alpha = 0.4f))
            )
        },
        modifier = modifier.testTag("sheet_user_profile_menu")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Title & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleDeepCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Menu do Usuário",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Gerencie seu perfil, foto e preferências",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar Menu",
                        tint = TextMuted
                    )
                }
            }

            // Athlete Main Card (Hero)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = PurpleDeepCard,
                border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar with Photo Action
                    Box(contentAlignment = Alignment.BottomEnd) {
                        UserAvatarView(
                            photoUri = userProfile.photoUri,
                            userName = userProfile.name,
                            size = 84.dp,
                            showEditBadge = true,
                            onClick = {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (_: Exception) {
                                    contentPickerLauncher.launch("image/*")
                                }
                            }
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = userProfile.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🎯 ${userProfile.goal.label}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent
                            )
                            Text(text = "•", color = TextMuted)
                            Text(
                                text = "📍 ${userProfile.defaultGymLocation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Photo Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (_: Exception) {
                                    contentPickerLauncher.launch("image/*")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.6f)),
                            modifier = Modifier.testTag("btn_menu_change_photo")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = LilacAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (userProfile.photoUri != null) "Trocar Foto" else "Adicionar Foto",
                                color = LilacAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (userProfile.photoUri != null) {
                            OutlinedButton(
                                onClick = onPhotoRemoved,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, RedDestructive.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("btn_menu_remove_photo")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = RedDestructive,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Remover",
                                    color = RedDestructive,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Quick Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PurpleDarkSurface.copy(alpha = 0.8f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileMetricBadge(
                            label = "Peso Atual",
                            value = "${userProfile.currentWeightKg} kg",
                            color = LilacAccent
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(GlassBorderSubtle)
                        )
                        ProfileMetricBadge(
                            label = "Meta",
                            value = "${userProfile.targetWeightKg} kg",
                            color = EmeraldSuccess
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(GlassBorderSubtle)
                        )
                        ProfileMetricBadge(
                            label = "Frequência",
                            value = "${userProfile.weeklyGoalDays}d / sem",
                            color = CyanAccent
                        )
                    }

                    // Edit Profile Button
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenEditDialog()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_menu_edit_profile_full"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Editar Dados do Perfil",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Quick Tools & Settings Section
            Text(
                text = "Ferramentas & Integrações",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileOptionItem(
                    title = "Evolução & Medidas Corporais",
                    subtitle = "Acompanhar peso, fotos de antes/depois e circunferências",
                    icon = Icons.Default.TrendingUp,
                    tint = EmeraldSuccess,
                    onClick = {
                        onDismiss()
                        onNavigateToEvolution()
                    }
                )

                ProfileOptionItem(
                    title = "Smartwatch & Health Connect",
                    subtitle = "Sincronização com relógio, batimentos e calorias",
                    icon = Icons.Default.Watch,
                    tint = LilacAccent,
                    onClick = {
                        onDismiss()
                        onOpenHealthConnect()
                    }
                )

                ProfileOptionItem(
                    title = "Lembretes & Notificações de Treino",
                    subtitle = "Alarmes inteligentes e mensagens motivacionais de voz",
                    icon = Icons.Default.Notifications,
                    tint = AmberWarning,
                    onClick = {
                        onDismiss()
                        onOpenReminders()
                    }
                )

                ProfileOptionItem(
                    title = "Exportar & Fazer Backup dos Dados",
                    subtitle = "Gerar arquivos CSV e JSON com todos os treinos e registros",
                    icon = Icons.Default.Share,
                    tint = CyanAccent,
                    onClick = {
                        onDismiss()
                        onOpenDataExport()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileMetricBadge(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}

@Composable
private fun ProfileOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = PurpleDeepCard,
        border = BorderStroke(1.dp, GlassBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Dedicated Profile Editing Dialog.
 * Direct input for Name, Photo, Goals, Weights, Height, Age, Weekly Goal Days, and Default Gym.
 */
@Composable
fun UserProfileDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (UserProfile) -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile.name) }
    var currentW by remember { mutableStateOf(userProfile.currentWeightKg.toString()) }
    var startingW by remember { mutableStateOf(userProfile.startingWeightKg.toString()) }
    var targetW by remember { mutableStateOf(userProfile.targetWeightKg.toString()) }
    var heightStr by remember { mutableStateOf(userProfile.heightCm.toString()) }
    var ageStr by remember { mutableStateOf(userProfile.age.toString()) }
    var selectedGoal by remember { mutableStateOf(userProfile.goal) }
    var weeklyDays by remember { mutableStateOf(userProfile.weeklyGoalDays.toString()) }
    var defaultGym by remember { mutableStateOf(userProfile.defaultGymLocation) }
    var currentPhotoPath by remember { mutableStateOf(userProfile.photoUri) }
    val context = LocalContext.current

    // Media picker launcher with permanent local storage
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = PhotoStorageHelper.saveImageToInternalStorage(
                context = context,
                sourceUri = uri,
                directoryName = "profile_avatar",
                prefix = "user_avatar"
            )
            currentPhotoPath = permanentPath
            onPhotoSelected(uri)
        }
    }

    // Fallback document/content picker with permanent local storage
    val contentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = PhotoStorageHelper.saveImageToInternalStorage(
                context = context,
                sourceUri = uri,
                directoryName = "profile_avatar",
                prefix = "user_avatar"
            )
            currentPhotoPath = permanentPath
            onPhotoSelected(uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = LilacAccent
                )
                Text(
                    text = "Editar Perfil do Atleta",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 19.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Photo Picker Section
                Box(
                    modifier = Modifier.padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserAvatarView(
                            photoUri = currentPhotoPath,
                            userName = name.ifBlank { "Atleta" },
                            size = 88.dp,
                            showEditBadge = true,
                            onClick = {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (_: Exception) {
                                    contentPickerLauncher.launch("image/*")
                                }
                            }
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    } catch (_: Exception) {
                                        contentPickerLauncher.launch("image/*")
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("btn_select_user_photo")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = LilacAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentPhotoPath != null) "Trocar Foto" else "Enviar Foto",
                                    color = LilacAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (currentPhotoPath != null) {
                                IconButton(
                                    onClick = {
                                        currentPhotoPath = null
                                        onPhotoRemoved()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover Foto",
                                        tint = RedDestructive,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Atleta") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_name")
                )

                // Goal Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Objetivo Principal:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    FitnessGoal.values().forEach { fg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedGoal == fg) PurpleDeepCard else PurpleDarkSurface,
                            border = BorderStroke(
                                1.dp,
                                if (selectedGoal == fg) LilacAccent else GlassBorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGoal = fg }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fg.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = fg.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                if (selectedGoal == fg) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = LilacAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // Weights row (Atual / Inicial / Alvo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startingW,
                        onValueChange = { startingW = it },
                        label = { Text("P. Inicial (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = currentW,
                        onValueChange = { currentW = it },
                        label = { Text("P. Atual (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetW,
                        onValueChange = { targetW = it },
                        label = { Text("P. Alvo (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Height and Age
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = heightStr,
                        onValueChange = { heightStr = it },
                        label = { Text("Altura (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Idade") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Weekly days and Default gym
                OutlinedTextField(
                    value = weeklyDays,
                    onValueChange = { weeklyDays = it },
                    label = { Text("Meta Semanal (dias de treino: 1-7)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = defaultGym,
                    onValueChange = { defaultGym = it },
                    label = { Text("Academia / Local Padrão") },
                    singleLine = true,
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
                    val updated = userProfile.copy(
                        name = name.ifBlank { "Atleta" },
                        photoUri = currentPhotoPath,
                        startingWeightKg = startingW.toDoubleOrNull() ?: userProfile.startingWeightKg,
                        currentWeightKg = currentW.toDoubleOrNull() ?: userProfile.currentWeightKg,
                        targetWeightKg = targetW.toDoubleOrNull() ?: userProfile.targetWeightKg,
                        heightCm = heightStr.toDoubleOrNull() ?: userProfile.heightCm,
                        age = ageStr.toIntOrNull() ?: userProfile.age,
                        goal = selectedGoal,
                        weeklyGoalDays = (weeklyDays.toIntOrNull() ?: userProfile.weeklyGoalDays).coerceIn(1, 7),
                        defaultGymLocation = defaultGym.ifBlank { "Academia Smart Fit" }
                    )
                    onSaveProfile(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_save_user_profile")
            ) {
                Text("Salvar Perfil", fontWeight = FontWeight.Bold, color = Color.White)
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
