package data

import kotlinx.serialization.Serializable

@Serializable
data class Platform(
    val id: Int,
    val name: String,
    val existsBoolean: Boolean
)