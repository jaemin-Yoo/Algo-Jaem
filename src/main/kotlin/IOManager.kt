import java.io.File

private const val INPUT_ERROR_MSG = "\n❗입력 형식이 맞지 않습니다.❗\n"
private const val NOT_EXISTS_NUMBER_ERROR_MSG = "\n❗존재하지 않는 번호입니다.❗\n"
private const val REMOVE_ALGORITHM_ERROR_MSG = "\n❗현재 선택된 알고리즘이 삭제되었기 때문에 [랜덤] 알고리즘으로 변경되었습니다.❗\n"
private const val NOTE_FILE_PATH = "src/main/kotlin/note.txt"

class IOManager {

    private val br = System.`in`.bufferedReader()

    private val algorithmManager = AlgorithmManager()
    private val algorithms
        get() = algorithmManager.getAlgorithms()

    private val configurationManager = ConfigurationManager()
    private val configuration
        get() = configurationManager.getConfiguration()

    private val numberRegex = Regex("\\d+")
    private val addAlgorithmRegex = Regex("add [가-힣]+(-[가-힣]+)* [a-zA-Z]+(-[a-zA-Z]+)* https?://.*")
    private val removeAlgorithmRegex = Regex("remove \\d+")
    private val updateAlgorithmRegex = Regex("update \\d+")

    fun start() {
        val configuration = configurationManager.getConfiguration()
        val platformName = configuration.platform.krName
        val algorithmName = configuration.algorithm?.krName ?: "랜덤"
        print(
            """
                
                ************************************
                🔓 [알고잼] - 알고리즘 문제 풀이 프로그램
                ************************************
                
                  1. 문제 풀기
                  2. 플랫폼: $platformName
                  3. 알고리즘: $algorithmName
                  
                  0. 종료
                
                수행할 작업의 번호를 입력하세요.
                input> 
            """.trimIndent()
        )

        val input = getValidInput(numberRegex) ?: return
        when (input.toInt()) {
            1 -> solveProblem()
            2 -> updatePlatform()
            3 -> showAlgorithms()
            else -> print(NOT_EXISTS_NUMBER_ERROR_MSG)
        }
        start()
    }

    private fun solveProblem() {
        val algorithm = if (configuration.algorithm == null) {
            algorithmManager.randomAlgorithm()
        } else {
            configuration.algorithm
        }

        if (algorithm == null) {
            print("\n❗추가된 알고리즘이 존재하지 않습니다. 알고리즘을 추가해주세요.❗\n")
            showAlgorithms()
        } else {
            println("${algorithm.krName} ${algorithm.url}")

            val file = File(NOTE_FILE_PATH)
            file.createNewFile()
        }
    }

    private fun loadConfiguration() {
        val configuration = configurationManager.getConfiguration()
        val platformName = configuration.platform.krName
        val algorithmName = configuration.algorithm?.krName ?: "랜덤"

        print(
            """
                
                ************
                🔧 환경 설정
                ************
                
                  1. 플랫폼: $platformName
                  2. 알고리즘: $algorithmName
                  
                  0. 뒤로 가기
                
                관리할 설정의 번호를 입력하세요.
                input> 
            """.trimIndent()
        )

        val input = getValidInput(numberRegex) ?: return
        when (input.toInt()) {
            1 -> updatePlatform()
            2 -> showAlgorithms()
        }
        loadConfiguration()
    }

    private fun updatePlatform() {
        println(
            """
                
                **********
                🏝️ 플랫폼
                **********
                
            """.trimIndent()
        )

        val platforms = Platform.entries
        platforms.forEachIndexed { idx, platform ->
            println("  ${idx + 1}. ${platform.krName}")
        }
        println("\n  0. 뒤로 가기")
        print("\n변경할 플랫폼 번호를 입력하세요.")
        print("\ninput> ")

        val input = getValidInput(numberRegex) ?: return
        val num = input.toInt()
        if (num < platforms.size + 1) {
            val selectedPlatform = platforms[num - 1]
            val msg = configurationManager.setConfiguration(
                platform = selectedPlatform,
                algorithm = configuration.algorithm
            )
            print(msg)
        } else {
            print(NOT_EXISTS_NUMBER_ERROR_MSG)
            updatePlatform()
        }
    }

