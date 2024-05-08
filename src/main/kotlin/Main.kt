fun main() {
    val manager = AlgorithmManager()
    // val msg = manager.addAlgorithm(Algorithm("구현", "IMPLEMENTATION", "www.example3.com"))
    val msg = manager.removeAlgorithm(Algorithm("구현", "IMPLEMENTATION", "www.example3.com"))
    println(msg)
}