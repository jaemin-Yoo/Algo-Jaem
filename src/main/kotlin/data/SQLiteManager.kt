package data

import java.sql.Connection
import java.sql.DriverManager

object SQLiteManager {
    @Volatile private var connection: Connection? = null

    @Synchronized
    fun getConnection(): Connection {
        if (connection == null || connection?.isClosed == true) {
            connection = DriverManager.getConnection("jdbc:sqlite:algo_jaem.db")
        }
        return connection!!
    }
}