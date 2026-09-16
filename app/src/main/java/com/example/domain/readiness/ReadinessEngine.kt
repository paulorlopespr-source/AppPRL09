package com.example.domain.readiness

import kotlin.math.roundToInt

data class DailyCheckIn(val sleep:Int,val energy:Int,val soreness:Int,val motivation:Int,val stress:Int,val timestampMillis:Long=System.currentTimeMillis()){
 init { require(listOf(sleep,energy,soreness,motivation,stress).all{it in 1..5}) }
}
data class WeeklyTrainingLoad(val strengthVolumeKg:Double,val strengthSets:Int,val cardioMinutes:Int,val cardioDistanceKm:Double,val sessions:Int,val averageRpe:Double?)
data class ReadinessResult(val score:Int,val label:String,val reasons:List<String>,val recommendation:String,val suggestDeload:Boolean)

data class ReadinessRules(val highLoadRatio:Double=1.25,val deloadLoadRatio:Double=1.45,val lowWellbeingThreshold:Double=2.4,val deloadScoreThreshold:Int=45)

object ReadinessEngine {
 fun calculate(checkIn:DailyCheckIn,current:WeeklyTrainingLoad,baseline:WeeklyTrainingLoad?,rules:ReadinessRules=ReadinessRules()):ReadinessResult{
  val wellbeing=((checkIn.sleep+checkIn.energy+checkIn.motivation+(6-checkIn.soreness)+(6-checkIn.stress))/25.0)*100.0
  val loadRatio=baseline?.let{ b -> val base=loadPoints(b); if(base>0) loadPoints(current)/base else 1.0 }?:1.0
  var score=wellbeing.roundToInt(); val reasons=mutableListOf<String>()
  if(checkIn.sleep<=2){score-=10;reasons+="Sono abaixo do habitual"}
  if(checkIn.energy<=2){score-=10;reasons+="Energia baixa"}
  if(checkIn.soreness>=4){score-=10;reasons+="Dor muscular elevada"}
  if(checkIn.stress>=4){score-=8;reasons+="Estresse elevado"}
  if(loadRatio>=rules.highLoadRatio){score-=10;reasons+="Carga semanal ${"%.0f".format((loadRatio-1)*100)}% acima da referência"}
  if(current.averageRpe?.let{it>=9.0}==true){score-=8;reasons+="Esforço percebido médio muito alto"}
  score=score.coerceIn(0,100)
  val deload=loadRatio>=rules.deloadLoadRatio || (score<=rules.deloadScoreThreshold && wellbeing/20.0<=rules.lowWellbeingThreshold)
  val label=when{score>=80->"Pronto";score>=60->"Atenção";score>=45->"Recuperação";else->"Carga reduzida"}
  val recommendation=when{deload->"Considere reduzir temporariamente volume e/ou intensidade e priorizar recuperação.";score<60->"Treine de forma conservadora hoje e reavalie como você se sente durante a sessão.";else->"Os indicadores atuais são compatíveis com a programação normal."}
  return ReadinessResult(score,label,reasons.ifEmpty{listOf("Check-in e carga dentro das faixas configuradas")},recommendation,deload)
 }
 fun loadPoints(load:WeeklyTrainingLoad):Double { val rpeFactor=(load.averageRpe?:6.0)/6.0; return (load.strengthSets*10.0 + load.strengthVolumeKg/100.0 + load.cardioMinutes*2.0 + load.cardioDistanceKm*5.0 + load.sessions*15.0)*rpeFactor }
}
