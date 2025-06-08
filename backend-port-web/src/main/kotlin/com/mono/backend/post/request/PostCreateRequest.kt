package com.mono.backend.post.request

import com.mono.backend.post.Post
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

data class PostCreateRequest(
    val title: String,
    val content: String,
    val memberId: Long,
    val boardId: Long
) {
    companion object {
        fun fromPart(parts: MultiValueMap<String, Part>, memberId: Long) = PostCreateRequest(
            title = (parts["title"]?.firstOrNull() as? FormFieldPart)?.value() ?: "",
            content = (parts["content"]?.firstOrNull() as? FormFieldPart)?.value() ?: "",
            memberId = memberId,
            boardId = (parts["boardId"]?.firstOrNull() as? FormFieldPart)?.value()?.toLongOrNull() ?: 0
        )
    }
    fun toDomain(postId: Long): Post {
        return Post(
            postId = postId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = 0
        )
    }
}