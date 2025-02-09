package com.mono.backend.repository.post

import com.mono.backend.post.Post
import com.mono.backend.entity.post.PostEntity
import com.mono.backend.entity.post.toPost
import com.mono.backend.post.PostPersistencePort
import org.springframework.stereotype.Repository

@Repository
class PostRepository : PostPersistencePort {
    private var index = 0
    private val postDB = mutableListOf<PostEntity>()

    override suspend fun save(post: Post): Int {
        val postEntity = PostEntity(index++, post.title, post.content)
        postDB.add(postEntity)
        println("save post to db... id: ${postEntity.id}")
        return postEntity.id
    }

    override fun findAll(): List<Post> {
        return postDB.map { it.toPost() }
    }

    override fun findById(id: Int): Post {
        return postDB.first { it.id == id }.toPost()
    }

    override fun update(id: Int, post: Post): Int {
        postDB[id] = PostEntity(id, post.title, post.content)
        return id
    }

    override fun deleteById(id: Int) {
        postDB.removeIf { it.id == id }
    }
}