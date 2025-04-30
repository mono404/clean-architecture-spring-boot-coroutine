package com.mono.backend.hotarticle

import com.mono.backend.hotarticle.response.HotArticleResponse

interface HotArticleUseCase {
    suspend fun readAll(dateStr: String): List<HotArticleResponse>?
}