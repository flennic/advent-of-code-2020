import java.io.File
import java.io.InputStream
import java.math.BigInteger

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-10/src/input.txt").inputStream()
    val jolts = mutableListOf<Int>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            jolts.add(line.toInt())
        }
    }

    val joltSequence = getJoltSequence(jolts)
    val outerCache = mutableMapOf<Int, BigInteger>()
    val res1 = getProdOfOneAndThreeDist(joltSequence)
    val res2 = getCombinations(0, joltSequence, cache = outerCache)

    print("Question 1: ")
    println(res1)

    print("Question 2: ")
    println(res2)
}


fun getCombinations(joltLevel: Int, joltSequence: MutableList<Jolt>, capacity: Int = 3, cache: MutableMap<Int, BigInteger>): BigInteger {

    val consideredJolt = joltSequence.filter { it.joltLevel - joltLevel in 1..capacity }

    if (consideredJolt.isEmpty())
        return BigInteger.ONE

    return consideredJolt.map { jolt ->
        val cacheRes = cache[jolt.joltLevel]
        if (cacheRes == null) {
            val computeResult = getCombinations(jolt.joltLevel, joltSequence, cache = cache)
            cache[jolt.joltLevel] = computeResult
            computeResult
        }
        else {
            cacheRes
        }

    }.reduce{a, b -> a + b}
}

fun getProdOfOneAndThreeDist(joltSequence: MutableList<Jolt>): Int {
    val ones = joltSequence.filter { it.previousDiff == 1 }.sumBy { 1 }
    val threes = joltSequence.filter { it.previousDiff == 3 }.sumBy { 1 }
    return ones * threes
}

fun addLaptop(joltSequence: MutableList<Jolt>, capacity: Int = 3): MutableList<Jolt> {
    val lastJolt = joltSequence.last()
    val laptop = Jolt(lastJolt.joltLevel+capacity, capacity)
    joltSequence.add(laptop)
    return joltSequence
}

fun getJoltSequence(jolts: MutableList<Int>): MutableList<Jolt> {

    var joltLevel = 0
    val joltSequence = mutableListOf<Jolt>()

    while (jolts.size > 0) {
        val possibleNextJolt: Int = jolts.minOrNull() ?: return addLaptop(joltSequence)
        val diff = possibleNextJolt - joltLevel

        if (diff <= 3) {
            joltLevel += diff
            joltSequence.add(Jolt(joltLevel, diff))
            jolts.remove(possibleNextJolt)
        }
        else {
            return addLaptop(joltSequence)
        }
    }

    return addLaptop(joltSequence)
}

data class Jolt (
    var joltLevel: Int,
    var previousDiff: Int,
)

