package com.mono.backend.infra.persistence.searchindex

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.domain.search.DomainType
import com.mono.backend.domain.search.SearchIndex
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "search_index")
data class SearchIndexEntity(
    @Id
    val searchIndexId: Long,
    val domainId: Long,
    val boardId: Long,
    val title: String? = null,
    val content: String? = null,
    val comment: String? = null,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,

    val memberId: Long,
    val nickname: String,
    val profileImageUrl: String?,
) : Persistable<Long> {
    override fun getId(): Long = searchIndexId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain() = SearchIndex(
        searchIndexId = searchIndexId,
        domainType = DomainType.fromId(domainId),
        boardType = BoardType.fromId(boardId),
        title = title,
        content = content,
        comment = comment,
        createdAt = createdAt,
        updatedAt = updatedAt,

        member = EmbeddedMember(
            memberId = memberId,
            nickname = nickname,
            profileImageUrl = profileImageUrl
        )
    )

    companion object {
        fun from(searchIndex: SearchIndex): SearchIndexEntity = SearchIndexEntity(
            searchIndexId = searchIndex.searchIndexId,
            domainId = searchIndex.domainType.id,
            boardId = searchIndex.boardType.id,
            title = searchIndex.title,
            content = searchIndex.content,
            comment = searchIndex.comment,
            createdAt = searchIndex.createdAt,
            updatedAt = searchIndex.updatedAt,

            memberId = searchIndex.member.memberId,
            nickname = searchIndex.member.nickname,
            profileImageUrl = searchIndex.member.profileImageUrl,
        )
    }
}