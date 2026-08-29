package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MealAnalysisResult
import com.example.data.model.MealLog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceDeep
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowWarning
import com.example.ui.viewmodel.FitnessViewModel

/**
 * Top-level entry point for AI Nutrition Dialog connecting to ViewModel
 */
@Composable
fun AINutritionDialog(
    viewModel: FitnessViewModel,
    onDismiss: () -> Unit
) {
    val isAnalyzing by viewModel.isAnalyzingMeal.collectAsStateWithLifecycle()
    val lastAnalysis by viewModel.lastMealAnalysis.collectAsStateWithLifecycle()

    AIFoodNutritionDialog(
        isLoading = isAnalyzing,
        analysisResult = lastAnalysis,
        onAnalyzeMeal = { desc, mealType ->
            viewModel.analyzeMealWithGemini(desc, mealType)
        },
        onSaveMealLog = { mealLog ->
            viewModel.saveMealLog(mealLog)
            onDismiss()
        },
        onDismiss = onDismiss
    )
}

/**
 * AI Meal & Calorie Estimator Dialog using Gemini API
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AIFoodNutritionDialog(
    isLoading: Boolean,
    analysisResult: MealAnalysisResult?,
    onAnalyzeMeal: (mealText: String, mealType: String) -> Unit,
    onSaveMealLog: (MealLog) -> Unit,
    onDismiss: () -> Unit
) {
    var mealDescription by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Almoço") }
    var mealTypeExpanded by remember { mutableStateOf(false) }

    val mealTypes = listOf("Café da Manhã", "Almoço", "Pré-Treino", "Pós-Treino", "Jantar", "Lanche/Ceia")
    val quickPresets = listOf(
        "3 ovos mexidos, 2 fatias pão integral e café",
        "150g frango grelhado, 150g arroz, feijão e salada",
        "1 scoop Whey Protein, 1 banana, 30g aveia e leite",
        "180g patinho moído, 200g batata doce e brócolis",
        "Tapioca com 2 ovos e queijo minas"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .height(680.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, GlassBorder, RoundedCornerShape(24.dp)),
            color = PurpleDarkest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                .background(Brush.linearGradient(listOf(PurpleVibrant, LilacAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nutrição & IA Calórica",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Estimativa inteligente com Gemini API",
                                style = MaterialTheme.typography.labelSmall,
                                color = LilacSoft
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_nutrition_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Meal Type selector
                    ExposedDropdownMenuBox(
                        expanded = mealTypeExpanded,
                        onExpandedChange = { mealTypeExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedMealType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Refeição", color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = GlassSurfaceDark,
                                unfocusedContainerColor = GlassSurfaceDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = mealTypeExpanded,
                            onDismissRequest = { mealTypeExpanded = false },
                            modifier = Modifier.background(PurpleDeepCard)
                        ) {
                            mealTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = TextPrimary) },
                                    onClick = {
                                        selectedMealType = type
                                        mealTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Food Description Input
                    OutlinedTextField(
                        value = mealDescription,
                        onValueChange = { mealDescription = it },
                        label = { Text("Descreva o que você comeu ou vai comer", color = TextSecondary) },
                        placeholder = { Text("Ex: 3 ovos, 2 fatias de pão com queijo e suco de laranja", color = TextMuted) },
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = GlassSurfaceDark,
                            unfocusedContainerColor = GlassSurfaceDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_meal_description")
                    )

                    // Quick presets chips
                    Text(
                        text = "Exemplos Rápidos:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPresets.forEach { preset ->
                            Surface(
                                color = GlassSurfaceDark,
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                                modifier = Modifier.clickable { mealDescription = preset }
                            ) {
                                Text(
                                    text = preset,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LilacSoft,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Calculate Button
                    PrimaryButton(
                        text = if (isLoading) "Analisando com Gemini..." else "Calcular Calorias & Macros ✨",
                        enabled = mealDescription.isNotBlank() && !isLoading,
                        onClick = { onAnalyzeMeal(mealDescription, selectedMealType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_analyze_meal_gemini")
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = LilacAccent)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Consultando Inteligência Nutricional...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LilacSoft
                                )
                            }
                        }
                    }

                    // Results Card
                    if (analysisResult != null && !isLoading) {
                        BentoCard(
                            backgroundColor = PurpleDeepCard,
                            borderColor = LilacAccent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = analysisResult.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Classificação: ${analysisResult.healthRating}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldSuccess,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        color = LilacAccent.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent)
                                    ) {
                                        Text(
                                            text = "${analysisResult.estimatedCalories} kcal",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = LilacAccent,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Macros breakdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MacroPill(label = "Proteína", value = "${analysisResult.proteinGrams.toInt()}g", color = EmeraldSuccess)
                                    MacroPill(label = "Carboidratos", value = "${analysisResult.carbsGrams.toInt()}g", color = CyanAccent)
                                    MacroPill(label = "Gorduras", value = "${analysisResult.fatsGrams.toInt()}g", color = YellowWarning)
                                    MacroPill(label = "Fibras", value = "${analysisResult.fiberGrams.toInt()}g", color = LilacSoft)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = analysisResult.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )

                                if (analysisResult.suggestions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    analysisResult.suggestions.forEach { sug ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Text(text = "• ", color = LilacAccent, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = sug,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = LilacSoft
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                PrimaryButton(
                                    text = "Salvar no Diário de Hoje 💾",
                                    onClick = {
                                        val today = java.time.LocalDate.now().toEpochDay()
                                        val mealLog = MealLog(
                                            dateEpochDay = today,
                                            mealType = selectedMealType,
                                            description = mealDescription,
                                            estimatedCalories = analysisResult.estimatedCalories,
                                            proteinGrams = analysisResult.proteinGrams,
                                            carbsGrams = analysisResult.carbsGrams,
                                            fatsGrams = analysisResult.fatsGrams,
                                            fiberGrams = analysisResult.fiberGrams,
                                            aiInsight = analysisResult.summary,
                                            healthRating = analysisResult.healthRating
                                        )
                                        onSaveMealLog(mealLog)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_save_meal_log")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroPill(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GlassSurfaceDark)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
