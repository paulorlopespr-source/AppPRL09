package com.example.data.model

/** Home equipment selected by the user, with optional usual loads for reference only. */
data class HomeEquipmentProfile(
    val availableEquipment: Set<Equipment> = setOf(Equipment.PESO_CORPO),
    val barLoadKg: Double? = null,
    val dumbbellLoadPerHandKg: Double? = null,
    val configured: Boolean = false
)
