package com.mono.backend.port.web.post

import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.web.post.dto.PostReadPageResponse
import com.mono.backend.port.web.post.dto.PostReadResponse

interface PostQueryUseCase {
    suspend fun read(postId: Long): PostReadResponse
    suspend fun readAll(boardType: BoardType, pageRequest: PageRequest): PostReadPageResponse
    suspend fun readAllInfiniteScroll(boardType: BoardType, cursorRequest: CursorRequest): List<PostReadResponse>
    suspend fun count(boardType: BoardType): Long
}