import java.io.File
import java.io.InputStream

/**
 * Created by Maximilian Pfundstein on 01/12/2020.
 */


fun main() {

    val inputStream: InputStream = File("advent-of-code-day-1/src/input.txt").inputStream()
    val lineList = mutableListOf<Int>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach {
        val lineValue = it.toIntOrNull()
        if (lineValue != null)
            lineList.add(lineValue)
    }}

    lineList.forEach { pinned ->
        lineList.forEach {
            if (pinned + it == 2020) {
                println("The sum of $pinned and $it is 2020 and their product is ${pinned * it}.")
                return
            }
        }
    }
}
