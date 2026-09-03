package io.github.garoluis.anotherlifecounter.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val life: Int = 40,
    val commanderDamage: Map<Int, Int> = emptyMap()
) {
    companion object {
        const val DEFAULT_LIFE = 40
        const val DEFAULT_DAMAGE = 0
    }
}