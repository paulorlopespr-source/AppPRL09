package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.readiness.DailyCheckIn

@Composable
fun ReadinessCheckInScreen(onSave:(DailyCheckIn)->Unit){
 var sleep by remember{mutableIntStateOf(3)};var energy by remember{mutableIntStateOf(3)};var soreness by remember{mutableIntStateOf(3)};var motivation by remember{mutableIntStateOf(3)};var stress by remember{mutableIntStateOf(3)}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
  Text("Check-in de hoje",style=MaterialTheme.typography.headlineMedium);Text("Use 1 para muito baixo e 5 para muito alto. Em dor e estresse, valores maiores significam maior carga percebida.")
  Scale("Sono",sleep){sleep=it};Scale("Energia",energy){energy=it};Scale("Dor muscular",soreness){soreness=it};Scale("Motivação",motivation){motivation=it};Scale("Estresse",stress){stress=it}
  Button(onClick={onSave(DailyCheckIn(sleep,energy,soreness,motivation,stress))},modifier=Modifier.fillMaxWidth()){Text("SALVAR CHECK-IN")}
  Text("O readiness é uma orientação baseada nas regras do app e não substitui avaliação médica ou profissional.",style=MaterialTheme.typography.bodySmall)
 }
}
@Composable private fun Scale(label:String,value:Int,onChange:(Int)->Unit){Column{Text("$label: $value");Slider(value=value.toFloat(),onValueChange={onChange(it.toInt().coerceIn(1,5))},valueRange=1f..5f,steps=3)}}
