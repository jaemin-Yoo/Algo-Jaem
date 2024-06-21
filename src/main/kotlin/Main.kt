import data.database.SQLiteManager
import data.database.dao.PlatformDao
import java.sql.DriverManager

fun main() {
    val connection = DriverManager.getConnection("jdbc:sqlite:algo_jaem.db")
    SQLiteManager.initializeDatabase(connection)

    val platformDao = PlatformDao(connection)
    platformDao.insertPlatform("백준", true)
    platformDao.insertPlatform("프로그래머스", false)
}