    private fun showAlgorithms() {
        println(
            """
                
                ***********
                🎮 알고리즘
                ***********
                
                <명령어>
                * 띄어쓰기는 '-'로 대체 *
                - 알고리즘 추가: input> add [한글명] [영문명] [주소]
                - 알고리즘 삭제: input> remove [번호]
                - 알고리즘 변경: input> update [번호]
                ex) add 다이나믹-프로그래밍 DYNAMIC-PROGRAMMING https://www.example.com
                
            """.trimIndent()
        )

        println("  1. 랜덤")
        algorithms.forEachIndexed { idx, algorithm ->
            println("  ${idx + 2}. ${algorithm.krName}")
        }
        println("\n  0. 뒤로 가기")
        print("\n명령어를 입력하세요.")

        while (true) {
            print("\ninput> ")
            val input = br.readLine()
            if (input == "0") {
                return
            }

            if (addAlgorithmRegex.matches(input)) {
                confirm("추가") {
                    val (_, krName, enName, url) = input.split(' ')
                    val msg = algorithmManager.addAlgorithm(Algorithm(krName, enName.uppercase(), url).replaceDashToSpace())
                    print(msg)
                }
            } else if (removeAlgorithmRegex.matches(input)) {
                val (_, numStr) = input.split(' ')
                val num = numStr.toInt()
                if (num == 1) {
                    print("\n❗[랜덤]은 삭제할 수 없습니다.❗\n")
                    continue
                } else if (num < algorithms.size + 2) {
                    confirm("삭제") {
                        val configuration = configurationManager.getConfiguration()
                        val selectedAlgorithm = algorithms[num - 2]
                        val msg = algorithmManager.removeAlgorithm(selectedAlgorithm)
                        print(msg)
                        if (selectedAlgorithm == configuration.algorithm) {
                            configurationManager.setConfiguration(platform = configuration.platform)
                            print(REMOVE_ALGORITHM_ERROR_MSG)
                        }
                    }
                } else {
                    print(NOT_EXISTS_NUMBER_ERROR_MSG)
                    continue
                }
            } else if (updateAlgorithmRegex.matches(input)) {
                val (_, numStr) = input.split(' ')
                val num = numStr.toInt()
                if (num == 1) {
                    confirm("변경") {
                        val msg = configurationManager.setConfiguration(
                            platform = configuration.platform,
                            algorithm = null
                        )
                        print(msg)
                    }
                    return
                } else if (num < algorithms.size + 2) {
                    confirm("변경") {
                        val selectedAlgorithm = algorithms[num - 2]
                        val msg = configurationManager.setConfiguration(
                            platform = configuration.platform,
                            algorithm = selectedAlgorithm
                        )
                        print(msg)
                    }
                    return
                } else {
                    print(NOT_EXISTS_NUMBER_ERROR_MSG)
                    continue
                }
            } else {
                print(INPUT_ERROR_MSG)
                continue
            }
            break
        }
        showAlgorithms()
    }

    private fun isValidInput(input: String, regex: Regex) = regex.matches(input)

    private fun getValidInput(regex: Regex): String? {
        var input = br.readLine()
        if (input == "0") {
            return null
        }

        while (!isValidInput(input, regex)) {
            print(INPUT_ERROR_MSG)
            print("input> ")
            input = br.readLine()
        }
        return input
    }

    private fun confirm(word: String, execute: () -> Unit) {
        print(
            """
                *******************************
                ❕정말로 $word 하시겠습니까? (y/n)
                *******************************
                
                input> 
            """.trimIndent()
        )
        val input = br.readLine().uppercase()
        when (input) {
            "Y" -> {
                execute()
                return
            }
            "N" -> return
            else -> print(INPUT_ERROR_MSG)
        }
        confirm(word, execute)
    }
}