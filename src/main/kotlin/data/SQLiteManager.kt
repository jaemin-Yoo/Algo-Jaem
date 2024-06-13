package data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object SQLiteManager {
    @Volatile private var connection: Connection? = null

    init {
        initializeDatabase()
    }

    @Synchronized
    fun getConnection(): Connection {
        if (connection == null || connection?.isClosed == true) {
            connection = DriverManager.getConnection("jdbc:sqlite:algo_jaem.db")
        }
        return connection!!
    }

    private fun initializeDatabase() {
        try {
            getConnection().use { conn ->
                enableForeignKey(conn)
                createTables(conn)
            }
        } catch (e: SQLException) {
            println("Error initializing database: ${e.message}")
        }
    }

    private fun enableForeignKey(conn: Connection) {
        conn.createStatement().use { stmt ->
            stmt.execute("PRAGMA foreign_keys = ON;")
        }
    }

    private fun createTables(conn: Connection) {
        val sqlCreateStatements = listOf(
            """
                CREATE TABLE IF NOT EXISTS platform (
                    platform_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    exists_algorithm BOOLEAN NOT NULL
                );
            """.trimIndent(),
            """
                CREATE TABLE IF NOT EXISTS platform_grade (
                    platform_id INTEGER,
                    grade TEXT,
                    PRIMARY KEY (platform_id, grade),
                    FOREIGN KEY(platform_id) REFERENCES platform(platform_id)
                );                
            """.trimIndent(),
            """
                CREATE TABLE IF NOT EXISTS algorithm (
                    algorithm_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    platform_id INTEGER,
                    name TEXT NOT NULL,
                    url TEXT NOT NULL,
                    UNIQUE (platform_id, name),
                    FOREIGN KEY(platform_id) REFERENCES platform(platform_id)
                );
            """.trimIndent(),
            """
                CREATE TABLE IF NOT EXISTS algorithm_grade (
                    algorithm_id INTEGER,
                    platform_id INTEGER,
                    grade TEXT,
                    PRIMARY KEY (algorithm_id, platform_id, grade),
                    FOREIGN KEY(algorithm_id) REFERENCES algorithm(algorithm_id),
                    FOREIGN KEY(platform_id) REFERENCES platform(platform_id)
                );
            """.trimIndent(),
            """
                CREATE TABLE IF NOT EXISTS solved_problem (
                    platform_id INTEGER,
                    name TEXT,
                    time LONG NOT NULL,
                    PRIMARY KEY (platform_id, name),
                    FOREIGN KEY(platform_id) REFERENCES platform(platform_id)
                );
            """.trimIndent(),
            """
                CREATE TABLE IF NOT EXISTS unsolved_problem (
                    platform_id INTEGER,
                    name TEXT,
                    PRIMARY KEY (platform_id, name),
                    FOREIGN KEY(platform_id) REFERENCES platform(platform_id)
                );
            """.trimIndent(),
            """
                CREATE TABLE IF NOT EXISTS configuration (
                    platform_id INTEGER NOT NULL,
                    algorithm_id INTEGER,
                    FOREIGN KEY(platform_id) REFERENCES platform(platform_id),
                    FOREIGN KEY(algorithm_id) REFERENCES platform(algorithm_id)
                );
            """.trimIndent()
        )
        sqlCreateStatements.forEach { sql ->
            conn.createStatement().use { statement ->
                statement.execute(sql)
            }
        }
    }

    fun listTables() {
        getConnection().use { conn ->
            val metaData = conn.metaData
            val tables = metaData.getTables(null, null, "%", arrayOf("TABLE"))
            while (tables.next()) {
                println(tables.getString("TABLE_NAME"))
            }
        }
    }
}