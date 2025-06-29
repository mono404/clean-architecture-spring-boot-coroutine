package com.mono.backend.infra.s3Client.util

import software.amazon.awssdk.core.SdkResponse

object AwsSdkUtils {

    fun checkSdkResponse(sdkResponse: SdkResponse) {
        if (isErrorSdkHttpResponse(sdkResponse)) {
            // TODO: UploadException
            throw RuntimeException(
                "${sdkResponse.sdkHttpResponse().statusCode()} - ${
                    sdkResponse.sdkHttpResponse().statusText()
                }"
            )
        }
    }

    private fun isErrorSdkHttpResponse(sdkResponse: SdkResponse): Boolean {
        return sdkResponse.sdkHttpResponse() == null || !sdkResponse.sdkHttpResponse().isSuccessful
    }
}