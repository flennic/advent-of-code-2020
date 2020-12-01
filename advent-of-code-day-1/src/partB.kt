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

    lineList.forEach { outer ->
        lineList.forEach { inter ->
            lineList.forEach { inner ->
                if (outer + inter + inner == 2020) {
                    println("The sum of $outer, $inter and $inner is 2020 and their product is ${outer * inter * inner}.")
                    return
                }
            }
        }
    }
}