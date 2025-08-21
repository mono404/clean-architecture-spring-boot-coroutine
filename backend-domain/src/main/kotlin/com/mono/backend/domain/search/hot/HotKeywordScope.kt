package com.mono.backend.domain.search.hot

import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class HotKeywordScope(
    val ttl: Duration
) {
    // TTL 은 여유 있게
    DAILY(Duration.ofDays(2)),
    WEEKLY(Duration.ofDays(8)),
    ;

    fun todayKeyPrefix(type: String): String {
        val date = LocalDate.now(ZoneId.of("Asia/Seoul"))
        return date.format(DateTimeFormatter.BASIC_ISO_DATE)
    }
}