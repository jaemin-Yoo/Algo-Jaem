import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private const val CONFIG_FILE_PATH = "src/main/kotlin/data/configuration.json"

@Serializable
data class Configuration(
    val algorithm: Algorithm? = null,
)

class ConfigurationManager {
    private var configuration = Configuration()

    private val json = Json { prettyPrint = true }

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        val file = File(CONFIG_FILE_PATH)
        if (file.exists()) {
            val text = file.readText()
            val data = json.decodeFromString<Configuration>(text)
            configuration = data
        }
    }

    private fun saveConfiguration() {
        val jsonText = json.encodeToString(configuration)
        File(CONFIG_FILE_PATH).writeText(jsonText)
    }

    fun setConfiguration(algorithm: Algorithm? = null) {
        configuration = Configuration(algorithm)
        saveConfiguration()
    }
}