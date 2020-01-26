package com.seiko.torrent.util.extensions

import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.data.model.filetree.FileNode
import com.seiko.torrent.data.model.filetree.FileTree
import com.seiko.download.torrent.model.BencodeFileItem
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/*
 * Not recommended for use with a large number of nodes
 * due to deterioration of performance, use getLeaves() method
 */
internal fun <F : FileTree<F>> F.find(index: Int): F? {
    val stack = Stack<F>()
    stack.push(this)
    while (stack.isNotEmpty()) {
        val node = stack.pop() ?: continue
        if (node.index == index) {
            return node
        } else {
            for (n in node.childrenName) {
                if (!node.isFile) {
                    stack.push(node.getChild(n))
                }
            }
        }
    }
    return null
}

/*
 * Returns the leaf nodes of the tree.
 */
internal fun <F : FileTree<F>> F.getLeaves(): List<F> {
    val stack = Stack<F>()
    stack.push(this)

    val leaves = ArrayList<F>()
    while (stack.isNotEmpty()) {
        val node = stack.pop() ?: continue
        if (node.isFile) {
            leaves.add(node)
        }
        for (n in node.children) {
            if (!node.isFile) {
                stack.push(n)
            }
        }
    }
    return leaves
}


internal fun List<BencodeFileItem>.toFileTree(): BencodeFileTree {
    val root = BencodeFileTree(FileTree.ROOT, 0L, FileNode.Type.DIR)
    var parentTree = root
    /* It allows reduce the number of iterations on the paths with equal beginnings */
    var prevPath = ""
    val filesCopy = ArrayList(this)
    /* Sort reduces the returns number to root */
    filesCopy.sort()

    for (file in filesCopy) {
        val path: String
        /*
         * Compare previous path with new path.
         * Example:
         * prev = dir1/dir2/
         * cur  = dir1/dir2/file1
         *        |________|
         *          equal
         *
         * prev = dir1/dir2/
         * cur  = dir3/file2
         *        |________|
         *         not equal
         */
        if (prevPath.isNotEmpty()
            && file.path.regionMatches(
                0, prevPath,
                0, prevPath.length,
                true)) {
            /*
             * If beginning paths are equal, remove previous path from the new path.
             * Example:
             * prev = dir1/dir2/
             * cur  = dir1/dir2/file1
             * new  = file1
             */
            path = file.path.substring(prevPath.length)
        } else {
            /* If beginning paths are not equal, return to root */
            path = file.path
            parentTree = root
        }

        val nodes = path.parsePath()
        /*
         * Remove last node (file) from previous path.
         * Example:
         * cur = dir1/dir2/file1
         * new = dir1/dir2/
         */
        prevPath = file.path.substring(0,
            file.path.length - nodes[nodes.size - 1].length)

        /* Iterates path nodes */
        nodes.forEachIndexed { i, node ->
            if (!parentTree.contains(node)) {
                parentTree.addChild(makeObject(
                    index = file.index,
                    name = node,
                    size = file.size,
                    parent = parentTree,
                    isFile = i == nodes.size - 1
                ))
            }

            val nextParent = parentTree.getChild(node)
            /* Skipping leaf nodes */
            if (!nextParent.isFile) {
                parentTree = nextParent
            }
        }
    }
    return root
}

private fun makeObject(index: Int,
                       name: String,
                       size: Long,
                       parent: BencodeFileTree,
                       isFile: Boolean): BencodeFileTree {
    return if (isFile) {
        BencodeFileTree(index, name, size, FileNode.Type.FILE, parent)
    } else {
        BencodeFileTree(name, 0L, FileNode.Type.DIR, parent)
    }
}

private fun String.parsePath(): List<String> {
    if (isNullOrEmpty()) return emptyList()
    return split(File.separator)
}
