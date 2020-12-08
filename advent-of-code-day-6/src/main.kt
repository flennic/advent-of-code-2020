import java.io.File
import java.io.InputStream

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-6/src/input.txt").inputStream()
    var sum = 0
    var sumAllYes = 0

    inputStream.bufferedReader().useLines { lines ->

        val questions = mutableSetOf<Char>()
        val questionsCounter = mutableMapOf<Char, Int>().withDefault { 0 }
        var individuals = 0

        lines.forEach { line ->
            if (line.isNotEmpty()) {
                line.forEach { char ->
                    questions.add(char)
                    questionsCounter[char] = questionsCounter.getValue(char) + 1
                }
                individuals++
            }
            else {
                sum += questions.size
                questions.clear()

                questionsCounter.forEach{ (key, value) ->
                    if (value == individuals)
                        sumAllYes++
                }

                questionsCounter.clear()
                individuals = 0
            }
        }
        sum += questions.size

        questionsCounter.forEach{ (key, value) ->
            if (value == individuals)
                sumAllYes++
        }

    }
    println(sum)
    println(sumAllYes)
}