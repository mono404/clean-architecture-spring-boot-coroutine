package com.mono.backend.domain.post

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.post.board.BoardType
import java.time.LocalDateTime

data class Post(
    val postId: Long,
    val title: String,
    val content: String,
    val boardType: BoardType,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    val member: EmbeddedMember,
) {
    companion object {
        private const val MAX_TITLE_LENGTH = 200
        private const val MAX_CONTENT_LENGTH = 10000
        private const val MIN_TITLE_LENGTH = 1
        private const val MIN_CONTENT_LENGTH = 1

        /**
         * 기본 생성자는 Entity에서 Domain으로 변환할 때 사용
         * 그 외에는, 검증을 위해 생성자를 사용해야 함
         */
        fun create(
            postId: Long,
            title: String,
            content: String,
            boardType: BoardType,
            member: EmbeddedMember
        ): Post {
            validateTitle(title)
            validateContent(content)

            return Post(
                postId = postId,
                title = title.trim(),
                content = content.trim(),
                boardType = boardType,
                member = member,
            )
        }

        private fun validateTitle(title: String) {
            require(title.trim().length in MIN_TITLE_LENGTH..MAX_TITLE_LENGTH) {
                "제목은 ${MIN_TITLE_LENGTH}자 이상 ${MAX_TITLE_LENGTH}자 이하여야 합니다."
            }
        }

        private fun validateContent(content: String) {
            require(content.trim().length in MIN_CONTENT_LENGTH..MAX_CONTENT_LENGTH) {
                "내용은 ${MIN_CONTENT_LENGTH}자 이상 ${MAX_CONTENT_LENGTH}자 이하여야 합니다."
            }
        }
    }

    fun update(newTitle: String, newContent: String): Post {
        validateTitle(newTitle)
        validateContent(newContent)

        return copy(
            title = newTitle.trim(),
            content = newContent.trim(),
            updatedAt = LocalDateTime.now()
        )
    }

    fun canBeEditedBy(memberId: Long): Boolean {
        return member.memberId == memberId
    }

    fun canBeDeletedBy(memberId: Long): Boolean {
        return member.memberId == memberId
    }

    fun isOwnedBy(memberId: Long): Boolean {
        return member.memberId == memberId
    }

    fun containsImages(): Boolean {
        return content.contains(Regex("""!\[\]\([^)]+\)"""))
    }

    fun extractImageBlobIds(): List<String> {
        return Regex("""!\[\]\(([^)]+)\)""")
            .findAll(content)
            .map { it.groupValues[1] }
            .toList()
    }

    fun replaceImageUrls(urlMap: Map<String, String>): Post {
        val updatedContent = urlMap.entries.fold(content) { acc, (blobUrl, s3Url) ->
            acc.replace(blobUrl, s3Url)
        }
        return copy(content = updatedContent)
    }

    fun hasValidContent(): Boolean {
        return title.isNotBlank() && content.isNotBlank()
    }
}