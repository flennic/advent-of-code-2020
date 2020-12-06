import java.io.File
import java.io.InputStream
import java.math.BigInteger

/**
 * Created by Maximilian Pfundstein on 03/12/2020.
 */


fun main() {
    val inputStream: InputStream = File("advent-of-code-day-3/src/input.txt").inputStream()
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach {
        if (it.isNotEmpty())
            lineList.add(it)
    }}

    val stepSizes = listOf(
            StepSize(1,1),
            StepSize(3,1),
            StepSize(5,1),
            StepSize(7,1),
            StepSize(1,2))

    val treeCounterResults = mutableListOf<BigInteger>()

    stepSizes.forEach {
        treeCounterResults.add(getTreeCounter(Position(0,0), it, lineList))
    }

    // Solution 1
    println(treeCounterResults[1])
    // Solution 2
    println(treeCounterResults)
    println(treeCounterResults.reduce{a, b -> a * b})
}

data class Position(
        var x: Int = 0,
        var y: Int = 0
)

data class StepSize(
        var x: Int  = 0,
        var y: Int  = 0
)

fun getTreeCounter(position: Position, stepSize: StepSize, grid: MutableList<String>) : BigInteger {

    var counter = 0.toBigInteger()

    while (position.y >= (-grid.size + 1)) {

        if (isTree(position.x, position.y, grid))
            counter++

        position.x += stepSize.x
        position.y -= stepSize.y
    }

    return counter
}

fun isTree(x: Int, y: Int, lineList: MutableList<String>): Boolean {

    if (y > 0 || y < -lineList.size + 1)
        throw IllegalArgumentException("y was ${y}, allowed range is 0 to ${-lineList.size+1}.")

    if (x < 0)
        throw java.lang.IllegalArgumentException("Lowest allowed value for x is 0.")

    val line = lineList[-y]
    val xSelector = x % lineList[0].length

    return line[xSelector] == '#'
}