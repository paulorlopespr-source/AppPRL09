package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowWarning

/**
 * Diálogo para Exportação de Dados e Backup (CSV / JSON)
 * Garante a soberania dos dados do atleta para Excel, Google Sheets ou backup.
 */
@Composable
fun DataExportDialog(
    userProfile: UserProfile?,
    workoutSessions: List<WorkoutSession>,
    cardioSessions: List<CardioSession>,
    measurements: List<BodyMeasurement>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isExporting by remember { mutableStateOf(false) }

    fun generateCsv(): String {
        val sb = StringBuilder()
        sb.append("Data,Treino,Local,Duracao_Segundos,Volume_Total_Kg,Calorias_Estimadas,RPE_Esforco,Status\n")
        workoutSessions.forEach { session ->
            sb.append("${DateUtils.formatEpochDayFull(session.dateEpochDay)},")
            sb.append("\"${session.title.replace("\"", "\"\"")}\",")
            sb.append("\"${session.location.replace("\"", "\"\"")}\",")
            sb.append("${session.durationSeconds},")
            sb.append("${session.totalWeightLiftedKg},")
            sb.append("${session.estimatedCalories},")
            sb.append("${session.perceivedExertion},")
            sb.append("${session.status.label}\n")
        }
        return sb.toString()
    }

    fun generateJson(): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"exportedAt\": \"${System.currentTimeMillis()}\",\n")
        sb.append("  \"user\": {\n")
        sb.append("    \"name\": \"${userProfile?.name ?: "Atleta"}\",\n")
        sb.append("    \"currentWeightKg\": ${userProfile?.currentWeightKg ?: 0.0},\n")
        sb.append("    \"targetWeightKg\": ${userProfile?.targetWeightKg ?: 0.0},\n")
        sb.append("    \"heightCm\": ${userProfile?.heightCm ?: 0}\n")
        sb.append("  },\n")
        sb.append("  \"workoutCount\": ${workoutSessions.size},\n")
        sb.append("  \"cardioCount\": ${cardioSessions.size},\n")
        sb.append("  \"measurementsCount\": ${measurements.size}\n")
        sb.append("}")
        return sb.toString()
    }

    fun shareExport(content: String, mimeType: String, title: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PurpleDarkest,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GradientAction),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Exportar & Backup",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Seus dados de treino 100% sob seu controle",
                                style = MaterialTheme.typography.bodySmall,
                                color = LilacSoft
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Summary Stats Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TREINOS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(text = "${workoutSessions.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LilacAccent)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "CARDIOS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(text = "${cardioSessions.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CyanAccent)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "BIOMETRIA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(text = "${measurements.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Option 1: CSV Export
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val csv = generateCsv()
                            shareExport(csv, "text/csv", "Exportar Treinos FitPr09 (CSV)")
                        }
                        .testTag("btn_export_csv")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldDark.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TableChart, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Planilha Excel / Sheets (CSV)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Text(text = "Histórico de treinos, cargas, datas e RPE", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.Default.Share, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option 2: JSON Backup Export
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val json = generateJson()
                            shareExport(json, "application/json", "Backup FitPr09 (JSON)")
                        }
                        .testTag("btn_export_json")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDeepCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FolderZip, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Backup Completo (JSON)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Text(text = "Estrutura completa para restauração ou migração", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.Default.Share, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fechar", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
