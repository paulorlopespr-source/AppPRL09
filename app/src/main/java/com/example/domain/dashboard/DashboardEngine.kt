package com.example.domain.dashboard

import com.example.data.model.*
import java.time.DayOfWeek
import java.time.LocalDate

data class MuscleVolume(val muscleGroup:String,val sets:Int,val volumeKg:Double,val previousWeekVolumeKg:Double){ val weeklyChangePercent:Double? get()=if(previousWeekVolumeKg>0)((volumeKg-previousWeekVolumeKg)/previousWeekVolumeKg)*100 else null }
data class EvolutionTrend(val currentWeightKg:Double?,val weightChangeKg:Double?,val measurementsCount:Int,val photosCount:Int,val workoutFrequency:Int,val workoutVolumeKg:Double,val cardioDistanceKm:Double,val cardioMinutes:Int)
data class DashboardSummary(val todayWorkout:WorkoutSession?,val weeklyWorkouts:Int,val weeklyGoal:Int,val streakDays:Int,val currentWeightKg:Double?,val weeklyVolumeKg:Double,val weeklyCardioKm:Double)

object DashboardEngine {
 fun home(profile:UserProfile?,workouts:List<WorkoutSession>,cardio:List<CardioSession>,measurements:List<BodyMeasurement>,today:LocalDate=LocalDate.now()):DashboardSummary{
  val start=today.with(DayOfWeek.MONDAY).toEpochDay(); val end=today.toEpochDay(); val week=workouts.filter{it.dateEpochDay in start..end&&it.status==SessionStatus.COMPLETED}; val wc=cardio.filter{it.dateEpochDay in start..end}
  return DashboardSummary(workouts.firstOrNull{it.dateEpochDay==end&&it.status!=SessionStatus.COMPLETED},week.size,profile?.weeklyGoalDays?:0,streak(workouts,cardio,today),measurements.maxByOrNull{it.dateEpochDay}?.weightKg?:profile?.currentWeightKg,week.sumOf{it.totalWeightLiftedKg},wc.sumOf{it.distanceKm?:0.0})
 }
 fun evolution(workouts:List<WorkoutSession>,cardio:List<CardioSession>,measurements:List<BodyMeasurement>,photos:List<ProgressPhoto>,days:Long=28,today:LocalDate=LocalDate.now()):EvolutionTrend{
  val from=today.minusDays(days).toEpochDay(); val m=measurements.filter{it.dateEpochDay>=from}.sortedBy{it.dateEpochDay}; val w=workouts.filter{it.dateEpochDay>=from&&it.status==SessionStatus.COMPLETED}; val c=cardio.filter{it.dateEpochDay>=from}
  return EvolutionTrend(m.lastOrNull()?.weightKg,if(m.size>=2)m.last().weightKg-m.first().weightKg else null,m.size,photos.count{it.dateEpochDay>=from},w.size,w.sumOf{it.totalWeightLiftedKg},c.sumOf{it.distanceKm?:0.0},c.sumOf{it.durationMinutes})
 }
 fun muscleVolume(records:List<ExerciseExecutionRecord>,today:LocalDate=LocalDate.now()):List<MuscleVolume>{ val start=today.with(DayOfWeek.MONDAY).toEpochDay(); return records.groupBy{it.muscleGroup}.map{(g,a)-> val cur=a.filter{it.sessionDateEpochDay in start..today.toEpochDay()}.flatMap{it.completedSets}; val prev=a.filter{it.sessionDateEpochDay in (start-7)..(start-1)}.flatMap{it.completedSets}; MuscleVolume(g,cur.size,cur.sumOf{it.weightKg*it.reps},prev.sumOf{it.weightKg*it.reps})}.sortedByDescending{it.volumeKg} }
 private fun streak(workouts:List<WorkoutSession>,cardio:List<CardioSession>,today:LocalDate):Int{ val days=(workouts.filter{it.status==SessionStatus.COMPLETED}.map{it.dateEpochDay}+cardio.map{it.dateEpochDay}).toSet(); var d=today.toEpochDay(); var n=0;if(d !in days)d--;while(d in days){n++;d--};return n }
}
