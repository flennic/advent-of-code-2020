import java.io.File
import java.io.InputStream

/**
 * Created by Maximilian Pfundstein on 05/12/2020.
 */


fun main() {

    val inputStream: InputStream = File("advent-of-code-day-4/src/input.txt").inputStream()
    val passports = mutableListOf<Map<String, String>>();

    inputStream.bufferedReader().useLines { lines ->
        var passportAsString = mutableListOf<String>()
        lines.forEach {
            if (it.isNotEmpty()) {
                passportAsString.add(it)
            }
            else {
                passports.add(linesToPassport(passportAsString))
                passportAsString = mutableListOf()
            }
        }

        // Why does kotlin does not read the last line?
        // Documentation says the last line is not a line.
        // Human common sense says: It is.
        if (passportAsString.isNotEmpty())
            passports.add(linesToPassport(passportAsString))
    }

    val validPassportCount = passports.map { pp ->
        isPassportValid(pp)
    }.toList().sumBy { pp -> if (pp) 1 else 0 }

    val validPassportCount2 = passports.map { pp ->
        isPassportValid2(pp)
    }.toList().sumBy { pp -> if (pp) 1 else 0 }

    println(validPassportCount)
    println(validPassportCount2)
}

fun isPassportValid2(passport: Map<String, String>): Boolean {

    /* byr (Birth Year) - four digits; at least 1920 and at most 2002.
    iyr (Issue Year) - four digits; at least 2010 and at most 2020.
    eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
    hgt (Height) - a number followed by either cm or in:
    If cm, the number must be at least 150 and at most 193.
    If in, the number must be at least 59 and at most 76.
    hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
    ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
    pid (Passport ID) - a nine-digit number, including leading zeroes.
    cid (Country ID) - ignored, missing or not.
    {hgt=193cm, iyr=2020, eyr=2026, pid=0136642346, ecl=hzl, hcl=#efcc98, byr=1995}
    */

    val mandatoryKeys = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

    mandatoryKeys.forEach { key ->
        if (!passport.containsKey(key)) {
            return false
        }
    }

    try {
        // Byr
        val byr = passport["byr"]?.toInt() ?: return false
        if (byr < 1920 || byr > 2002) return false

        // Iyr
        val iyr = passport["iyr"]?.toInt() ?: return false
        if (iyr < 2010 || iyr > 2020) return false

        // Eyr
        val eyr = passport["eyr"]?.toInt() ?: return false
        if (eyr < 2020 || eyr > 2030) return false

        // hgt
        val hgt = passport["hgt"]
        val hgtMetric = hgt?.takeLast(2) ?: return false
        val hgtValue = hgt.take(hgt.length - 2).toInt()

        if (hgtMetric == "cm") {
            if (hgtValue < 150 || hgtValue > 193) return false
        } else if (hgtMetric == "in") {
            if (hgtValue < 59 || hgtValue > 76) return false
        } else {
            return false
        }

        // hcl
        val hcl = passport["hcl"]
        val hclLength = hcl?.length ?: return false
        val isValidColorHexCode = hcl.takeLast(6).all { colorHash ->
            "1234567890abcdef".contains(colorHash)
        }
        if (hclLength != 7) return false
        if (hcl.take(1) != "#") return false
        if (!isValidColorHexCode) return false

        // ecl
        val ecl = passport["ecl"]
        val validEntries = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
        if (validEntries.all { entry -> entry != ecl }) return false

        // pid
        val pid = passport["pid"]
        val pidString = pid ?: return false
        if (pid.length != 9) return false
        pidString.toInt()
    }
    catch (e: NumberFormatException) {
        return false;
    }

    return true;
}

fun isPassportValid(passport: Map<String, String>): Boolean {

    val mandatoryKeys = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

    mandatoryKeys.forEach { key ->
        if (!passport.containsKey(key)) {
            return false
        }
    }

    return true
}

fun linesToPassport(lines: MutableList<String>): Map<String, String> {

    val passport = mutableMapOf<String, String>()

    lines.forEach { line ->
        line.split(' ').forEach{ entry ->
            val keyVal = entry.split(":")
            passport[keyVal[0]] = keyVal[1]
        }
    }

    return passport
}