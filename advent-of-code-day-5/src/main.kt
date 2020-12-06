import java.io.File
import java.io.InputStream

/**
 * Created by Maximilian Pfundstein on 05/12/2020.
 */


fun main() {
    val inputStream: InputStream = File("advent-of-code-day-5/src/input.txt").inputStream()
    val bsps = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach {
            if (it.isNotEmpty())
                bsps.add(it)
        }
    }

    val bspsProcessed = bsps.map { bsp ->
        BinarySeatPartition(bsp)
    }

    val maxId = bspsProcessed.map { bsp ->
        bsp.seatId
    }.max()

    var mySeatId = 0

    for (bsp in bspsProcessed.sortedBy { it.seatId }) {
        if (bsp.seatId == mySeatId + 2) {
            mySeatId = bsp.seatId - 1
            break
        }
        mySeatId = bsp.seatId
    }

    println(bspsProcessed.sortedBy { it.seatId }.map { it.seatId })
    println(maxId)
    println(mySeatId)
}

class BinarySeatPartition(seatEncoding: String) {
    var row: Int = 0
    var column: Int = 0
    var seatId: Int = 0

    init {
        val rowString = seatEncoding.take(7)
        val seatString = seatEncoding.takeLast(3)
        val binaryRowString = rowString.map { char ->
            if (char == 'F')  "0" else  "1"
        }.joinToString("")
        this.row = Integer.parseInt(binaryRowString, 2)
        val binarySeatString = seatString.map { char ->
            if (char == 'L')  "0" else  "1"
        }.joinToString("")
        this.column = Integer.parseInt(binarySeatString, 2)
        this.seatId = this.row * 8 + this.column
    }

    override fun toString() = "BinarySeatPartition {row: $row, column: $column, seatId: $seatId}"

}
