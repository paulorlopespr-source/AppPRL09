package com.example.domain.readiness

import org.junit.Assert.*
import org.junit.Test

class ReadinessEngineTest {
 @Test fun goodCheckInKeepsReadinessHigh(){ val r=ReadinessEngine.calculate(DailyCheckIn(5,5,1,5,1),WeeklyTrainingLoad(10000.0,15,60,5.0,4,7.0),WeeklyTrainingLoad(10000.0,15,60,5.0,4,7.0));assertTrue(r.score>=80);assertFalse(r.suggestDeload) }
 @Test fun highLoadAndPoorRecoveryCanSuggestDeload(){ val baseline=WeeklyTrainingLoad(5000.0,10,30,3.0,3,6.0);val current=WeeklyTrainingLoad(15000.0,30,120,12.0,7,9.0);val r=ReadinessEngine.calculate(DailyCheckIn(1,1,5,1,5),current,baseline);assertTrue(r.suggestDeload);assertTrue(r.reasons.isNotEmpty()) }
}
