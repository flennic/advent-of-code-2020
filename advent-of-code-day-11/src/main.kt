import java.io.File
import java.io.InputStream
import java.lang.IndexOutOfBoundsException

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-11/src/input.txt").inputStream()
    val content = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            content.add(line)
        }
    }

    val seatingSystem = SeatingSystem(content)
    do {
        seatingSystem.nextState()
    } while (seatingSystem.hasChanged)
    println(seatingSystem.getOccupiedSeats())

    val seatingSystem2 = SeatingSystem(content)
    do {
        seatingSystem2.nextState(lineOfSight = true)
    } while (seatingSystem2.hasChanged)
    println(seatingSystem2.getOccupiedSeats())
}

fun signToSeat(char: Char, x: Int, y: Int): Seat {
    if (char == '#')
        return Seat(Status.OCCUPIED, x, y)

    if (char == 'L')
        return Seat(Status.EMPTY, x, y)

    if (char == '.')
        return Seat(Status.FLOOR, x, y)

    throw IllegalArgumentException()
}

fun seatToSign(seat: Seat): Char {
    if (seat.Status == Status.OCCUPIED)
        return '#'

    if (seat.Status == Status.EMPTY)
        return 'L'

    if (seat.Status == Status.FLOOR)
        return '.'

    throw IllegalArgumentException()
}

class SeatingSystem() {

    private var round: Int = 0
    var hasChanged = false
    private var grid: MutableList<MutableList<Seat>> = mutableListOf()

    constructor(input: List<String>) : this() {
        for ((yIndex, row) in input.withIndex()) {
            this.grid.add(mutableListOf())
            for ((xIndex, rowSeat) in row.withIndex()) {
                this.grid[yIndex].add(signToSeat(rowSeat, xIndex, yIndex))
            }
        }
    }

    fun nextState(lineOfSight: Boolean = false) {
        hasChanged = false

        val newGrid: MutableList<MutableList<Seat>> = mutableListOf()
        for ((yIndex, row) in grid.withIndex()) {
            newGrid.add(mutableListOf())
            for (rowSeat in row) {
                val newSeat = mutate(rowSeat, lineOfSight)

                if (newSeat.Status != rowSeat.Status)
                    hasChanged = true

                newGrid[yIndex].add(newSeat)
            }
        }

        this.grid = newGrid
        round++
    }

    fun getOccupiedSeats(): Int {
        return grid.flatten().filter { it.Status == Status.OCCUPIED }.sumBy { 1 }
    }

    private fun mutate(seat: Seat, lineOfSight: Boolean = false): Seat {

        val threshold = if (lineOfSight) 5 else 4

        val neighbors = if (lineOfSight) getLineOfSightSeats(seat) else getAdjacentSeats(seat)

        if (seat.Status == Status.EMPTY && neighbors.none{ it.Status == Status.OCCUPIED }) {
            return Seat(Status.OCCUPIED, seat.X, seat.Y)
        }

        if (seat.Status == Status.OCCUPIED && neighbors.filter { it.Status == Status.OCCUPIED }.sumBy { 1 } >= threshold) {
            return Seat(Status.EMPTY, seat.X, seat.Y)
        }

        return Seat(seat.Status, seat.X, seat.Y)
    }

    private fun getLineOfSightSeats(seat: Seat): MutableList<Seat> {

        val lineOfSightSeats = mutableListOf<Seat>()

        val walks = listOf(
                Pair(-1, 0),    // N
                Pair(-1, 1),    // NE
                Pair(0, 1),     // E
                Pair(1, 1),     // SE
                Pair(1, 0),     // S
                Pair(1, -1),    // SW
                Pair(0, -1),    // W
                Pair(-1, -1),   // NW
        )

        walks.forEach { walk ->
            val startPosition = intArrayOf(seat.Y, seat.X)
            do {
                startPosition[0] += walk.first
                startPosition[1] += walk.second

                try {
                    val crossSeat = this.grid[startPosition[0]][startPosition[1]]
                    if (crossSeat.Status != Status.FLOOR) {
                        lineOfSightSeats.add(crossSeat)
                        break
                    }
                }
                catch (e: IndexOutOfBoundsException) {
                    break
                }
            } while (true)

        }

        return lineOfSightSeats
    }

    private fun getAdjacentSeats(seat: Seat, radius: Int = 1): MutableList<Seat> {
       val adjacentSeats = mutableListOf<Seat>()

       for (xRange in -(radius)..radius) {
           for (yRange in -(radius)..radius) {
               if (xRange != 0 || yRange != 0) {
                   try {
                       val currentSeat = this.grid[seat.Y+yRange][seat.X+xRange]
                       adjacentSeats.add(currentSeat)
                   }
                   catch (e: IndexOutOfBoundsException) {

                   }
               }
           }
       }

       return adjacentSeats
    }

    override fun toString(): String {
        val representation = StringBuilder()

        for (row in grid) {
            for (seat in row) {
                representation.append(seatToSign(seat))
            }
            representation.append("\n")
        }

        return representation.toString()
    }
}

data class Seat (
        var Status: Status,
        var X: Int,
        var Y: Int
)

enum class Status {
    EMPTY, OCCUPIED, FLOOR
}