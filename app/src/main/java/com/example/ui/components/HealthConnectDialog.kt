package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceDeep
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientHeroPrimary
import com.example.ui.theme.GradientVibrant
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel
import com.example.util.HealthConnectAvailability
import com.example.util.HealthConnectManager

@Composable
fun HealthConnectDialog(
    viewModel: FitnessViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val healthAvailability by viewModel.healthAvailability.collectAsStateWithLifecycle()
    val permissionsGranted by viewModel.healthPermissionsGranted.collectAsStateWithLifecycle()
    val dailyMetrics by viewModel.healthDailyMetrics.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isHealthSyncing.collectAsStateWithLifecycle()
    val syncResultMessage by viewModel.healthSyncResultMessage.collectAsStateWithLifecycle()

    var showGuide by remember { mutableStateOf(false) }

    // Launcher for Health Connect permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = viewModel.healthConnectManager.createPermissionResultContract()
    ) { grantedPermissions ->
        val allGranted = HealthConnectManager.REQUIRED_PERMISSIONS.all { it in grantedPermissions }
        viewModel.updateHealthPermissionsGranted(allGranted)
        if (allGranted) {
            Toast.makeText(context, "Permissões do Health Connect concedidas com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Algumas permissões não foram concedidas.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkHealthConnectStatus()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
            color = PurpleDarkest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PurpleDarkSurface,
                                PurpleDarkest
                            )
                        )
                    )
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(PurpleVibrant, LilacAccent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Smartwatch & Health Connect",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Conexão de Saúde Google / Android",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LilacAccent
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GlassSurfaceDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // Status Banner
                    item {
                        HealthConnectStatusCard(
                            availability = healthAvailability,
                            permissionsGranted = permissionsGranted,
                            onGrantPermissions = {
                                permissionLauncher.launch(HealthConnectManager.REQUIRED_PERMISSIONS)
                            },
                            onOpenHealthConnectSettings = {
                                try {
                                    context.startActivity(viewModel.healthConnectManager.getOpenHealthConnectIntent())
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Não foi possível abrir as configurações do Health Connect", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onInstallHealthConnect = {
                                try {
                                    context.startActivity(viewModel.healthConnectManager.getInstallHealthConnectIntent())
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Google Play Store não encontrada", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    // Synced Daily Metrics Section
                    item {
                        Text(
                            text = "MÉTRICAS SINCRONIZADAS HOJE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricSyncCard(
                                modifier = Modifier.weight(1f),
                                title = "Passos",
                                value = if (dailyMetrics.steps > 0) "%,d".format(dailyMetrics.steps) else "0",
                                unit = "passos",
                                icon = Icons.Default.DirectionsWalk,
                                iconTint = LilacAccent,
                                badgeText = "Smartwatch"
                            )

                            MetricSyncCard(
                                modifier = Modifier.weight(1f),
                                title = "Calorias Queimadas",
                                value = if (dailyMetrics.totalCaloriesBurned > 0) "${dailyMetrics.totalCaloriesBurned.toInt()}" else "0",
                                unit = "kcal gastas",
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = Color(0xFFFF7043),
                                badgeText = "Total dia"
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricSyncCard(
                                modifier = Modifier.weight(1f),
                                title = "Batimentos",
                                value = dailyMetrics.averageHeartRateBpm?.let { "$it" } ?: "--",
                                unit = "bpm médio",
                                icon = Icons.Default.Favorite,
                                iconTint = Color(0xFFEF5350),
                                badgeText = "Sensor de Pulso"
                            )

                            MetricSyncCard(
                                modifier = Modifier.weight(1f),
                                title = "Peso Corporal",
                                value = dailyMetrics.latestWeightKg?.let { "%.1f".format(it) } ?: "--",
                                unit = "kg (Balança)",
                                icon = Icons.Default.MonitorWeight,
                                iconTint = EmeraldSuccess,
                                badgeText = "Bioimpedância"
                            )
                        }
                    }

                    // Sync Result Banner if exists
                    if (syncResultMessage != null) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = EmeraldDark.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = syncResultMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.dismissHealthSyncMessage() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Fechar",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Synchronization Actions
                    item {
                        Text(
                            text = "AÇÕES DE SINCRONIZAÇÃO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = GlassSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Full Sync Button
                                Button(
                                    onClick = {
                                        if (permissionsGranted) {
                                            viewModel.syncAllWorkoutsWithHealthConnect()
                                        } else {
                                            permissionLauncher.launch(HealthConnectManager.REQUIRED_PERMISSIONS)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PurpleVibrant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSyncing
                                ) {
                                    if (isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Sincronizando histórico...", color = Color.White)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Sync,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Sincronizar Todos os Treinos com Relógio",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Refresh metrics button
                                    Button(
                                        onClick = { viewModel.refreshHealthDailyMetrics() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PurpleDeepCard
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = LilacAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Ler Relógio",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }

                                    // Open Health Connect Settings
                                    Button(
                                        onClick = {
                                            try {
                                                context.startActivity(viewModel.healthConnectManager.getOpenHealthConnectIntent())
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Configurações do Health Connect não encontradas", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PurpleDeepCard
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Permissões",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Smartwatch Integration Guide Collapsible
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                                .clickable { showGuide = !showGuide },
                            color = GlassSurfaceDeep
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = LilacAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Como conectar seu Smartwatch ao App",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                    }

                                    Icon(
                                        imageVector = if (showGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                AnimatedVisibility(
                                    visible = showGuide,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(top = 14.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        WatchBrandGuideItem(
                                            brand = "⌚ Galaxy Watch (Samsung Health)",
                                            steps = "1. Abra o app Samsung Health no celular\n2. Vá em Configurações (ícone de engrenagem) > Conexão de Saúde (Health Connect)\n3. Ative 'Permitir tudo' para sincronizar passos, batimentos cardíacos e treinos automaticamente."
                                        )

                                        WatchBrandGuideItem(
                                            brand = "🏃 Garmin Watch (Garmin Connect)",
                                            steps = "1. Abra o app Garmin Connect\n2. Menu Mais > Configurações > Aplicativos Conectados\n3. Selecione 'Health Connect' e conceda permissões de leitura/escrita."
                                        )

                                        WatchBrandGuideItem(
                                            brand = "🎯 Pixel Watch / Wear OS / Fitbit",
                                            steps = "1. Abra o app Fitbit ou Google Fit\n2. Vá em Perfil > Conectar ao Health Connect\n3. Os dados de batimentos cardíacos em tempo real e passos serão lidos diretamente pelo FitPr09."
                                        )

                                        WatchBrandGuideItem(
                                            brand = "📱 Xiaomi / Amazfit / Zepp",
                                            steps = "1. Abra o app Zepp Life ou Mi Fitness\n2. Vá na aba Perfil > Compartilhamento e Contas > Health Connect\n3. Habilite a sincronização contínua de atividades e calorias."
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = PurpleDeepCard,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "🔄 Fluxo Bidirecional: Quando você finaliza um treino no FitPr09, os dados de volume, séries e gasto calórico são gravados automaticamente no Health Connect e refletem no seu relógio!",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = LilacSoft,
                                                    lineHeight = 18.sp
                                                ),
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthConnectStatusCard(
    availability: HealthConnectAvailability,
    permissionsGranted: Boolean,
    onGrantPermissions: () -> Unit,
    onOpenHealthConnectSettings: () -> Unit,
    onInstallHealthConnect: () -> Unit
) {
    val (statusTitle, statusDesc, badgeText, badgeColor, icon) = when {
        availability == HealthConnectAvailability.NOT_SUPPORTED -> {
            Tuple5(
                "Health Connect não suportado",
                "Seu dispositivo não possui suporte ao subsistema Health Connect.",
                "Não Suportado",
                Color(0xFFEF5350),
                Icons.Default.Warning
            )
        }
        availability == HealthConnectAvailability.NOT_INSTALLED -> {
            Tuple5(
                "Health Connect não instalado",
                "Instale o aplicativo oficial do Google Health Connect para sincronizar com seu relógio.",
                "Instalação Necessária",
                Color(0xFFFFB74D),
                Icons.Default.Warning
            )
        }
        !permissionsGranted -> {
            Tuple5(
                "Permissões Pendentes",
                "Conceda permissão para ler passos/frequência cardíaca e salvar seus treinos no relógio.",
                "Requer Autorização",
                Color(0xFFFFB74D),
                Icons.Default.Settings
            )
        }
        else -> {
            Tuple5(
                "Smartwatch & Health Connect Ativos",
                "Sincronização bidirecional em tempo real com seu relógio inteligente e Google Health.",
                "Conectado & Sincronizado",
                EmeraldSuccess,
                Icons.Default.CheckCircle
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = GlassSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (permissionsGranted) EmeraldSuccess.copy(alpha = 0.5f) else GlassBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = statusTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = statusDesc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            )

            // Button when action is needed
            if (availability == HealthConnectAvailability.NOT_INSTALLED) {
                Button(
                    onClick = onInstallHealthConnect,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleVibrant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Instalar no Google Play", color = Color.White)
                }
            } else if (!permissionsGranted && availability == HealthConnectAvailability.AVAILABLE) {
                Button(
                    onClick = onGrantPermissions,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleVibrant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Autorizar Permissões de Saúde", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MetricSyncCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconTint: Color,
    badgeText: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = GlassSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun WatchBrandGuideItem(
    brand: String,
    steps: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = brand,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = LilacAccent
            )
        )
        Text(
            text = steps,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        )
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
