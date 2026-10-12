package com.example.domain.cardio

import org.junit.Assert.*
import org.junit.Test

class CardioIntervalPlanTest {
    @Test fun sixCyclesHaveFiveRestsAndTwentyTwoMinutes() {
        val plan = CardioIntervalPlan()
        assertEquals(1320L, plan.totalSeconds)
        assertEquals(11, plan.phases().size)
        assertEquals("CORRER", plan.progress(0).label)
        assertEquals("DESCANSAR", plan.progress(120).label)
        assertEquals(2, plan.progress(240).cycle)
        assertEquals("CORRER", plan.progress(1200).label)
        assertEquals(6, plan.progress(1200).cycle)
        assertTrue(plan.progress(1320).completed)
        assertTrue(plan.progress(2000).completed)
    }

    @Test fun delayedUpdatesDeriveTheCorrectPhaseFromElapsedTime() {
        val plan = CardioIntervalPlan()
        val progress = plan.progress(665)
        assertEquals("DESCANSAR", progress.label)
        assertEquals(55L, progress.remainingSeconds)
        assertEquals(3, progress.cycle)
    }

    @Test fun oneCycleHasNoRestAndHonorsWarmupAndCooldown() {
        val plan = CardioIntervalPlan(cycles = 1, warmupSeconds = 30, cooldownSeconds = 60)
        assertEquals(210L, plan.totalSeconds)
        assertEquals("AQUECER", plan.progress(0).label)
        assertEquals("CORRER", plan.progress(30).label)
        assertEquals("DESAQUECER", plan.progress(150).label)
        assertTrue(plan.progress(210).completed)
    }

    @Test fun zeroRestSkipsEmptyPhases() {
        val plan = CardioIntervalPlan(restSeconds = 0)
        assertEquals(720L, plan.totalSeconds)
        assertEquals(6, plan.phases().size)
        assertEquals(2, plan.progress(120).cycle)
    }

    @Test(expected = IllegalArgumentException::class) fun invalidPlanIsRejected() {
        CardioIntervalPlan(effortSeconds = 0)
    }
}
