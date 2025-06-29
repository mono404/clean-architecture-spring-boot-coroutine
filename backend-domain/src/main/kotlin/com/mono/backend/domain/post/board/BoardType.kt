package com.mono.backend.domain.post.board

enum class BoardType(
    val id: Long,
    val code: String,
    val desc: String
) {
    ALL(0, "ALL", "전체"),
    FREE(1, "FREE", "자유"),
    QUESTION(2, "QUESTION", "질문"),
    REVIEW(3, "REVIEW", "리뷰"),
    ;

    companion object {
        private val idMap = entries.associateBy(BoardType::id)
        private val codeMap = entries.associateBy(BoardType::code)
        private val descMap = entries.associateBy(BoardType::desc)

        fun fromId(id: Long): BoardType = idMap[id] ?: ALL
        fun fromCode(code: String): BoardType = codeMap[code] ?: ALL
        fun fromDesc(desc: String): BoardType = descMap[desc] ?: ALL
    }
}