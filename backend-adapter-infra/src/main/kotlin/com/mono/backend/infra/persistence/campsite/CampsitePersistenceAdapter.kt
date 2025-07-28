package com.mono.backend.infra.persistence.campsite

import com.mono.backend.domain.campsite.Campsite
import com.mono.backend.port.infra.campsite.persistence.CampsitePersistencePort
import org.springframework.stereotype.Repository

@Repository
class CampsitePersistenceAdapter(
    private val campsiteRepository: CampsiteRepository
) : CampsitePersistencePort {

    override suspend fun save(campsite: Campsite): Campsite {
        val entity = CampsiteEntity.from(campsite)
        campsiteRepository.saveWithGeometry(entity)
        return entity.toDomain()
    }

    override suspend fun saveAll(campsites: List<Campsite>): List<Campsite> {
        val entities = campsites.map { CampsiteEntity.from(it) }
        // 각 엔티티를 개별적으로 저장 (ST_GeomFromText 사용)
        entities.forEach { entity ->
            campsiteRepository.saveWithGeometry(entity)
        }
        return entities.map { it.toDomain() }
    }

    override suspend fun findById(campsiteId: Long): Campsite? {
        return campsiteRepository.findById(campsiteId)?.toDomain()
    }

    override suspend fun findAll(limit: Int, offset: Int): List<Campsite> {
        return campsiteRepository.findAllWithPaging(limit, offset).map { it.toDomain() }
    }

    override suspend fun findByProvince(province: String, limit: Int, offset: Int): List<Campsite> {
        return campsiteRepository.findByProvince(province, limit, offset).map { it.toDomain() }
    }

    override suspend fun findByCity(province: String, city: String, limit: Int, offset: Int): List<Campsite> {
        return campsiteRepository.findByProvinceAndCity(province, city, limit, offset).map { it.toDomain() }
    }

    override suspend fun findByName(name: String, limit: Int, offset: Int): List<Campsite> {
        return campsiteRepository.findByNameContaining(name, limit, offset).map { it.toDomain() }
    }

    override suspend fun count(): Long {
        return campsiteRepository.countAll()
    }

    override suspend fun countByProvince(province: String): Long {
        return campsiteRepository.countByProvince(province)
    }

    override suspend fun countByCity(province: String, city: String): Long {
        return campsiteRepository.countByProvinceAndCity(province, city)
    }

    override suspend fun deleteAll() {
        campsiteRepository.deleteAll()
    }

    override suspend fun existsByName(name: String): Boolean {
        return campsiteRepository.existsByName(name)
    }

    override suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double,
        limit: Int,
        offset: Int
    ): List<Campsite> {
        return campsiteRepository.findWithinRadius(latitude, longitude, radiusInMeters, limit, offset)
            .map { it.toDomain() }
    }

    override suspend fun countWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double
    ): Long {
        return campsiteRepository.countWithinRadius(latitude, longitude, radiusInMeters)
    }

    override suspend fun findNearestCampsites(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<Campsite> {
        return campsiteRepository.findNearestCampsites(latitude, longitude, limit)
            .map { it.toDomain() }
    }

    override suspend fun findWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double,
        limit: Int,
        offset: Int
    ): List<Campsite> {
        return campsiteRepository.findWithinBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude, limit, offset
        ).map { it.toDomain() }
    }

    override suspend fun countWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double
    ): Long {
        return campsiteRepository.countWithinBounds(minLatitude, maxLatitude, minLongitude, maxLongitude)
    }
} 