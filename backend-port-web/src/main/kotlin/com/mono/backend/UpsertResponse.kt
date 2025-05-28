package com.mono.backend

data class UpsertResponse<T>(
    val isCreate: Boolean,
    val body: T,
) {

    val typeText: String
        get() {
            return if (isCreate) CREATED else UPDATED
        }

    companion object {
        private const val CREATED = "created"
        private const val UPDATED = "updated"

        @JvmStatic
        fun <T> created(response: T): UpsertResponse<T> = UpsertResponse(true, response)

        @JvmStatic
        fun <T> updated(response: T): UpsertResponse<T> = UpsertResponse(false, response)
    }
}
