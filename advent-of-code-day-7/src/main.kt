import java.io.File
import java.io.InputStream

fun main() {
    val inputStream: InputStream = File("advent-of-code-day-7/src/input.txt").inputStream()
    val bagTypes = mutableListOf<BagType>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            bagTypes.add(BagType(line))
        }
    }

    val res = bagTypes.map{ contains(addChildrenToNode(it, bagTypes), "shiny gold") }.toList().sum()
    val res2 = contains(addChildrenToNode(bagTypes.single{ it.name == "shiny gold"}, bagTypes), "shiny gold", true)

    println(res)
    println(res2)
}

fun contains(rootNode: BagNode, name: String, descendants: Boolean = false): Int {

    if (descendants)
        return containsDescendants(rootNode, name)

    return if (containsMany(rootNode, name) > 0) 1 else 0
}

fun containsDescendants(rootNode: BagNode, name: String): Int {

    if (rootNode.children.size == 0)
        return rootNode.quantity

    var descendantBags = rootNode.children.map { containsDescendants(it, name) }.toList().sum() * rootNode.quantity

    if (rootNode.name != name)
        descendantBags += rootNode.quantity

    return descendantBags
}

fun containsMany(rootNode: BagNode, name: String): Int {

    if (rootNode.children.any{child -> child.name == name})
        return 1

    return rootNode.children.map { containsMany(it, name) }.toList().sum()
}

fun addChildrenToNode(bagType: BagType, allBagTypes: MutableList<BagType>): BagNode {

    val rootNode = BagNode(bagType)

    // That part seems a bit cumbersome... but well, it works :o
    for (child in bagType.containedBags) {
        val childNode = BagNode(child.key,child.value, rootNode)
        val childBagType = allBagTypes.single { it.name == childNode.name }
        val childNodeWithChildren = addChildrenToNode(childBagType, allBagTypes)
        childNodeWithChildren.parent = rootNode
        childNodeWithChildren.quantity = childNode.quantity
        rootNode.children.add(childNodeWithChildren)
    }

    return rootNode
}

class BagNode() {
    var name: String = ""
    var quantity: Int = 1
    var parent: BagNode? = null
    var children = mutableListOf<BagNode>()

    constructor(bagType: BagType) : this() {
        this.name = bagType.name
        this.quantity = 1
        this.parent = null
        this.children = mutableListOf()
    }

    constructor(name: String, quantity: Int, parent: BagNode) : this() {
        this.name = name
        this.quantity = quantity
        this.parent = parent
        this.children = mutableListOf()
    }
}

class BagType(description: String) {
    var name: String = ""
    var containedBags = mutableMapOf<String, Int>()

    init {
        val descriptionsSplit = description.split(" ")
        this.name = "${descriptionsSplit[0]} ${descriptionsSplit[1]}"

        if (descriptionsSplit[4] != "no") {
            var i = 0
            do {
                i += 4
                this.containedBags["${descriptionsSplit[i+1]} ${descriptionsSplit[i+2]}"] = descriptionsSplit[i].toInt()
            } while (descriptionsSplit[i+3].last() != '.')
        }
    }

    override fun toString(): String {
        val representation = StringBuilder().append("${this.name}\n")

        for (containedBag in this.containedBags) {
            representation.append("${containedBag.key}: ${containedBag.value}\n")
        }

        representation.append("\n")

        return representation.toString()
    }
}