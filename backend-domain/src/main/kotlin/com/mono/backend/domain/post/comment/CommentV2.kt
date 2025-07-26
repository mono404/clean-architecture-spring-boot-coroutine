package com.mono.backend.domain.post.comment

import com.mono.backend.domain.common.member.EmbeddedMember
import java.time.LocalDateTime

data class CommentV2(
    val commentId: Long,
    val content: String,
    val postId: Long,
    val commentPath: CommentPath,
    var deleted: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    val member: EmbeddedMember,
) {
    companion object {
        private const val MAX_CONTENT_LENGTH = 1000
        private const val MIN_CONTENT_LENGTH = 1

        fun create(
            commentId: Long,
            content: String,
            postId: Long,
            commentPath: CommentPath,
            member: EmbeddedMember
        ): CommentV2 {
            validateContent(content)

            return CommentV2(
                commentId = commentId,
                content = content.trim(),
                postId = postId,
                commentPath = commentPath,
                member = member,
            )
        }

        private fun validateContent(content: String) {
            require(content.trim().length in MIN_CONTENT_LENGTH..MAX_CONTENT_LENGTH) {
                "댓글 내용은 ${MIN_CONTENT_LENGTH}자 이상 ${MAX_CONTENT_LENGTH}자 이하여야 합니다."
            }
        }
    }

    fun isRoot(): Boolean = commentPath.isRoot()

    fun delete(): CommentV2 = copy(deleted = true, updatedAt = LocalDateTime.now())

    fun update(newContent: String): CommentV2 {
        validateContent(newContent)
        return copy(content = newContent.trim(), updatedAt = LocalDateTime.now())
    }

    fun canBeEditedBy(memberId: Long): Boolean {
        return !deleted && member.memberId == memberId
    }

    fun canBeDeletedBy(memberId: Long): Boolean {
        return !deleted && member.memberId == memberId
    }
}