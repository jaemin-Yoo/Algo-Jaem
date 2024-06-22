package data.database.dao

import data.database.entity.Platform
import java.sql.Connection
import java.sql.SQLException

class PlatformDao(private val connection: Connection) {

    fun getPlatforms(): List<Platform> {
        val sql = """
            SELECT * FROM platform
        """.trimIndent()

        val platforms = mutableListOf<Platform>()
        val statement = connection.createStatement()
        val result = statement.executeQuery(sql)
        while (result.next()) {
            val id = result.getInt("platform_id")
            val name = result.getString("name")
            val existsAlgorithm = result.getBoolean("exists_algorithm")
            platforms.add(Platform(id, name, existsAlgorithm))
        }
        return platforms
    }

    fun insertPlatform(name: String, existsAlgorithm: Boolean) {
        val sql = """
            INSERT INTO platform(name, exists_algorithm) VALUES(?, ?)
        """.trimIndent()

        var result = "성공적으로 추가하였습니다."
        try {
            val statement = connection.prepareStatement(sql)
            statement.setString(1, name)
            statement.setBoolean(2, existsAlgorithm)
            statement.executeUpdate()
        } catch (e: SQLException) {
            if (e.message!!.contains("UNIQUE constraint failed")) {
                result = "중복된 이름이 존재합니다."
            }
        }
        println(result)
    }

    fun deletePlatform(platformId: Int) {
        val sql = """
            DELETE FROM platform WHERE platform_id = ?
        """.trimIndent()

        val statement = connection.prepareStatement(sql)
        statement.setInt(1, platformId)
        statement.executeUpdate()
    }
}