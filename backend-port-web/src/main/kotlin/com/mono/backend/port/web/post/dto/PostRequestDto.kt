package com.mono.backend.port.web.post.dto

import com.mono.backend.domain.post.board.BoardType
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

data class PostCreateRequest(
    val title: String,
    val content: String,
    val boardType: BoardType
) {
    companion object {
        fun fromPart(parts: MultiValueMap<String, Part>) = PostCreateRequest(
            title = (parts["title"]?.firstOrNull() as? FormFieldPart)?.value() ?: "",
            content = (parts["content"]?.firstOrNull() as? FormFieldPart)?.value() ?: "",
            boardType = (parts["boardId"]?.firstOrNull() as? FormFieldPart)?.value()?.toLong()
                ?.let { BoardType.fromId(it) }
                ?: BoardType.ALL
        )
    }
}

data class PostUpdateRequest(
    val title: String,
    val content: String,
)