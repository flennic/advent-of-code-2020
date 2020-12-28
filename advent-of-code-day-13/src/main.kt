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
    println(goldCoinService.getTimestamp())
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

class GoldCoinService {

    private val reqList = mutableListOf<BusReqBigInt>()

    constructor(content: List<String>): this() {
        reqList.clear()
        content[1].split(",").forEachIndexed { index, s ->
            if (s != "x")
                reqList.add(BusReqBigInt(BigInteger.valueOf(index.toLong()), BigInteger.valueOf(s.toLong())))
        }

        // TODO: Sorting the input results in a lot of very weird behaviour...
        //reqList.sortByDescending { it.bus }
    }

    constructor() {

        // Test cases that can be used if no input is given

        // RES = 26
        /*
        reqList.add(BusReqBigInt(BigInteger("2"), BigInteger("3")))
        reqList.add(BusReqBigInt(BigInteger("2"), BigInteger("4")))
        reqList.add(BusReqBigInt(BigInteger("1"), BigInteger("5")))
        */

        // RES = 39
        /*
        reqList.add(BusReqBigInt(BigInteger("0"), BigInteger("3")))
        reqList.add(BusReqBigInt(BigInteger("3"), BigInteger("4")))
        reqList.add(BusReqBigInt(BigInteger("4"), BigInteger("5")))
         */

        reqList.sortByDescending { it.bus }
    }

    fun getTimestamp(): BigInteger {

        val offset = this.reqList.map { it.offset }
        val bus = this.reqList.map { it.bus }

        var t = bus[0]

        for (i in 0 until this.reqList.size) {
            while ((t + offset[i]).mod(bus[i]) != BigInteger.ZERO) {
                val collection = bus.take(i)
                if (collection.isNotEmpty())
                    t += collection.reduce { a, b ->  a*b}
            }
        }

        return t
    }

    fun getBasisSolutionBruteForce(pair: Pair<BigInteger, BusReqBigInt>): BigInteger {
        val myMod = pair.second.bus
        val myReminder = pair.second.offset

        val value = pair.first
        var mult = BigInteger.ONE

        // If too slow, use Extended Euclidean Algorithm
        while ((mult*value).mod(myMod) != myReminder)
            mult++

        return mult*value
    }

    fun getTimestamp2(): BigInteger {

        val basis = this.reqList.map { req ->
            reqList.filter { it.bus != req.bus }.map { it.bus }.reduce { a, b -> a * b}
        }

        val res = basis.zip(this.reqList).map { pair -> getBasisSolutionBruteForce(pair)

            val myMod = pair.second.bus
            val myReminder = pair.second.offset

            val value = pair.first
            var mult = BigInteger.ONE

            // If too slow, use Extended Euclidean Algorithm
            while ((mult*value).mod(myMod) != myReminder)
                mult++

            mult*value
        }

        val mySum = res.reduce { a, b ->  a + b}
        val myProd = BigInteger.valueOf(this.reqList.map { it.bus }.reduce { a, b -> a*b }.toLong())
        return myProd - mySum.mod(myProd)
    }

    private fun sequenceArithmeticProgression(baseValue: BigInteger, step: BigInteger) = sequence {
        var value = baseValue
        yield(value)
        while (true) {
            value += step
            yield(value)
        }
    }

    private fun isCongruentTo(value: BigInteger, baseValue: BigInteger, modulo:BigInteger): Boolean {
        return value.mod(modulo) == baseValue
    }

    fun getTimestamp3(): BigInteger {

        var stepSize = BigInteger.ONE
        var x = this.reqList[0].offset

        for (i in 0 until (this.reqList.size - 1)) {
            println("Iteration $i")
            stepSize *= this.reqList[i].bus
            val seqGen = sequenceArithmeticProgression(x, stepSize)//.iterator()

            for (congruent in seqGen) {
                if (isCongruentTo(congruent, this.reqList[i + 1].offset, this.reqList[i + 1].bus)) {
                    x = congruent
                    break
                }
            }
        }

        return reqList.map { it.bus }.reduce { a, b -> a * b } - x
    }

    override fun toString(): String {
        val representation = StringBuilder()

        reqList.forEach { representation.append("${it}\n") }

        return representation.toString()
    }
}

data class BusReqBigInt(
        var offset: BigInteger,
        var bus: BigInteger
)