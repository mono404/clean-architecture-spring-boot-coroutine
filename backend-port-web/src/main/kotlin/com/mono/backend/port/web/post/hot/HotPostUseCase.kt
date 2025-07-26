package com.mono.backend.port.web.post.hot

import com.mono.backend.port.web.post.hot.dto.HotPostResponse

interface HotPostUseCase {
    suspend fun readAll(memberId: Long?, dateStr: String): List<HotPostResponse>?
}