import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private const val ALGORITHMS_FILE_PATH = "src/main/kotlin/data/algorithms.json"

@Serializable
data class Algorithm(
    val krName: String,
    val enName: String,
    val url: String
)

class AlgorithmManager {

    private val algorithms = mutableListOf<Algorithm>()

    private val json = Json { prettyPrint = true }

    init {
        loadAlgorithms()
    }

    private fun loadAlgorithms() {
        val file = File(ALGORITHMS_FILE_PATH)
        if (file.exists()) {
            val text = file.readText()
            if (text.isNotEmpty()) {
                val data = json.decodeFromString<List<Algorithm>>(text)
                algorithms.addAll(data)
            }
        }
    }

    private fun saveAlgorithm() {
        val jsonText = json.encodeToString(algorithms)
        File(ALGORITHMS_FILE_PATH).writeText(jsonText)
    }

    fun addAlgorithm(algorithm: Algorithm): String {
        algorithms.forEach { (krName, enName, url) ->
            if (algorithm.krName == krName) {
                return "동일한 \"한글명\"의 알고리즘이 존재합니다."
            } else if (algorithm.enName == enName) {
                return "동일한 \"영문명\"의 알고리즘이 존재합니다."
            } else if (algorithm.url == url) {
                return "동일한 \"주소\"의 알고리즘이 존재합니다."
            }
        }

        algorithms.add(algorithm)
        saveAlgorithm()
        return "[${algorithm.krName}] 알고리즘을 추가하였습니다."
    }

    fun removeAlgorithm(algorithm: Algorithm): String {
        if (algorithms.contains(algorithm)) {
            algorithms.remove(algorithm)
            saveAlgorithm()
            return "[${algorithm.krName}] 알고리즘을 삭제하였습니다."
        }

        return "[${algorithm.krName}] 알고리즘이 존재하지 않습니다."
    }
}