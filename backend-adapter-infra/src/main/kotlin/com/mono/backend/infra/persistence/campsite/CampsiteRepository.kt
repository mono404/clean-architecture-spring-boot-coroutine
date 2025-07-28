package com.mono.backend.infra.persistence.campsite

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CampsiteRepository : CoroutineCrudRepository<CampsiteEntity, Long> {

    @Query("SELECT * FROM campsite WHERE province = :province ORDER BY campsite_id LIMIT :limit OFFSET :offset")
    suspend fun findByProvince(province: String, limit: Int, offset: Int): List<CampsiteEntity>

    @Query("SELECT * FROM campsite WHERE province = :province AND city = :city ORDER BY campsite_id LIMIT :limit OFFSET :offset")
    suspend fun findByProvinceAndCity(province: String, city: String, limit: Int, offset: Int): List<CampsiteEntity>

    @Query("SELECT * FROM campsite WHERE name LIKE CONCAT('%', :name, '%') ORDER BY campsite_id LIMIT :limit OFFSET :offset")
    suspend fun findByNameContaining(name: String, limit: Int, offset: Int): List<CampsiteEntity>

    @Query("SELECT * FROM campsite ORDER BY campsite_id LIMIT :limit OFFSET :offset")
    suspend fun findAllWithPaging(limit: Int, offset: Int): List<CampsiteEntity>

    @Query("SELECT COUNT(*) FROM campsite")
    suspend fun countAll(): Long

    @Query("SELECT COUNT(*) FROM campsite WHERE province = :province")
    suspend fun countByProvince(province: String): Long

    @Query("SELECT COUNT(*) FROM campsite WHERE province = :province AND city = :city")
    suspend fun countByProvinceAndCity(province: String, city: String): Long

    suspend fun existsByName(name: String): Boolean

    // 커스텀 save 메서드 (ST_GeomFromText 사용)
    @Query(
        """
        INSERT INTO campsite (
            campsite_id, name, management_type, province, city, address, road_address, zip_code, 
            phone, homepage, location, general_camping, auto_camping, glamping, caravan, 
            personal_caravan, site_size1_width, site_size1_height, site_size1_count,
            site_size2_width, site_size2_height, site_size2_count, site_size3_width,
            site_size3_height, site_size3_count, weekday_open, weekend_open, spring_open,
            summer_open, fall_open, winter_open, has_electricity, has_hot_water, has_wifi,
            has_firewood, has_walking_trail, has_water_play, has_playground, has_mart,
            has_firepit, has_dump_station, toilet_count, shower_count, sink_count,
            fire_extinguisher_count, fire_water_count, fire_sand_count, smoke_detector_count,
            nearby_fishing, nearby_walking_trail, nearby_beach, nearby_water_sports,
            nearby_valley, nearby_river, nearby_pool, nearby_youth_facility,
            nearby_rural_experience, nearby_kids_playground, glamping_bed, glamping_tv,
            glamping_fridge, glamping_internet, glamping_toilet, glamping_aircon,
            glamping_heater, glamping_cooking, theme, equipment_rental, pet_allowed,
            facilities, nearby_facilities, features, introduction, license_date
        ) VALUES (
            :#{#entity.campsiteId}, :#{#entity.name}, :#{#entity.managementType}, 
            :#{#entity.province}, :#{#entity.city}, :#{#entity.address}, :#{#entity.roadAddress}, 
            :#{#entity.zipCode}, :#{#entity.phone}, :#{#entity.homepage}, 
            ST_GeomFromText(:#{#entity.location}), :#{#entity.generalCamping}, 
            :#{#entity.autoCamping}, :#{#entity.glamping}, :#{#entity.caravan}, 
            :#{#entity.personalCaravan}, :#{#entity.siteSize1Width}, :#{#entity.siteSize1Height}, 
            :#{#entity.siteSize1Count}, :#{#entity.siteSize2Width}, :#{#entity.siteSize2Height}, 
            :#{#entity.siteSize2Count}, :#{#entity.siteSize3Width}, :#{#entity.siteSize3Height}, 
            :#{#entity.siteSize3Count}, :#{#entity.weekdayOpen}, :#{#entity.weekendOpen}, 
            :#{#entity.springOpen}, :#{#entity.summerOpen}, :#{#entity.fallOpen}, 
            :#{#entity.winterOpen}, :#{#entity.hasElectricity}, :#{#entity.hasHotWater}, 
            :#{#entity.hasWifi}, :#{#entity.hasFirewood}, :#{#entity.hasWalkingTrail}, 
            :#{#entity.hasWaterPlay}, :#{#entity.hasPlayground}, :#{#entity.hasMart}, 
            :#{#entity.hasFirepit}, :#{#entity.hasDumpStation}, :#{#entity.toiletCount}, 
            :#{#entity.showerCount}, :#{#entity.sinkCount}, :#{#entity.fireExtinguisherCount}, 
            :#{#entity.fireWaterCount}, :#{#entity.fireSandCount}, :#{#entity.smokeDetectorCount}, 
            :#{#entity.nearbyFishing}, :#{#entity.nearbyWalkingTrail}, :#{#entity.nearbyBeach}, 
            :#{#entity.nearbyWaterSports}, :#{#entity.nearbyValley}, :#{#entity.nearbyRiver}, 
            :#{#entity.nearbyPool}, :#{#entity.nearbyYouthFacility}, :#{#entity.nearbyRuralExperience}, 
            :#{#entity.nearbyKidsPlayground}, :#{#entity.glampingBed}, :#{#entity.glampingTv}, 
            :#{#entity.glampingFridge}, :#{#entity.glampingInternet}, :#{#entity.glampingToilet}, 
            :#{#entity.glampingAircon}, :#{#entity.glampingHeater}, :#{#entity.glampingCooking}, 
            :#{#entity.theme}, :#{#entity.equipmentRental}, :#{#entity.petAllowed}, 
            :#{#entity.facilities}, :#{#entity.nearbyFacilities}, :#{#entity.features}, 
            :#{#entity.introduction}, :#{#entity.licenseDate}
        )
    """
    )
    suspend fun saveWithGeometry(entity: CampsiteEntity): Long

    // Spatial 쿼리 메서드들
    @Query(
        """
        SELECT * FROM campsite 
        WHERE ST_Distance_Sphere(location, POINT(:longitude, :latitude)) <= :radiusInMeters
        ORDER BY ST_Distance_Sphere(location, POINT(:longitude, :latitude))
        LIMIT :limit OFFSET :offset
    """
    )
    suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double,
        limit: Int,
        offset: Int
    ): List<CampsiteEntity>

    @Query(
        """
        SELECT COUNT(*) FROM campsite 
        WHERE ST_Distance_Sphere(location, POINT(:longitude, :latitude)) <= :radiusInMeters
    """
    )
    suspend fun countWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double
    ): Long

    @Query(
        """
        SELECT * FROM campsite 
        ORDER BY ST_Distance_Sphere(location, POINT(:longitude, :latitude))
        LIMIT :limit
    """
    )
    suspend fun findNearestCampsites(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<CampsiteEntity>

    @Query(
        """
        SELECT * FROM campsite 
        WHERE ST_Within(location, ST_GeomFromText('POLYGON((:minLng :minLat, :maxLng :minLat, :maxLng :maxLat, :minLng :maxLat, :minLng :minLat))'))
        ORDER BY campsite_id
        LIMIT :limit OFFSET :offset
    """
    )
    suspend fun findWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double,
        limit: Int,
        offset: Int
    ): List<CampsiteEntity>

    @Query(
        """
        SELECT COUNT(*) FROM campsite 
        WHERE ST_Within(location, ST_GeomFromText('POLYGON((:minLng :minLat, :maxLng :minLat, :maxLng :maxLat, :minLng :maxLat, :minLng :minLat))'))
    """
    )
    suspend fun countWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double
    ): Long
} 