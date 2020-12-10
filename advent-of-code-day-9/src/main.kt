import java.io.File
import java.io.InputStream
import java.math.BigInteger

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-9/src/input.txt").inputStream()
    val inputNumbers = mutableListOf<BigInteger>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            inputNumbers.add(line.toBigInteger())
        }
    }

    val invalidNumber = findInvalidNumber(inputNumbers)
    val contiguousSet = findContiguousSet(invalidNumber, inputNumbers)
    val res = getMinMaxSum(contiguousSet)
    println(res)
}

fun getMinMaxSum(list: MutableList<BigInteger>): BigInteger {

    val min = list.minOrNull()
    val max = list.maxOrNull()

    if (min == null || max == null)
        return BigInteger.ZERO

    return min + max
}

fun findContiguousSet(invalidNumber: NumberResult, list: MutableList<BigInteger>): MutableList<BigInteger> {

    val contiguousSet = mutableListOf<BigInteger>()

    for (i in list.indices) {

        if (i == invalidNumber.index) {
            //println("Did not found any set where the sum resembles teh given number ${invalidNumber.number}")
            return mutableListOf()
        }

        var contiguousCounter = 0
        while (contiguousSet.isEmpty() || contiguousSet.reduce{a, b -> a + b} < invalidNumber.number) {
            contiguousSet.add(list[i + contiguousCounter])
            contiguousCounter++
        }
        if (contiguousSet.reduce{a, b -> a + b} == invalidNumber.number && contiguousSet.size > 1) {
            //println("Found contiguous set $contiguousSet")
            return contiguousSet
        }
        else {
            contiguousSet.clear()
        }
    }
    return mutableListOf()
}

fun findInvalidNumber(inputNumbers: MutableList<BigInteger>, preambleLength: Int = 25) : NumberResult {
    val preambleBuffer = mutableListOf<BigInteger>()

    for ((index, number) in inputNumbers.withIndex()) {

        if (preambleBuffer.size == preambleLength && !number.isSumOf(preambleBuffer)) {
            println("Number $number cannot be obtained by combining any two values of the buffer.")
            return NumberResult(index, number)
        }
        else {

            if (preambleBuffer.size >= preambleLength)
                preambleBuffer.removeAt(0)

            if (preambleBuffer.size < preambleLength)
                preambleBuffer.add(number)
        }
    }

    return NumberResult(-1, BigInteger.ZERO)
}

fun BigInteger.isSumOf(list: MutableList<BigInteger>) : Boolean {

    list.forEach{ number1 ->
        list.forEach { number2 ->
            if (number1 + number2 == this)
                return true
        }
    }

    return false
}

data class NumberResult(
        var index: Int,
        var number: BigInteger
)