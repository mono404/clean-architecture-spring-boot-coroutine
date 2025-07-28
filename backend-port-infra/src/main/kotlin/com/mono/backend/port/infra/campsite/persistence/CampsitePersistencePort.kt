package com.mono.backend.port.infra.campsite.persistence

import com.mono.backend.domain.campsite.Campsite

interface CampsitePersistencePort {
    suspend fun save(campsite: Campsite): Campsite
    suspend fun saveAll(campsites: List<Campsite>): List<Campsite>
    suspend fun findById(campsiteId: Long): Campsite?
    suspend fun findAll(limit: Int, offset: Int): List<Campsite>
    suspend fun findByProvince(province: String, limit: Int, offset: Int): List<Campsite>
    suspend fun findByCity(province: String, city: String, limit: Int, offset: Int): List<Campsite>
    suspend fun findByName(name: String, limit: Int, offset: Int): List<Campsite>
    suspend fun count(): Long
    suspend fun countByProvince(province: String): Long
    suspend fun countByCity(province: String, city: String): Long
    suspend fun deleteAll()
    suspend fun existsByName(name: String): Boolean

    // Spatial 검색 메서드들
    suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double,
        limit: Int,
        offset: Int
    ): List<Campsite>

    suspend fun countWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double
    ): Long

    suspend fun findNearestCampsites(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<Campsite>

    suspend fun findWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double,
        limit: Int,
        offset: Int
    ): List<Campsite>

    suspend fun countWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double
    ): Long
} 