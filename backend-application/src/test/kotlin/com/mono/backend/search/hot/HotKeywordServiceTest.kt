package com.mono.backend.search.hot

import com.mono.backend.domain.search.hot.HotKeywordScope
import com.mono.backend.port.infra.search.cache.hot.HotKeywordCachePort
import com.mono.backend.service.search.hot.HotKeywordService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [MockKExtension::class])
class HotKeywordServiceTest {
    @MockK
    lateinit var hotKeywordCachePort: HotKeywordCachePort

    lateinit var hotKeywordService: HotKeywordService

    @BeforeEach
    fun setUp() {
        hotKeywordService = HotKeywordService(hotKeywordCachePort)
    }

    @Test
    fun `recordKeyword should call increaseKeywordScore for each scope`() = runTest {
        // given
        val type = "post"
        val keyword = "침낭"

        coEvery {
            hotKeywordCachePort.increaseKeywordScore(any(), any(), any())
        } just Runs

        // when
        hotKeywordService.recordKeyword(type, keyword)

        // then
        HotKeywordScope.entries.forEach {
            coVerify(timeout = 1000) {
                hotKeywordCachePort.increaseKeywordScore(type, keyword, it)
            }
        }
    }
}