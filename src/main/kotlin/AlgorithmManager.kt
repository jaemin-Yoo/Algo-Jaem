import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private const val ALGORITHMS_FILE_PATH = "src/main/kotlin/data/algorithms.json"
private const val ALGORITHM_STATUS_FILE_PATH = "src/main/kotlin/data/algorithm_status.json"

@Serializable
data class Algorithm(
    val krName: String,
    val enName: String,
    val url: String
)

@Serializable
data class AlgorithmStatus(
    val algorithm: Algorithm,
    val solved: Boolean
)

class AlgorithmManager {

    private val algorithms = mutableListOf<Algorithm>()
    private val algorithmStatuses = mutableListOf<AlgorithmStatus>()

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

    private fun saveAlgorithmStatus() {
        val jsonText = json.encodeToString(algorithmStatuses)
        File(ALGORITHM_STATUS_FILE_PATH).writeText(jsonText)
    }

    fun addAlgorithm(algorithm: Algorithm): String {
        algorithms.forEach { (krName, enName, url) ->
            if (algorithm.krName == krName) {
                return "\n❗동일한 \"한글명\"의 알고리즘이 존재합니다.❗\n"
            } else if (algorithm.enName == enName) {
                return "\n❗동일한 \"영문명\"의 알고리즘이 존재합니다.❗\n"
            } else if (algorithm.url == url) {
                return "\n❗동일한 \"주소\"의 알고리즘이 존재합니다.❗\n"
            }
        }

        algorithms.add(algorithm)
        algorithmStatuses.add(AlgorithmStatus(algorithm, false))
        saveAlgorithm()
        saveAlgorithmStatus()
        return "\n✅ [${algorithm.krName}] 알고리즘을 추가하였습니다.\n"
    }

    fun removeAlgorithm(algorithm: Algorithm): String {
        if (algorithms.contains(algorithm)) {
            algorithms.remove(algorithm)
            saveAlgorithm()
            return "\n❌ [${algorithm.krName}] 알고리즘을 삭제하였습니다.\n"
        }

        return "\n❗[${algorithm.krName}] 알고리즘이 존재하지 않습니다.❗\n"
    }

    fun randomAlgorithm(): Algorithm? {
        val file = File(ALGORITHM_STATUS_FILE_PATH)
        return if (file.exists()) {
            val text = file.readText()
            if (text.isNotEmpty()) {
                val data = json.decodeFromString<List<AlgorithmStatus>>(text)
                data.filterNot { it.solved }.map { it.algorithm }.random()
            } else {
                null
            }
        } else {
            null
        }
    }

    fun getAlgorithms() = algorithms.toList()
}