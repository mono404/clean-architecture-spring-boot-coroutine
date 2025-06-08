package com.mono.backend.hotpost

import com.mono.backend.hotpost.response.HotPostResponse

interface HotPostUseCase {
    suspend fun readAll(dateStr: String): List<HotPostResponse>?
}