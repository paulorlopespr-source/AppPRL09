package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeEquipmentProfileTest {
    @Test
    fun equipmentLoadsAreOptionalReferencesAndDoNotApplyAFixedCap() {
        val profile = HomeEquipmentProfile(
            barLoadKg = 500.0,
            dumbbellLoadPerHandKg = 120.0
        )

        assertEquals(500.0, profile.barLoadKg!!, 0.0)
        assertEquals(120.0, profile.dumbbellLoadPerHandKg!!, 0.0)
    }

    @Test
    fun loadReferencesCanBeAbsent() {
        val profile = HomeEquipmentProfile()

        assertNull(profile.barLoadKg)
        assertNull(profile.dumbbellLoadPerHandKg)
    }
}
