package com.mono.backend.infra.persistence.campsite

import com.mono.backend.domain.campsite.Campsite
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "campsite")
data class CampsiteEntity(
    @Id
    val campsiteId: Long,
    val name: String,
    val managementType: String?,
    val province: String?,
    val city: String?,
    val address: String?,
    val roadAddress: String?,
    val zipCode: String?,
    val phone: String?,
    val homepage: String?,

    // 위치 정보 (POINT 타입으로 통합)
    val location: String, // WKT 형식: "POINT(longitude latitude)"

    // 사이트 정보
    val generalCamping: Boolean = false,
    val autoCamping: Boolean = false,
    val glamping: Boolean = false,
    val caravan: Boolean = false,
    val personalCaravan: Boolean = false,

    // 사이트 크기 및 수량
    val siteSize1Width: Int? = null,
    val siteSize1Height: Int? = null,
    val siteSize1Count: Int? = null,
    val siteSize2Width: Int? = null,
    val siteSize2Height: Int? = null,
    val siteSize2Count: Int? = null,
    val siteSize3Width: Int? = null,
    val siteSize3Height: Int? = null,
    val siteSize3Count: Int? = null,

    // 운영 정보
    val weekdayOpen: Boolean = false,
    val weekendOpen: Boolean = false,
    val springOpen: Boolean = false,
    val summerOpen: Boolean = false,
    val fallOpen: Boolean = false,
    val winterOpen: Boolean = false,

    // 부대시설
    val hasElectricity: Boolean = false,
    val hasHotWater: Boolean = false,
    val hasWifi: Boolean = false,
    val hasFirewood: Boolean = false,
    val hasWalkingTrail: Boolean = false,
    val hasWaterPlay: Boolean = false,
    val hasPlayground: Boolean = false,
    val hasMart: Boolean = false,
    val hasFirepit: Boolean = false,
    val hasDumpStation: Boolean = false,

    // 시설 개수
    val toiletCount: Int? = null,
    val showerCount: Int? = null,
    val sinkCount: Int? = null,
    val fireExtinguisherCount: Int? = null,
    val fireWaterCount: Int? = null,
    val fireSandCount: Int? = null,
    val smokeDetectorCount: Int? = null,

    // 주변 시설
    val nearbyFishing: Boolean = false,
    val nearbyWalkingTrail: Boolean = false,
    val nearbyBeach: Boolean = false,
    val nearbyWaterSports: Boolean = false,
    val nearbyValley: Boolean = false,
    val nearbyRiver: Boolean = false,
    val nearbyPool: Boolean = false,
    val nearbyYouthFacility: Boolean = false,
    val nearbyRuralExperience: Boolean = false,
    val nearbyKidsPlayground: Boolean = false,

    // 글램핑 시설
    val glampingBed: Boolean = false,
    val glampingTv: Boolean = false,
    val glampingFridge: Boolean = false,
    val glampingInternet: Boolean = false,
    val glampingToilet: Boolean = false,
    val glampingAircon: Boolean = false,
    val glampingHeater: Boolean = false,
    val glampingCooking: Boolean = false,

    // 추가 정보
    val theme: String?,
    val equipmentRental: String?,
    val petAllowed: Boolean = false,
    val facilities: String?,
    val nearbyFacilities: String?,
    val features: String?,
    val introduction: String?,
    val licenseDate: String?,

    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = campsiteId
    override fun isNew(): Boolean = createdAt == null

    fun toDomain(): Campsite {
        val (latitude, longitude) = parseLocationToCoordinates(location)

        return Campsite(
            campsiteId = campsiteId,
            name = name,
            managementType = managementType,
            province = province,
            city = city,
            address = address,
            roadAddress = roadAddress,
            zipCode = zipCode,
            phone = phone,
            homepage = homepage,
            latitude = latitude,
            longitude = longitude,
            generalCamping = generalCamping,
            autoCamping = autoCamping,
            glamping = glamping,
            caravan = caravan,
            personalCaravan = personalCaravan,
            siteSize1Width = siteSize1Width,
            siteSize1Height = siteSize1Height,
            siteSize1Count = siteSize1Count,
            siteSize2Width = siteSize2Width,
            siteSize2Height = siteSize2Height,
            siteSize2Count = siteSize2Count,
            siteSize3Width = siteSize3Width,
            siteSize3Height = siteSize3Height,
            siteSize3Count = siteSize3Count,
            weekdayOpen = weekdayOpen,
            weekendOpen = weekendOpen,
            springOpen = springOpen,
            summerOpen = summerOpen,
            fallOpen = fallOpen,
            winterOpen = winterOpen,
            hasElectricity = hasElectricity,
            hasHotWater = hasHotWater,
            hasWifi = hasWifi,
            hasFirewood = hasFirewood,
            hasWalkingTrail = hasWalkingTrail,
            hasWaterPlay = hasWaterPlay,
            hasPlayground = hasPlayground,
            hasMart = hasMart,
            hasFirepit = hasFirepit,
            hasDumpStation = hasDumpStation,
            toiletCount = toiletCount,
            showerCount = showerCount,
            sinkCount = sinkCount,
            fireExtinguisherCount = fireExtinguisherCount,
            fireWaterCount = fireWaterCount,
            fireSandCount = fireSandCount,
            smokeDetectorCount = smokeDetectorCount,
            nearbyFishing = nearbyFishing,
            nearbyWalkingTrail = nearbyWalkingTrail,
            nearbyBeach = nearbyBeach,
            nearbyWaterSports = nearbyWaterSports,
            nearbyValley = nearbyValley,
            nearbyRiver = nearbyRiver,
            nearbyPool = nearbyPool,
            nearbyYouthFacility = nearbyYouthFacility,
            nearbyRuralExperience = nearbyRuralExperience,
            nearbyKidsPlayground = nearbyKidsPlayground,
            glampingBed = glampingBed,
            glampingTv = glampingTv,
            glampingFridge = glampingFridge,
            glampingInternet = glampingInternet,
            glampingToilet = glampingToilet,
            glampingAircon = glampingAircon,
            glampingHeater = glampingHeater,
            glampingCooking = glampingCooking,
            theme = theme,
            equipmentRental = equipmentRental,
            petAllowed = petAllowed,
            facilities = facilities,
            nearbyFacilities = nearbyFacilities,
            features = features,
            introduction = introduction,
            licenseDate = licenseDate,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    private fun parseLocationToCoordinates(locationWkt: String): Pair<Double?, Double?> {
        return try {
            // "POINT(longitude latitude)" 형식 파싱
            val coordsString = locationWkt.substringAfter("POINT(").substringBefore(")")
            val coords = coordsString.split(" ")
            if (coords.size == 2) {
                val longitude = coords[0].toDoubleOrNull()
                val latitude = coords[1].toDoubleOrNull()
                Pair(latitude, longitude)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Pair(null, null)
        }
    }

    companion object {
        fun from(campsite: Campsite): CampsiteEntity {
            val location = createLocationWkt(campsite.latitude, campsite.longitude)
                ?: throw IllegalArgumentException("캠핑장은 유효한 위치 정보가 필요합니다.")

            return CampsiteEntity(
                campsiteId = campsite.campsiteId,
                name = campsite.name,
                managementType = campsite.managementType,
                province = campsite.province,
                city = campsite.city,
                address = campsite.address,
                roadAddress = campsite.roadAddress,
                zipCode = campsite.zipCode,
                phone = campsite.phone,
                homepage = campsite.homepage,
                location = location,
                generalCamping = campsite.generalCamping,
                autoCamping = campsite.autoCamping,
                glamping = campsite.glamping,
                caravan = campsite.caravan,
                personalCaravan = campsite.personalCaravan,
                siteSize1Width = campsite.siteSize1Width,
                siteSize1Height = campsite.siteSize1Height,
                siteSize1Count = campsite.siteSize1Count,
                siteSize2Width = campsite.siteSize2Width,
                siteSize2Height = campsite.siteSize2Height,
                siteSize2Count = campsite.siteSize2Count,
                siteSize3Width = campsite.siteSize3Width,
                siteSize3Height = campsite.siteSize3Height,
                siteSize3Count = campsite.siteSize3Count,
                weekdayOpen = campsite.weekdayOpen,
                weekendOpen = campsite.weekendOpen,
                springOpen = campsite.springOpen,
                summerOpen = campsite.summerOpen,
                fallOpen = campsite.fallOpen,
                winterOpen = campsite.winterOpen,
                hasElectricity = campsite.hasElectricity,
                hasHotWater = campsite.hasHotWater,
                hasWifi = campsite.hasWifi,
                hasFirewood = campsite.hasFirewood,
                hasWalkingTrail = campsite.hasWalkingTrail,
                hasWaterPlay = campsite.hasWaterPlay,
                hasPlayground = campsite.hasPlayground,
                hasMart = campsite.hasMart,
                hasFirepit = campsite.hasFirepit,
                hasDumpStation = campsite.hasDumpStation,
                toiletCount = campsite.toiletCount,
                showerCount = campsite.showerCount,
                sinkCount = campsite.sinkCount,
                fireExtinguisherCount = campsite.fireExtinguisherCount,
                fireWaterCount = campsite.fireWaterCount,
                fireSandCount = campsite.fireSandCount,
                smokeDetectorCount = campsite.smokeDetectorCount,
                nearbyFishing = campsite.nearbyFishing,
                nearbyWalkingTrail = campsite.nearbyWalkingTrail,
                nearbyBeach = campsite.nearbyBeach,
                nearbyWaterSports = campsite.nearbyWaterSports,
                nearbyValley = campsite.nearbyValley,
                nearbyRiver = campsite.nearbyRiver,
                nearbyPool = campsite.nearbyPool,
                nearbyYouthFacility = campsite.nearbyYouthFacility,
                nearbyRuralExperience = campsite.nearbyRuralExperience,
                nearbyKidsPlayground = campsite.nearbyKidsPlayground,
                glampingBed = campsite.glampingBed,
                glampingTv = campsite.glampingTv,
                glampingFridge = campsite.glampingFridge,
                glampingInternet = campsite.glampingInternet,
                glampingToilet = campsite.glampingToilet,
                glampingAircon = campsite.glampingAircon,
                glampingHeater = campsite.glampingHeater,
                glampingCooking = campsite.glampingCooking,
                theme = campsite.theme,
                equipmentRental = campsite.equipmentRental,
                petAllowed = campsite.petAllowed,
                facilities = campsite.facilities,
                nearbyFacilities = campsite.nearbyFacilities,
                features = campsite.features,
                introduction = campsite.introduction,
                licenseDate = campsite.licenseDate,
                createdAt = campsite.createdAt,
                updatedAt = campsite.updatedAt,
            )
        }

        private fun createLocationWkt(latitude: Double?, longitude: Double?): String? {
            return if (latitude != null && longitude != null) {
                "POINT($longitude $latitude)"
            } else {
                null
            }
        }
    }
} 