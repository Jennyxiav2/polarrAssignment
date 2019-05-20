package xyz.rthqks.section1.tree

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.random.Random


object TreeUtils {
    private const val MAX_NODE_INT_VALUE = 1000
    private const val MARK_CHILD = 0
    private const val MARK_END_CHILD = 1

    fun generateTree(depth: Int, maxChildren: Int): Tree<Int> {
        val tree = Tree(Random.nextInt(MAX_NODE_INT_VALUE))

        var node = tree.root

        // ensure tree reaches passed in depth
        // tree.root is level 1
        for (i in 2..depth) {
            node = addRandomChild(node)
        }

        // now that we have a path of `depth`, add random subtrees to those nodes up to maxChildren
        node = tree.root
        for (i in 2..depth) {
            generateSubtree(node, depth - i, maxChildren)
            node = node.children[0]
        }

        return tree
    }

    private fun generateSubtree(parent: Tree.Node<Int>, depth: Int, maxChildren: Int) {
        // subtract children size if node already has children from ensuring desired depth
        val numChildren = Random.nextInt(maxChildren + 1) - parent.children.size

        for (i in 1..numChildren) {
            val node = addRandomChild(parent)

            if (depth > 0) {
                generateSubtree(node, depth - 1, maxChildren)
            }
        }
    }

    private fun addRandomChild(parent: Tree.Node<Int>): Tree.Node<Int> {
        val node = Tree.Node<Int>()
        node.data = Random.nextInt(MAX_NODE_INT_VALUE)
        node.parent = parent
        node.children = mutableListOf()

        parent.children.add(node)
        return node
    }

    fun serialize(node: Tree.Node<Int>, sb: StringBuilder = StringBuilder(), indent: String = ""): String {
        sb.append(indent).append('(').append(node.data.toString())
        if (node.children.isEmpty()) {
            sb.append(')')
        } else {
            node.children.forEach {
                sb.append("\n")
                serialize(it, sb, "$indent  ")
            }
            sb.append("\n").append(indent).append(")")
        }
        return sb.toString()
    }

    fun serializeToStream(tree: Tree<Int>, stream: OutputStream) {
        serializeToStream(tree.root, DataOutputStream(EncryptedOutputStream(stream, 0xCAFEBAE)))
    }

    private fun serializeToStream(node: Tree.Node<Int>, stream: DataOutputStream) {
        stream.writeInt(node.data)

        node.children.forEach {
            stream.writeByte(MARK_CHILD)
            serializeToStream(it, stream)
        }

        stream.writeByte(MARK_END_CHILD)
    }

    fun deserializeFromStream(stream: InputStream): Tree<Int> {
        val tree = Tree(0)
        deserializeFromStream(tree.root, DataInputStream(EncryptedInputStream(stream, 0xCAFEBAE)))
        return tree
    }

    private fun deserializeFromStream(node: Tree.Node<Int>, stream: DataInputStream) {
        node.data = stream.readInt()
        var type = stream.read()

        while (type == MARK_CHILD) {
            val child = Tree.Node<Int>()
            child.parent = node
            child.children = mutableListOf()
            node.children.add(child)
            deserializeFromStream(child, stream)
            type = stream.read()
        }
    }
}

class EncryptedOutputStream(private val out: OutputStream, private val secret: Int) : OutputStream() {
    override fun write(b: Int) {
        out.write(b xor secret)
    }
}

class EncryptedInputStream(private val input: InputStream, private val secret: Int) : InputStream() {
    override fun read(): Int {
        val b = input.read()
        val enc = (b xor secret)
//         we're only xoring a byte at a time :\
        return enc and 0x000000ff
    }

}