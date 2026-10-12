package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CardioType
import com.example.ui.screens.getCardioIcon
import com.example.ui.theme.*

private enum class CardioCategory(val title: String, val subtitle: String, val modes: List<CardioType>) {
    RUN("Corrida e caminhada", "Livre, esteira e trilha", listOf(CardioType.CAMINHADA_AR_LIVRE, CardioType.CAMINHADA_ESTEIRA, CardioType.CORRIDA, CardioType.CORRIDA_ESTEIRA, CardioType.TRILHA)),
    BIKE("Bicicleta", "Indoor e ao ar livre", listOf(CardioType.BICICLETA_INDOOR, CardioType.BICICLETA_AR_LIVRE)),
    BOXING("Boxe", "Saco, sombra e rounds", listOf(CardioType.BOXE_SACO, CardioType.BOXE_SOMBRA, CardioType.BOXE_INTERVALADO)),
    INTERVAL("HIIT e Tabata", "Construa seus intervalos", listOf(CardioType.HIIT)),
    OTHER("Outras atividades", "Elíptico e futebol", listOf(CardioType.ELIPTICO, CardioType.FUTEBOL))
}

fun cardioActivityAsset(type: CardioType, female: Boolean): Int = when (type) {
    CardioType.BICICLETA_INDOOR, CardioType.BICICLETA_AR_LIVRE -> if (female) R.drawable.cardio_cycling_female else R.drawable.cardio_cycling_male
    CardioType.BOXE_SACO, CardioType.BOXE_SOMBRA, CardioType.BOXE_INTERVALADO -> if (female) R.drawable.cardio_boxing_female else R.drawable.cardio_boxing_male
    CardioType.HIIT -> if (female) R.drawable.cardio_hiit_female else R.drawable.cardio_hiit_male
    else -> if (female) R.drawable.cardio_runner_female else R.drawable.cardio_runner_male
}

@Composable
fun CardioVisualLibrary(female: Boolean, onSelect: (CardioType) -> Unit) {
    var categoryName by rememberSaveable { mutableStateOf<String?>(null) }
    val category = CardioCategory.entries.firstOrNull { it.name == categoryName }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (category != null) TextButton(onClick = { categoryName = null }) { Text("‹ Todas as atividades", color = LilacAccent) }
        Text(category?.title ?: "ESCOLHA SUA ATIVIDADE", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(if (category == null) "Toque em uma imagem para explorar." else "Escolha a modalidade para configurar o treino.", color = TextSecondary)
        if (category == null) {
            CardioCategory.entries.filter { it != CardioCategory.OTHER }.chunked(2).forEach { categories ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    categories.forEach { item ->
                        CardioImageCard(item.title, item.subtitle, item.modes.first(), female, Modifier.weight(1f).testTag("cardio_category_${item.name}")) { categoryName = item.name }
                    }
                    if (categories.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            TextButton(onClick = { categoryName = CardioCategory.OTHER.name }) { Text("Outras atividades: elíptico e futebol", color = LilacAccent) }
        } else {
            category.modes.chunked(2).forEach { modes ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    modes.forEach { mode ->
                        CardioImageCard(mode.title, if (mode.supportsGps && mode != CardioType.HIIT) "Externo • GPS opcional" else if (mode == CardioType.HIIT) "Interno ou externo" else "Interno • sem GPS", mode, female, Modifier.weight(1f).testTag("card_cardio_type_${mode.name}")) { onSelect(mode) }
                    }
                    if (modes.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CardioImageCard(title: String, subtitle: String, type: CardioType, female: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(22.dp), color = Color(0xFF11131B), border = BorderStroke(1.dp, LilacAccent.copy(alpha = .4f))) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Image(painterResource(cardioActivityAsset(type, female)), contentDescription = null, modifier = Modifier.fillMaxWidth().height(130.dp), contentScale = ContentScale.Fit)
            Icon(getCardioIcon(type), contentDescription = null, tint = LilacAccent, modifier = Modifier.size(22.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, minLines = 2)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp, minLines = 2)
            Text("EXPLORAR  ›", color = LilacAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
fun CardioWorkoutShortcut(female: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth().testTag("btn_workouts_cardio"), shape = RoundedCornerShape(22.dp), color = Color(0xFF11131B), border = BorderStroke(1.dp, LilacAccent.copy(alpha = .6f))) {
        Row(Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TREINO CARDIO", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Caminhe, corra, pedale ou crie seu treino intervalado.", color = TextSecondary, fontSize = 13.sp)
                Text("INICIAR CARDIO  ›", color = LilacAccent, fontWeight = FontWeight.Bold)
            }
            Image(painterResource(if (female) R.drawable.cardio_runner_female else R.drawable.cardio_runner_male), null, Modifier.size(100.dp))
        }
    }
}
