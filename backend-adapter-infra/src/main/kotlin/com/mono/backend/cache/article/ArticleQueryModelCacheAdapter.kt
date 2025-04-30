package com.mono.backend.cache.article

import com.mono.backend.article.ArticleQueryModel
import com.mono.backend.dataserializer.DataSerializer
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleQueryModelCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): ArticleQueryModelCachePort {
    companion object {
        const val KEY_FORMAT = "article-read::article::%s"
    }

    override suspend fun create(articleQueryModel: ArticleQueryModel, ttl: Duration) {
        val value = DataSerializer.serialize(articleQueryModel) ?: return
        redisTemplate.opsForValue().set(generateKey(articleQueryModel), value, ttl).awaitSingle()
    }

    override suspend fun update(articleQueryModel: ArticleQueryModel) {
        val value = DataSerializer.serialize(articleQueryModel) ?: return
        redisTemplate.opsForValue().setIfPresent(generateKey(articleQueryModel), value).awaitSingle()
    }

    override suspend fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId)).awaitSingle()
    }

    override suspend fun read(articleId: Long): ArticleQueryModel? {
        return redisTemplate.opsForValue().get(generateKey(articleId)).awaitSingleOrNull()
            ?.let { json -> DataSerializer.deserialize(json, ArticleQueryModel::class.java) }
    }

    private fun generateKey(articleQueryModel: ArticleQueryModel): String {
        return generateKey(articleQueryModel.articleId)
    }

    private fun generateKey(articleId: Long): String {
        return String.format(KEY_FORMAT, articleId)
    }

    override suspend fun readAll(articleIds: List<Long>): List<ArticleQueryModel>? {
        val keyList = articleIds.map(this::generateKey)
        return redisTemplate.opsForValue().multiGet(keyList).awaitSingleOrNull()
            ?.mapNotNull { json -> DataSerializer.deserialize(json, ArticleQueryModel::class.java) }
    }
}