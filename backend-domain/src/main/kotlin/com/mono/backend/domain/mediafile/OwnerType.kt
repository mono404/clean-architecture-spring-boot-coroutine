package com.mono.backend.domain.mediafile

enum class OwnerType(
    val ownerTypeId: Int,
    val code: String,
) {
    PROFILE(0, "profile_image"),
    POST(1, "post_media"),
    ;

    companion object {
        fun from(ownerTypeId: Int): OwnerType = entries.find { it.ownerTypeId == ownerTypeId }
            ?: throw IllegalArgumentException("owner type id is not valid")
    }
}