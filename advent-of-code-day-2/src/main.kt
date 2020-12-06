import java.io.File
import java.io.InputStream

/**
 * Created by Maximilian Pfundstein on 02/12/2020.
 */


fun main() {

    val inputStream: InputStream = File("advent-of-code-day-2/src/input.txt").inputStream()
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach {
        if (it.isNotEmpty())
            lineList.add(it)
    }}

    val noCorrectPasswords = lineList.map { isPasswordValid(getMetrics(it)) }.toList().sumBy { if (it) 1 else 0 }
    val noCorrectPasswords2 = lineList.map { isPasswordValid2(getMetrics(it)) }.toList().sumBy { if (it) 1 else 0 }

    println(noCorrectPasswords)
    println(noCorrectPasswords2)
}

fun isPasswordValid2(dict: Map<String, String>): Boolean {

    var positionOne = dict["first"]?.toInt() ?: return false
    var positionTwo = dict["second"]?.toInt() ?: return false
    val password = dict["password"].toString()

    positionOne--
    positionTwo--

    val policyChar: Char = dict["character"]?.get(0) ?: return false

    val matchOne = password[positionOne] == policyChar
    val matchTwo = password[positionTwo] == policyChar

    return matchOne xor matchTwo
}

fun isPasswordValid(dict: Map<String, String>): Boolean {

    var occurrence = 0
    val lowerRange = dict["first"]?.toInt() ?: return false
    val upperRange = dict["second"]?.toInt() ?: return false

    val policyChar: Char = dict["character"]?.get(0) ?: return false

    for (char in dict["password"].orEmpty()) {
        if (char == policyChar)
            occurrence++
    }

    return occurrence in lowerRange..upperRange
}

fun getMetrics(line: String): Map<String, String> {
    val lineSplit = line.split(':')
    val policy = lineSplit[0]
    val password = lineSplit[1].split(' ')[1]

    val policySplit = policy.split(' ')
    val range = policySplit[0]
    val character = policySplit[1]

    val rangeSplit = range.split('-')
    val rangeLower = rangeSplit[0]
    val rangeUpper = rangeSplit[1]

    return mapOf("first" to rangeLower, "second" to rangeUpper, "character" to character, "password" to password)
}
