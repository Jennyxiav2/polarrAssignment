package xyz.rthqks.section1.tree

import com.squareup.moshi.*
import kotlin.random.Random


object TreeUtils {
    private const val MAX_NODE_INT_VALUE = 1000

    fun generateTree(depth: Int, maxChildren: Int): Tree<Int> {
        val tree = Tree(Random.nextInt(MAX_NODE_INT_VALUE))

        var node = tree.root

        // ensure tree reaches passed in depth
        for (i in 1 until depth) {

            node = addRandomChild(node)

            if (depth - i > 0) {
                // add random subtree to new node like normal
                generateSubtree(node, depth - i, maxChildren)
            }
        }

        // we ensured we reached proper depth, but need to potentially add more random children
        generateSubtree(tree.root, depth - 1, maxChildren)
        return tree
    }

    private fun generateSubtree(parent: Tree.Node<Int>, depth: Int, maxChildren: Int) {
        // subtract children size if node already has children from ensuring desired depth
        val numChildren = Random.nextInt(maxChildren + 1) - parent.children.size

        for (i in 0 until numChildren) {
            val node = addRandomChild(parent)

            if (depth > 1) {
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

    fun serializeJson(tree: Tree<Int>): String {
        val nodeType =
            Types.newParameterizedTypeWithOwner(Tree::class.java, Tree.Node::class.java, Int::class.javaObjectType)

        val moshi = Moshi.Builder()
            .add(nodeType, NodeAdapter())
            .build()

        val type = Types.newParameterizedType(Tree::class.java, Int::class.javaObjectType)
        return moshi.adapter<Tree<Int>>(type).toJson(tree)
    }

    fun serializeEncode(tree: Tree<Int>, encoder: (Int) -> Int): String {
        val nodeType =
            Types.newParameterizedTypeWithOwner(Tree::class.java, Tree.Node::class.java, Int::class.javaObjectType)

        val moshi = Moshi.Builder()
            .add(nodeType, NodeAdapter(encoder))
            .build()

        val type = Types.newParameterizedType(Tree::class.java, Int::class.javaObjectType)
        return moshi.adapter<Tree<Int>>(type).toJson(tree)
    }

    fun deserialize(json: String): Tree<Int> {

        val nodeType =
            Types.newParameterizedTypeWithOwner(Tree::class.java, Tree.Node::class.java, Int::class.javaObjectType)

        val moshi = Moshi.Builder()
            .add(nodeType, NodeAdapter())
            .build()

        val type = Types.newParameterizedType(Tree::class.java, Int::class.javaObjectType)
        val tree = moshi.adapter<Tree<Int>>(type).fromJson(json)!!
        // we didn't serialize parents (cyclic structure), so we reset parent pointers here
        setParents(tree.root)
        return tree
    }

    fun deserializeDecode(json: String, decoder: (Int) -> Int): Tree<Int> {
        val nodeType =
            Types.newParameterizedTypeWithOwner(Tree::class.java, Tree.Node::class.java, Int::class.javaObjectType)

        val moshi = Moshi.Builder()
            .add(nodeType, NodeAdapter(decoder = decoder))
            .build()

        val type = Types.newParameterizedType(Tree::class.java, Int::class.javaObjectType)
        val tree = moshi.adapter<Tree<Int>>(type).fromJson(json)!!
        // we didn't serialize parents (cyclic structure), so we reset parent pointers here
        setParents(tree.root)
        return tree
    }

    private fun setParents(node: Tree.Node<Int>) {
        node.children.forEach {
            it.parent = node
            setParents(it)
        }
    }
}

class NodeAdapter(
    private val encoder: (Int) -> Int = { it },
    private val decoder: (Int) -> Int = { it }
) : JsonAdapter<Tree.Node<Int>>() {

    private val nodeType =
        Types.newParameterizedTypeWithOwner(Tree::class.java, Tree.Node::class.java, Int::class.javaObjectType)

    private val listAdapter = Moshi.Builder()
        .add(nodeType, this)
        .build()
        .adapter<List<Tree.Node<Int>>>(
            Types.newParameterizedType(
                List::class.java,
                nodeType
            )
        )

    override fun fromJson(reader: JsonReader): Tree.Node<Int>? {
        val node = Tree.Node<Int>()
        reader.beginObject()
        reader.nextName()
        node.data = decoder.invoke(reader.nextInt())
        reader.nextName()
        node.children = listAdapter.fromJson(reader)
        reader.endObject()
        return node
    }

    override fun toJson(writer: JsonWriter, value: Tree.Node<Int>?) {
        value?.let {
            writer.beginObject()
            writer.name("data")
            writer.value(encoder.invoke(it.data))
            writer.name("children")
            listAdapter.toJson(writer, it.children)
            writer.endObject()
        }
    }

}