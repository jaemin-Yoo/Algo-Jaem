package data.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class PlatformGrade(
    val platformId: Int,
    val grade: String
)