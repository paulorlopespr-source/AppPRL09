package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.components.DayBarData
import com.example.ui.components.WeeklyProgressChart
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PainelChartInteractionTest {
    @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun weeklyChartRendersSevenDaysAndOpensDetails() {
        var detailsClicks = 0
        composeRule.setContent {
            WeeklyProgressChart(
                dayBars = listOf("S", "T", "Q", "Q", "S", "S", "D").mapIndexed { index, day ->
                    DayBarData(dayLabel = day, dateEpochDay = index.toLong(), volumeKg = 100.0, hasWorkout = index < 3, intensityRatio = 0.4f + index * 0.05f)
                },
                onVerMais = { detailsClicks++ }
            )
        }
        (0..6).forEach { composeRule.onNodeWithTag("weekly_progress_bar_$it").assertExists() }
        composeRule.onNodeWithTag("btn_chart_ver_mais").performClick()
        composeRule.runOnIdle { assertEquals(1, detailsClicks) }
    }
}
