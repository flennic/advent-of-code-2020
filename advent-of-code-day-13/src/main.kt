import java.io.File
import java.io.InputStream
import java.math.BigInteger

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-13/src/input.txt").inputStream()
    val content = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            content.add(line)
        }
    }

    val departureService = DepartureService(content)
    println(departureService.getResult())

    val goldCoinService = GoldCoinService(content)
    goldCoinService.getTimestamp()
    println(goldCoinService)
}

class DepartureService() {
    private var earliestDeparture = 0
    private var departures: List<Int> = mutableListOf()
    private var earliestDepartureResult = DepartureLookup(0,0,0)

    constructor(content: List<String>) : this() {
        this.earliestDeparture = content[0].toInt()
        departures = content[1]
                .split(",")
                .filter { it != "x" }
                .map { it.toInt() }
    }

    private fun calculateEarliestDeparture() {

        var i = 0

        while (true) {

            val lookupDeparture = this.earliestDeparture + i

            val res = this.departures
                    .map { DepartureLookup(lookupDeparture % it, it, lookupDeparture) }
                    .firstOrNull { it.Modulo == 0 }

            if (res != null) {
                this.earliestDepartureResult = res
                return
            }

            i++
        }
    }

    fun getResult(): Int {
        this.calculateEarliestDeparture()
        return (this.earliestDepartureResult.Time - this.earliestDeparture) * this.earliestDepartureResult.BusId
    }
}

data class DepartureLookup (
        var Modulo: Int,
        var BusId: Int,
        var Time: Int
)

class GoldCoinService() {

    private val reqList = mutableListOf<BusReq>()

    constructor(content: List<String>): this() {
        content[1].split(",").forEachIndexed { index, s ->
            if (s != "x")
                reqList.add(BusReq(index, s.toInt()))
        }

        reqList.sortByDescending { it.BusId }
    }

    fun getTimestamp() {
        var currentT = BigInteger.valueOf(reqList[0].BusId.toLong())
        val stepSize = reqList[0].BusId

        println(stepSize)

        goto@ while (true) {
            println(currentT)
            for (req in reqList) {
                if ((currentT + BigInteger.valueOf(req.Offset.toLong())).mod(BigInteger.valueOf(req.BusId.toLong())) != BigInteger.ZERO) {
                    currentT += BigInteger.valueOf(stepSize.toLong())
                    continue@goto
                }
            }

            println(currentT)
            return

            /*
            val check = reqList.all { req ->
                (currentT + BigInteger.valueOf(req.Offset.toLong())).mod(BigInteger.valueOf(req.BusId.toLong())) == BigInteger.ZERO
            }

            if (check) {
                println(currentT)
                return
            }
            */

        }
    }

    override fun toString(): String {
        val representation = StringBuilder()

        reqList.forEach { representation.append("${it}\n") }

        return representation.toString()
    }


}

data class BusReq(
        var Offset: Int,
        var BusId: Int
)