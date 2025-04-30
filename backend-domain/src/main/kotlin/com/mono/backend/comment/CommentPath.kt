package com.mono.backend.comment

data class CommentPath(
    val path: String = "00000"
) {
    companion object {
        private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

        private const val DEPTH_CHUNK_SIZE = 5
        private const val MAX_DEPTH = 5

        // MIN_CHUNK = "00000", MAX_CHUNK = "zzzzz"
        private val MIN_CHUNK = "0".repeat(DEPTH_CHUNK_SIZE)
        private val MAX_CHUNK = "z".repeat(DEPTH_CHUNK_SIZE)
    }

    fun create(path: String): CommentPath {
        if (isDepthOverflowed(path)) {
            throw IllegalStateException("depth overflowed")
        }
        return CommentPath(path)
    }

    private fun isDepthOverflowed(path: String): Boolean {
        return calDepth(path) > MAX_DEPTH
    }

    private fun calDepth(path: String): Int {
        return path.length / DEPTH_CHUNK_SIZE
    }

    fun getDepth(): Int {
        return calDepth(path)
    }

    fun isRoot(): Boolean {
        return calDepth(path) == 0
    }

    fun getParentPath(): String {
        return path.substring(0, path.length - DEPTH_CHUNK_SIZE)
    }

    fun createChildCommentPath(descendantsTopPath: String?): CommentPath {
        if (descendantsTopPath == null) {
            return create(path + MIN_CHUNK)
        }
        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return create(increase(childrenTopPath))
    }

    private fun findChildrenTopPath(descendantsTopPath: String): String {
        return descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE)
    }

    private fun increase(path: String): String {
        val lastChunk = path.substring(path.length - DEPTH_CHUNK_SIZE)
        if (isChunkOverflowed(lastChunk)) {
            throw IllegalStateException("chunk overflowed")
        }

        var value = 0
        for (ch in lastChunk) {
            value = value * CHARSET.length + CHARSET.indexOf(ch)
        }

        value++

        var result = ""
        for (i in 0 until DEPTH_CHUNK_SIZE) {
            result = CHARSET[value % CHARSET.length] + result
            value /= CHARSET.length
        }

        return path.substring(0, path.length - DEPTH_CHUNK_SIZE) + result
    }

    private fun isChunkOverflowed(lastChunk: String): Boolean {
        return lastChunk == MAX_CHUNK
    }
}