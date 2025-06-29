package com.mono.backend.port.web.post.dto

import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

data class PostCreateRequest(
    val title: String,
    val content: String,
    val memberId: Long,
    val boardType: BoardType
) {
    companion object {
        fun fromPart(parts: MultiValueMap<String, Part>, memberId: Long) = PostCreateRequest(
            title = (parts["title"]?.firstOrNull() as? FormFieldPart)?.value() ?: "",
            content = (parts["content"]?.firstOrNull() as? FormFieldPart)?.value() ?: "",
            memberId = memberId,
            boardType = (parts["boardId"]?.firstOrNull() as? FormFieldPart)?.value()?.let { BoardType.valueOf(it) }
                ?: BoardType.ALL
        )
    }

    fun toDomain(postId: Long): Post {
        return Post(
            postId = postId,
            title = title,
            content = content,
            boardType = boardType,
            writerId = 0
        )
    }
}

data class PostUpdateRequest(
    val title: String,
    val content: String,
)