package com.mono.backend.port.web.campsite.dto

import com.mono.backend.domain.campsite.Campsite
import java.time.LocalDateTime

data class CampsiteResponse(
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
    val latitude: Double?,
    val longitude: Double?,

    // 사이트 정보
    val generalCamping: Boolean,
    val autoCamping: Boolean,
    val glamping: Boolean,
    val caravan: Boolean,
    val personalCaravan: Boolean,

    // 운영 정보
    val weekdayOpen: Boolean,
    val weekendOpen: Boolean,
    val springOpen: Boolean,
    val summerOpen: Boolean,
    val fallOpen: Boolean,
    val winterOpen: Boolean,

    // 부대시설
    val hasElectricity: Boolean,
    val hasHotWater: Boolean,
    val hasWifi: Boolean,
    val hasFirewood: Boolean,
    val hasWalkingTrail: Boolean,
    val hasWaterPlay: Boolean,
    val hasPlayground: Boolean,
    val hasMart: Boolean,
    val hasFirepit: Boolean,
    val hasDumpStation: Boolean,

    // 시설 개수
    val toiletCount: Int?,
    val showerCount: Int?,
    val sinkCount: Int?,
    val fireExtinguisherCount: Int?,

    // 주변 시설
    val nearbyFishing: Boolean,
    val nearbyBeach: Boolean,
    val nearbyWaterSports: Boolean,
    val nearbyValley: Boolean,
    val nearbyRiver: Boolean,
    val nearbyPool: Boolean,

    // 글램핑 시설
    val glampingBed: Boolean,
    val glampingTv: Boolean,
    val glampingFridge: Boolean,
    val glampingInternet: Boolean,
    val glampingToilet: Boolean,
    val glampingAircon: Boolean,
    val glampingHeater: Boolean,
    val glampingCooking: Boolean,

    // 추가 정보
    val theme: String?,
    val equipmentRental: String?,
    val petAllowed: Boolean,
    val facilities: String?,
    val features: String?,
    val introduction: String?,

    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    companion object {
        fun from(campsite: Campsite): CampsiteResponse {
            return CampsiteResponse(
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
                latitude = campsite.latitude,
                longitude = campsite.longitude,
                generalCamping = campsite.generalCamping,
                autoCamping = campsite.autoCamping,
                glamping = campsite.glamping,
                caravan = campsite.caravan,
                personalCaravan = campsite.personalCaravan,
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
                nearbyFishing = campsite.nearbyFishing,
                nearbyBeach = campsite.nearbyBeach,
                nearbyWaterSports = campsite.nearbyWaterSports,
                nearbyValley = campsite.nearbyValley,
                nearbyRiver = campsite.nearbyRiver,
                nearbyPool = campsite.nearbyPool,
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
                features = campsite.features,
                introduction = campsite.introduction,
                createdAt = campsite.createdAt,
                updatedAt = campsite.updatedAt,
            )
        }
    }
}

data class CsvImportResponse(
    val success: Boolean,
    val totalParsed: Int,
    val totalSaved: Int,
    val message: String
)

data class CampsiteListResponse(
    val campsites: List<CampsiteResponse>,
    val totalCount: Long? = null,
    val currentPage: Int? = null,
    val pageSize: Int? = null
)

data class CampsiteStatisticsResponse(
    val totalCampsites: Long
)

data class CampsiteWithDistanceResponse(
    val campsite: CampsiteResponse,
    val distanceInKm: Double? = null
) {
    companion object {
        fun from(campsite: Campsite, distanceInMeters: Double? = null): CampsiteWithDistanceResponse {
            return CampsiteWithDistanceResponse(
                campsite = CampsiteResponse.from(campsite),
                distanceInKm = distanceInMeters?.div(1000.0) // 미터를 킬로미터로 변환
            )
        }
    }
} 