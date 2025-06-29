package com.mono.backend.auth

import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.port.web.auth.LoginResponse
import com.mono.backend.port.web.auth.SocialLoginRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class AuthApiTest {
    private val restClient = RestClient.create("http://localhost:3001")

    @Test
    fun test() = runBlocking {
        val response = create(
            SocialLoginRequest(
                provider = SocialProvider.KAKAO,
                accessToken = "pvSssNub3RywOIql7cW0Sos5LGYv7NUdAAAAAQoNG5oAAAGWqf4SB_oXDHwO3UaB",
                idToken = "eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJjZjFjMDgxYmI1NGZjYjk2MTBmMjc3NWI2YTUzNTRiMyIsInN1YiI6IjQyNDk4MTUxNDMiLCJhdXRoX3RpbWUiOjE3NDY2MDg3MjIsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwibmlja25hbWUiOiLquYDsm5DtmLgiLCJleHAiOjE3NDY2MTU5MjIsImlhdCI6MTc0NjYwODcyMiwicGljdHVyZSI6Imh0dHA6Ly9rLmtha2FvY2RuLm5ldC9kbi9iYkFSMFcvYnRzTGExbHlMdXMvM3JXSG9pd2s3Y3BFYWVrWER3Rkg1MC9pbWdfMTEweDExMC5qcGcifQ.BsoakXYnBSIRWAOYtoFEflGRjTkwzNQc8vy8VbR1O4a8B3yHSG4pNy1ZIj2N34UokTWHY1UVmR9FHArvelt-BQadIH-QAs4r8noaUApQaQxAmQtg40qbSC2NYqzD52KWzPCIfrBgtsuzJcD51N3j8wYrSeAMM40r3TSYZgS6irwkhuYFssk7yE9yrAdXa6Gg3DMQhUo1YrSy21Rv8UnRFGKVPKz-TtBCKdkYKboj5PUpChtrZHABtuZRMo81iyhnXMLtxQ5Edoj58jZQjXelzf34h6HAdW0IBbSCCn5lrQ6zCZrJ3B0GBj9hNXat-I3EE5giiHCgGz3zbWngH8ub8w",
                deviceId = "web"
            )
        )
    }

    fun create(request: SocialLoginRequest): LoginResponse? {
        return restClient.post()
            .uri("/v1/auth/login")
            .body(request)
            .retrieve()
            .body(LoginResponse::class.java)
    }
}