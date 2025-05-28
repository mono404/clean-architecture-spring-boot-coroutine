package com.mono.backend.persistence.member

import com.mono.backend.member.SocialProvider
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: CoroutineCrudRepository<MemberEntity, Long> {
    suspend fun findByProviderAndProviderId(provider: SocialProvider, providerId: String): MemberEntity?
    suspend fun existsByNickname(nickname: String): Boolean
}

//@Component
//class MemberRepository {
//    private val members: MutableMap<Long, MemberEntity> = mutableMapOf()
//
//    fun save(memberEntity: MemberEntity) : MemberEntity {
//        members[memberEntity.memberId] = memberEntity
//        return members[memberEntity.memberId]!!
//    }
//
//    fun findById(memberId: Long): MemberEntity? {
//        return members[memberId]
//    }
//
//    fun findByProviderAndProviderId(provider: SocialProvider, providerId: String): MemberEntity? {
//        val map = members.filter { it.value.provider == provider && it.value.providerId == providerId }
//        return map.values.firstOrNull()
//    }
//}