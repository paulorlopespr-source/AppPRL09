package com.example.data.repository

import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutSessionWritePolicyTest {
    private val completed = WorkoutSession(
        id = 42,
        title = "Treino A",
        dateEpochDay = 20_000,
        status = SessionStatus.COMPLETED
    )

    @Test
    fun `late autosave cannot downgrade completed workout`() {
        val lateDraft = completed.copy(status = SessionStatus.IN_PROGRESS)

        assertTrue(isStaleWorkoutDraft(completed, lateDraft))
    }

    @Test
    fun `draft remains writable before workout completion`() {
        val inProgress = completed.copy(status = SessionStatus.IN_PROGRESS)
        val nextDraft = inProgress.copy(durationSeconds = 120)

        assertFalse(isStaleWorkoutDraft(inProgress, nextDraft))
    }

    @Test
    fun `completed session can be newly inserted`() {
        assertFalse(isStaleWorkoutDraft(null, completed))
    }
}
