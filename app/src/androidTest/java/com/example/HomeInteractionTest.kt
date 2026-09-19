package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.components.DayProgressStatus
import com.example.ui.components.EssentialMetricsRow
import com.example.ui.components.HomeHeader
import com.example.ui.components.HomeUiState
import com.example.ui.components.TodayWorkoutHero
import com.example.ui.components.WeeklyProgressCard
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Interaction coverage for the Home controls exposed through stable test tags. */
@RunWith(AndroidJUnit4::class)
class HomeInteractionTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun headerMenuAndNotificationInvokeTheirActions() {
        var menuClicks = 0
        var notificationClicks = 0

        composeRule.setContent {
            HomeHeader(
                userName = "Paulo",
                onMenuClick = { menuClicks++ },
                onNotificationClick = { notificationClicks++ },
                hasUnreadNotification = false
            )
        }

        composeRule.onNodeWithTag("btn_home_drawer").performClick()
        composeRule.onNodeWithTag("btn_home_notifications").performClick()

        composeRule.runOnIdle {
            assertEquals(1, menuClicks)
            assertEquals(1, notificationClicks)
        }
    }

    @Test
    fun heroUsesOfficialArtworkAndStartsPlannedWorkout() {
        var startClicks = 0

        composeRule.setContent {
            TodayWorkoutHero(
                state = HomeUiState(hasSufficientData = true, hasPlannedWorkout = true),
                onStartWorkout = { startClicks++ },
                onResumeWorkout = {},
                onResumeCardio = {},
                onSelectWorkout = {},
                onConfigureData = {}
            )
        }

        composeRule.onNodeWithContentDescription("Hero Background").assertExists()
        composeRule.onNodeWithTag("btn_hero_cta").performClick()

        composeRule.runOnIdle { assertEquals(1, startClicks) }
    }

    @Test
    fun heroRoutesMissingDataAndActiveSessionsToTheirCorrectActions() {
        var configureClicks = 0
        var resumeWorkoutClicks = 0
        var resumeCardioClicks = 0

        fun render(state: HomeUiState) {
            composeRule.setContent {
                TodayWorkoutHero(
                    state = state,
                    onStartWorkout = {},
                    onResumeWorkout = { resumeWorkoutClicks++ },
                    onResumeCardio = { resumeCardioClicks++ },
                    onSelectWorkout = {},
                    onConfigureData = { configureClicks++ }
                )
            }
        }

        render(HomeUiState(hasSufficientData = false))
        composeRule.onNodeWithTag("btn_hero_cta").performClick()
        composeRule.runOnIdle { assertEquals(1, configureClicks) }

        render(HomeUiState(isWorkoutActive = true))
        composeRule.onNodeWithTag("btn_active_session_action").performClick()
        composeRule.runOnIdle { assertEquals(1, resumeWorkoutClicks) }

        render(HomeUiState(isCardioActive = true))
        composeRule.onNodeWithTag("btn_active_session_action").performClick()
        composeRule.runOnIdle { assertEquals(1, resumeCardioClicks) }
    }

    @Test
    fun focusCheckInAndWeeklyProgressOpenTheirDestinations() {
        var checkInClicks = 0
        var progressClicks = 0

        composeRule.setContent {
            Column {
                EssentialMetricsRow(
                    streakDays = 2,
                    weeklyDone = 1,
                    weeklyGoal = 5,
                    readinessScore = null,
                    hasReadinessData = false,
                    onReadinessClick = { checkInClicks++ }
                )
                WeeklyProgressCard(
                    weeklyDone = 1,
                    weeklyGoal = 5,
                    dayStatuses = listOf(
                        DayProgressStatus("S", true, false, false),
                        DayProgressStatus("T", false, true, false)
                    ),
                    onOpenDashboard = { progressClicks++ }
                )
            }
        }

        composeRule.onNodeWithTag("card_metric_focus").performClick()
        composeRule.onNodeWithTag("card_weekly_progress").performClick()

        composeRule.runOnIdle {
            assertEquals(1, checkInClicks)
            assertEquals(1, progressClicks)
        }
    }

    @Test
    fun bottomNavigationInvokesTheSelectedDestination() {
        var selected: AppDestination? = null

        composeRule.setContent {
            MainBottomNavigation(
                currentDestination = AppDestination.HOME,
                onNavigate = { selected = it }
            )
        }

        composeRule.onNodeWithTag("tab_agenda").performClick()

        composeRule.runOnIdle { assertEquals(AppDestination.AGENDA, selected) }
    }
}
