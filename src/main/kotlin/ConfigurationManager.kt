import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private const val CONFIG_FILE_PATH = "src/main/kotlin/data/configuration.json"

enum class PlatformType(val krName: String) {
    BAEKJOON("백준")
}

@Serializable
data class Configuration(
    val platformType: PlatformType = PlatformType.BAEKJOON,
    val algorithm: Algorithm? = null,
)

class ConfigurationManager {
    private var configuration = Configuration()

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        val file = File(CONFIG_FILE_PATH)
        if (file.exists()) {
            val text = file.readText()
            if (text.isNotEmpty()) {
                val data = json.decodeFromString<Configuration>(text)
                configuration = data
            }
        }
    }

    private fun saveConfiguration() {
        val jsonText = json.encodeToString(configuration)
        File(CONFIG_FILE_PATH).writeText(jsonText)
    }

    fun setConfiguration(
        platformType: PlatformType = PlatformType.BAEKJOON,
        algorithm: Algorithm? = null
    ): String {
        configuration = Configuration(platformType, algorithm)
        saveConfiguration()
        return "\n✅ 설정이 변경되었습니다.\n"
    }

    fun getConfiguration() = configuration
}