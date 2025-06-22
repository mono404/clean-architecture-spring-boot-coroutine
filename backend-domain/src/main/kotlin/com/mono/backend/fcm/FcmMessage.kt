package com.mono.backend.fcm

data class FcmMessage(
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap()  // 추가 커스텀 페이로드
)
