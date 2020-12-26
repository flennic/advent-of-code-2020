import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException
import kotlin.math.abs

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-12/src/input.txt").inputStream()
    val content = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            content.add(line)
        }
    }

    val cruise = ShipCruise(content)
    cruise.drive()
    print(cruise)

    val cruise2 = ShipCruise2(content)
    cruise2.drive()
    print(cruise2)
}

class ShipCruise() {

    private var location = Location(Position(0, 0), CardinalDirection.EAST)
    private var cruiseInstructions: List<Action> = mutableListOf()

    constructor(input: List<String>) : this() {
        cruiseInstructions = input.map { inputToAction(it) }
    }

    fun drive() {
        this.cruiseInstructions.forEach { ci ->
            executeAction(ci)
        }
    }

    override fun toString(): String {
        val representation = StringBuilder()

        representation.append("Location: ( ${this.location.Position.X} | ${this.location.Position.Y} )\n")
        representation.append("Manhattan Distance: ${this.getManhattanDistance()}\n\n")

        return representation.toString()
    }

    private fun getManhattanDistance(): Int {
        return abs(this.location.Position.X) + abs(this.location.Position.Y)
    }

    private fun executeAction(action: Action) {
        when (action.ActionType) {
            ActionType.NORTH -> this.location.Position.Y += action.Value
            ActionType.EAST -> this.location.Position.X += action.Value
            ActionType.SOUTH -> this.location.Position.Y -= action.Value
            ActionType.WEST -> this.location.Position.X -= action.Value
            ActionType.TURN_LEFT -> setCardinalDirectionAfterTurn(action)
            ActionType.TURN_RIGHT -> setCardinalDirectionAfterTurn(action)
            ActionType.MOVE_FORWARD -> setPositionAfterMoveForward(action.Value)
        }
    }

