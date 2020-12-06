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

    outerloop@ for (pinned in lineList)
        for (inner in lineList)
            if (pinned + inner == 2020) {
                println("The sum of $pinned and $inner is 2020 and their product is ${pinned * inner}.")
                break@outerloop
            }

    outerloop2@ for (outer in lineList)
        for (inter in lineList)
            for (inner in lineList)
                if (outer + inter + inner == 2020) {
                    println("The sum of $outer, $inter and $inner is 2020 and their product is ${outer * inter * inner}.")
                    break@outerloop2
                }
}
