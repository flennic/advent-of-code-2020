import java.io.File
import java.io.InputStream

/**
 * Created by Maximilian Pfundstein on 05/12/2020.
 */


fun main() {
    val inputStream: InputStream = File("advent-of-code-day-4/src/input.txt").inputStream()
    val passports = mutableListOf<Map<String, String>>();

    inputStream.bufferedReader().useLines { lines -> lines.forEach {

        val passportAsString = mutableListOf<String>()

        if (it.isNotEmpty()) {
            println("Adding $it")
            passportAsString.add(it)
        }
        else {
            println(passportAsString)
            passports.add(linesToPassport(passportAsString))
        }
    }}

    //println(passports)
}

fun linesToPassport(lines: MutableList<String>): Map<String, String> {

    //println("Input is $lines")

    val passport = mutableMapOf<String, String>()

    //iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
    //hcl:#cfa07d byr:1929

    lines.forEach { line ->
        line.split(' ').forEach{ entry ->
            val keyVal = entry.split(":")
            passport[keyVal[0]] = keyVal[1]
        }
    }

    return passport
}