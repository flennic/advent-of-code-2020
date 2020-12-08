import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

fun main() {

    val inputStream: InputStream = File("advent-of-code-day-8/src/input.txt").inputStream()
    val sourceCode = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            sourceCode.add(line)
        }
    }

    println("Part 1")
    val gameBoy = HandheldGameConsole(sourceCode)
    gameBoy.run()

    println("Part 2")
    for ((cnt, instructionAsCode) in sourceCode.withIndex()) {
        val instructionSplit = instructionAsCode.split(' ')
        val instruction = Instruction(instructionSplit[0], instructionSplit[1].toInt())

        if (instruction.opCode == "acc") {
            continue
        }
        else if (instruction.opCode == "nop") {
            instruction.opCode = "jmp"
        }
        else if (instruction.opCode == "jmp") {
            instruction.opCode = "nop"
        }
        else {
            throw IllegalArgumentException("Do not support instruction ${instruction.opCode}")
        }

        val adjustedSourceCode = sourceCode.toMutableList()
        adjustedSourceCode[cnt] = "${instruction.opCode} ${if (instruction.value >= 0) "+" else ""}${instruction.value}"
        val gameBoy2 = HandheldGameConsole(adjustedSourceCode)
        if (gameBoy2.run()) {
            println("Found the instruction to fix!")
            println("acc ${gameBoy2.accumulator}")
            break
        }
    }
}

class HandheldGameConsole(sourceCode: MutableList<String>) {
    var accumulator: Int = 0
        private set
    private var programCounter: Int = 0
    private var instructionList = mutableListOf<Instruction>()
    private var executedInstructions = mutableListOf<Int>()

    init {
        for (codeInstruction in sourceCode) {
            addInstruction(codeInstruction)
        }
    }

    private fun addInstruction(codeInstruction: String) {
        val stringSplit = codeInstruction.split(' ')
        this.instructionList.add(Instruction(stringSplit[0], stringSplit[1].toInt()))
    }

    fun run(): Boolean {

        var res: Boolean

        do {
            try {
                res = executeInstruction()
            }
            // Using this for information passing is ugly but I don't want to rewrite it :)
            catch (e: IndexOutOfBoundsException) {
                println("Reached the end of the source code. acc: ${this.accumulator}, pc: ${this.programCounter}")
                return true
            }
        }
        while (res)
        
        return false
    }

    private fun executeInstruction(): Boolean {

        val instruction = this.instructionList[programCounter]

        return when (instruction.opCode) {
            "nop" -> {
                this.doNop()
            }
            "acc" -> {
                this.doAcc(instruction.value)
            }
            "jmp" -> {
                this.doJmp(instruction.value)
            }
            else -> {
                throw IllegalCallerException("Instruction ${instruction.opCode} not supported.")
            }
        }
    }

    private fun isNewAddressOk(offset: Int): Boolean {
        if (this.executedInstructions.contains(this.programCounter + offset)) {
            println("Detected infinite loop. acc: ${this.accumulator}, pc: ${this.programCounter}")
            return false
        }
        return true
    }

    private fun savePc() {
        this.executedInstructions.add(this.programCounter)
    }

    private fun doNop(): Boolean {
        if (this.isNewAddressOk(1)) {
            savePc()
            this.programCounter++
            return true
        }
        return false
    }

    private fun doAcc(value: Int): Boolean {
        if (this.isNewAddressOk(1)) {
            savePc()
            this.programCounter++
            this.accumulator += value
            return true
        }
        return false
    }

    private fun doJmp(relativeJumpAddr: Int): Boolean {
        if (this.isNewAddressOk(relativeJumpAddr)) {
            savePc()
            this.programCounter += relativeJumpAddr
            return true
        }
        return false
    }

    override fun toString(): String {

        val representation = StringBuilder()
        for ((cnt, instruction) in instructionList.withIndex()) {
            representation.append("${instruction.opCode} ${instruction.value}")

            if (cnt == this.programCounter)
                representation.append(" <-")

            representation.append("\n")
        }
        return representation.toString()
    }
}

data class Instruction(
    var opCode: String = "",
    var value: Int = 0
)