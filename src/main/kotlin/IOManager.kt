import java.io.File

private const val INPUT_ERROR_MSG = "\n❗입력 형식이 맞지 않습니다.❗\n"
private const val NOT_EXISTS_NUMBER_ERROR_MSG = "\n❗존재하지 않는 번호입니다.❗\n"
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
    private val addAlgorithmRegex = Regex("[가-힣]+ [A-Z]+(-[A-Z]+)* https?://.*")

    fun start() {
        print(
            """
                
                ************************************
                🔓 [알고잼] - 알고리즘 문제 풀이 프로그램
                ************************************
                
                  1. 문제 풀기
                  2. 추가된 알고리즘 보기
                  3. 환경 설정
                  0. 종료
                
                수행할 작업의 번호를 입력하세요.
                input: 
            """.trimIndent()
        )

        val input = getValidInput(numberRegex) ?: return
        when (input.toInt()) {
            1 -> solveProblem()
            2 -> showAlgorithms()
            3 -> loadConfiguration()
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
            addAlgorithm()
        } else {
            println("${algorithm.krName} ${algorithm.url}")

            val file = File(NOTE_FILE_PATH)
            file.createNewFile()
        }
    }

    private fun showAlgorithms() {
        println(
            """
                
                ********************
                👁️ 추가된 알고리즘 보기
                ********************
                
            """.trimIndent()
        )
        algorithms.forEach { algorithm ->
            println("${algorithm.krName} - ${algorithm.enName}")
        }
        print(
            """
                
              1. 알고리즘 추가
              2. 알고리즘 삭제
              0. 뒤로 가기
            
            수행할 작업의 번호를 입력하세요.
            input: 
            """.trimIndent()
        )

        val input = getValidInput(numberRegex) ?: return
        when (input.toInt()) {
            1 -> addAlgorithm()
            2 -> removeAlgorithm()
            else -> print(NOT_EXISTS_NUMBER_ERROR_MSG)
        }
        showAlgorithms()
    }

    private fun addAlgorithm() {
        print(
            """
                
                **************************************************
                  추가할 알고리즘을 아래 양식대로 입력하세요.
                  양식: [한글명] [영문명-대문자] [주소]
                  ex) 구현 IMPLEMENTATION https://www.example.com
                **************************************************
                
                  0. 뒤로가기
                
                input: 
            """.trimIndent()
        )

        val input = getValidInput(addAlgorithmRegex) ?: return
        val (krName, enName, url) = input.split(' ')
        val msg = algorithmManager.addAlgorithm(Algorithm(krName, enName, url))
        print(msg)
    }

    private fun removeAlgorithm() {
        println(
            """
                
                *********************************
                  삭제할 알고리즘의 번호를 입력하세요.
                *********************************
                
            """.trimIndent()
        )

        val algorithms = algorithmManager.getAlgorithms()
        algorithms.forEachIndexed { idx, (krName, _, _) ->
            println("  ${idx + 1}. $krName")
        }
        println("  0. 뒤로 가기")
        print("\ninput: ")

        val input = getValidInput(numberRegex) ?: return
        val msg = algorithmManager.removeAlgorithm(algorithms[input.toInt() - 1])
        print(msg)
    }

    private fun loadConfiguration() {
        val configuration = configurationManager.getConfiguration()
        val platform = configuration.platform
        val algorithm = if (configuration.algorithm == null) {
            "랜덤"
        } else {
            configuration.algorithm.krName
        }

        print(
            """
                
                ************
                🔧 환경 설정
                ************
                
                  1. 플랫폼: ${platform.krName}
                  2. 알고리즘: $algorithm
                  0. 뒤로 가기
                
                변경할 설정의 번호를 입력하세요.
                input: 
            """.trimIndent()
        )

        val input = getValidInput(numberRegex) ?: return
        when (input.toInt()) {
            1 -> changePlatform()
            2 -> changeAlgorithm()
        }
        loadConfiguration()
    }

    private fun changePlatform() {
        print(
            """
                
                *************
                  플랫폼 선택
                *************
                
            """.trimIndent()
        )

        val platforms = Platform.entries
        platforms.forEachIndexed { idx, platform ->
            println("  ${idx + 1}. ${platform.krName}")
        }
        println("  0. 뒤로 가기")
        print("\n변경할 플랫폼 번호를 입력하세요.")
        print("\ninput: ")

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
            changePlatform()
        }
    }

    private fun changeAlgorithm() {
        print(
            """
                
                **************
                  알고리즘 선택
                **************
                
            """.trimIndent()
        )

        println("  1. 랜덤")
        algorithms.forEachIndexed { idx, algorithm ->
            println("  ${idx + 2}. ${algorithm.krName}")
        }
        println("  0. 뒤로 가기")
        print("\n변경할 알고리즘 번호를 입력하세요.")
        print("\ninput: ")

        val input = getValidInput(numberRegex) ?: return
        val num = input.toInt()
        if (num == 1) {
            val msg = configurationManager.setConfiguration(
                platform = configuration.platform,
                algorithm = null
            )
            print(msg)
        } else if (num < algorithms.size + 2) {
            val selectedAlgorithm = algorithms[num - 2]
            val msg = configurationManager.setConfiguration(
                platform = configuration.platform,
                algorithm = selectedAlgorithm
            )
            print(msg)
        } else {
            print(NOT_EXISTS_NUMBER_ERROR_MSG)
            changeAlgorithm()
        }
    }

    private fun isValidInput(input: String, regex: Regex) = regex.matches(input)

    private fun getValidInput(regex: Regex): String? {
        var input = br.readLine()
        if (input == "0") {
            return null
        }

        while (!isValidInput(input, regex)) {
            print(INPUT_ERROR_MSG)
            print("input: ")
            input = br.readLine()
        }
        return input
    }
}