    private fun setCardinalDirectionAfterTurn(action: Action) {

        // N = 0, E = 1, S = 2, W = 3
        val currentAlignment = this.location.CardinalDirection.ordinal

        when (action.ActionType) {
            ActionType.TURN_LEFT -> {
                this.location.CardinalDirection = CardinalDirection.values()[Math.floorMod(currentAlignment - action.Value / 90, 4)]
            }
            ActionType.TURN_RIGHT -> {
                this.location.CardinalDirection = CardinalDirection.values()[Math.floorMod(currentAlignment + action.Value / 90, 4)]
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    private fun setPositionAfterMoveForward(value: Int) {
        when (this.location.CardinalDirection) {
            CardinalDirection.NORTH -> this.location.Position.Y += value
            CardinalDirection.EAST -> this.location.Position.X += value
            CardinalDirection.SOUTH -> this.location.Position.Y -= value
            CardinalDirection.WEST -> this.location.Position.X -= value
        }
    }

    private fun inputToAction(inputInstruction: String): Action {
        val actionTypeAsChar = inputInstruction[0]
        val actionValueAsString = inputInstruction.drop(1)

        val actionValue = actionValueAsString.toInt()

        return when (actionTypeAsChar) {
            'N' -> Action(ActionType.NORTH, actionValue)
            'E' -> Action(ActionType.EAST, actionValue)
            'S' -> Action(ActionType.SOUTH, actionValue)
            'W' -> Action(ActionType.WEST, actionValue)
            'L' -> Action(ActionType.TURN_LEFT, actionValue)
            'R' -> Action(ActionType.TURN_RIGHT, actionValue)
            'F' -> Action(ActionType.MOVE_FORWARD, actionValue)
            else -> {
                throw IllegalArgumentException()
            }
        }
    }
}


class ShipCruise2() {

    private var relativeWaypoint = Position(0, 0)
    private val initialWaypointOffset = Position(10, 1)
    private var location = Position(0, 0)
    private var cruiseInstructions: List<Action> = mutableListOf()

    constructor(input: List<String>) : this() {
        cruiseInstructions = input.map { inputToAction(it) }
        relativeWaypoint = initialWaypointOffset
    }

    fun drive() {
        print(this.toString())
        this.cruiseInstructions.forEach { ci ->
            executeAction(ci)
            print(this.toString())
        }
    }

    override fun toString(): String {
        val representation = StringBuilder()

        representation.append("Location: ( ${this.location.X} | ${this.location.Y} )\n")
        representation.append("Waypoint: ( ${this.relativeWaypoint.X} | ${this.relativeWaypoint.Y} )\n")
        representation.append("Manhattan Distance: ${this.getManhattanDistance()}\n\n")

        return representation.toString()
    }

    private fun getManhattanDistance(): Int {
        return abs(this.location.X) + abs(this.location.Y)
    }

    private fun executeAction(action: Action) {
        when (action.ActionType) {
            ActionType.NORTH -> this.relativeWaypoint.Y += action.Value
            ActionType.EAST -> this.relativeWaypoint.X += action.Value
            ActionType.SOUTH -> this.relativeWaypoint.Y -= action.Value
            ActionType.WEST -> this.relativeWaypoint.X -= action.Value
            ActionType.TURN_LEFT -> rotateWaypoint(action)
            ActionType.TURN_RIGHT -> rotateWaypoint(action)
            ActionType.MOVE_FORWARD -> moveToWaypoint(action)
        }
    }

    private fun rotateWaypoint(action: Action) {

        val rotateQuarters: Int = when (action.ActionType) {
            ActionType.TURN_RIGHT -> {
                action.Value / 90
            }
            ActionType.TURN_LEFT -> {
                4 - action.Value / 90
            }
            else -> {
                throw IllegalArgumentException()
            }
        }

        val newRelativeWayPoint = Position(0, 0)

        when (rotateQuarters) {
            1 -> {
                newRelativeWayPoint.X = +this.relativeWaypoint.Y
                newRelativeWayPoint.Y = -this.relativeWaypoint.X
            }
            2 -> {
                newRelativeWayPoint.X = -this.relativeWaypoint.X
                newRelativeWayPoint.Y = -this.relativeWaypoint.Y
            }
            3 -> {
                newRelativeWayPoint.X = -this.relativeWaypoint.Y
                newRelativeWayPoint.Y = +this.relativeWaypoint.X
            }
        }

        this.relativeWaypoint = newRelativeWayPoint
    }

    private fun moveToWaypoint(action: Action) {
        this.location = Position(this.location.X + action.Value * this.relativeWaypoint.X, this.location.Y + action.Value * this.relativeWaypoint.Y)
    }

    private fun inputToAction(inputInstruction: String): Action {
        val actionTypeAsChar = inputInstruction[0]
        val actionValueAsString = inputInstruction.drop(1)

        val actionValue = actionValueAsString.toInt()

        return when (actionTypeAsChar) {
            'N' -> Action(ActionType.NORTH, actionValue)
            'E' -> Action(ActionType.EAST, actionValue)
            'S' -> Action(ActionType.SOUTH, actionValue)
            'W' -> Action(ActionType.WEST, actionValue)
            'L' -> Action(ActionType.TURN_LEFT, actionValue)
            'R' -> Action(ActionType.TURN_RIGHT, actionValue)
            'F' -> Action(ActionType.MOVE_FORWARD, actionValue)
            else -> {
                throw IllegalArgumentException()

            }
        }
    }
}

data class Position(
        var X: Int,
        var Y: Int
)

data class Location(
        var Position: Position,
        var CardinalDirection: CardinalDirection
)

enum class ActionType {
    NORTH, EAST, SOUTH, WEST, TURN_LEFT, TURN_RIGHT, MOVE_FORWARD
}

enum class CardinalDirection {
    NORTH, EAST, SOUTH, WEST
}

data class Action(
        var ActionType: ActionType,
        var Value: Int
)