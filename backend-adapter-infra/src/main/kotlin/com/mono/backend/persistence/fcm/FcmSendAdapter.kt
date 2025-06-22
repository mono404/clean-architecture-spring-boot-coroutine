package com.mono.backend.persistence.fcm

import com.google.api.core.ApiFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.*
import com.mono.backend.fcm.FcmMessage
import com.mono.backend.log.logger
import kotlinx.coroutines.suspendCancellableCoroutine
import org.springframework.stereotype.Component
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Component
class FcmSendAdapter(
    private val firebaseApp: FirebaseApp
) : FcmSendPort {
    private val messaging = FirebaseMessaging.getInstance(firebaseApp)
    private val log = logger()

    override suspend fun sendToDevice(token: String, message: FcmMessage): String {
        val notification = Notification.builder()
            .setTitle(message.title)
            .setBody(message.body)
            .build()

        val fcmMessage = Message.builder()
            .setToken(token)
            .setNotification(notification)
            .putAllData(message.data)
//            .setAndroidConfig(
//                AndroidConfig.builder()
//                    .setPriority(AndroidConfig.Priority.HIGH)
//                    .setNotification(
//                        AndroidNotification.builder()
//                            .setChannelId("high_importance_channel")
//                            .setSound("default")
//                            .build()
//                    )
//                    .build()
//            )
//            // 5) iOS(APNs) 전용 옵션 (배지·사운드)
//            .setApnsConfig(
//                ApnsConfig.builder()
//                    .setAps(
//                        Aps.builder()
//                            .setBadge(1)
//                            .setSound("default")
//                            .build()
//                    )
//                    .build()
//            )
            .build()

        return messaging.sendAsync(fcmMessage).await()
    }

    override suspend fun sendToDevices(tokens: List<String>, message: FcmMessage): List<String> {
        if (tokens.isEmpty()) return emptyList()

        val allMessageIds = mutableListOf<String>()
        // FCM 멀티캐스트는 최대 500개 토큰까지 지원

        val notification = Notification.builder()
            .setTitle(message.title)
            .setBody(message.body)
            .build()
        tokens.chunked(500).forEach { batch ->
            val multicast = MulticastMessage.builder()
                .addAllTokens(batch)
                .setNotification(notification)
                .putAllData(message.data)
                .build()

            val response = messaging
                .sendEachForMulticastAsync(multicast)
                .await()

            // 성공한 메시지 ID만 수집
            allMessageIds += response.responses
                .mapNotNull { it.messageId }

            // (선택) 실패 토큰 로그
            response.responses
                .filterNot { it.isSuccessful }
                .forEachIndexed { idx, resp ->
                    val err = resp.exception ?: Exception("Unknown error")
                    log.error("FCM multicast failure [batch ${batch[idx]}]: ${err.message}")
                }
        }

        return allMessageIds
    }

    override suspend fun sendToTopic(topic: String, message: FcmMessage): String {
        val notification = Notification.builder()
            .setTitle(message.title)
            .setBody(message.body)
            .build()

        val fcmMessage = Message.builder()
            .setTopic(topic)
            .setNotification(notification)
            .putAllData(message.data)
            .build()

        return messaging.sendAsync(fcmMessage).await()
    }

    /**
     * ApiFuture<T>를 코루틴에서 논블로킹으로 기다리게 해 주는 확장함수
     */
    suspend fun <T> ApiFuture<T>.await(): T =
        suspendCancellableCoroutine { cont ->
            // ApiFuture는 Guava ListenableFuture를 상속하므로 addListener 사용 가능
            addListener({
                try {
                    cont.resume(get())
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            }, MoreExecutors.directExecutor())

            // 코루틴이 취소되면 Future도 취소
            cont.invokeOnCancellation { this.cancel(true) }
        }
}