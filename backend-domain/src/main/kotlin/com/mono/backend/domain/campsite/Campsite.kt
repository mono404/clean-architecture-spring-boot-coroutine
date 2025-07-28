package com.mono.backend.domain.campsite

import java.time.LocalDateTime

data class Campsite(
    val campsiteId: Long,
    val name: String,
    val managementType: String?, // 사업주체 구분
    val province: String?, // 도/시도명
    val city: String?, // 시군구
    val address: String?, // 주소
    val roadAddress: String?, // 도로명주소 
    val zipCode: String?, // 우편번호
    val phone: String?, // 연락처 (조합된 형태)
    val homepage: String?, // 홈페이지

    // 위치 정보
    val latitude: Double?, // 위도
    val longitude: Double?, // 경도

    // 사이트 정보
    val generalCamping: Boolean = false, // 일반야영장
    val autoCamping: Boolean = false, // 자동차야영장
    val glamping: Boolean = false, // 글램핑
    val caravan: Boolean = false, // 카라반
    val personalCaravan: Boolean = false, // 개인 카라반

    // 사이트 크기 및 수량
    val siteSize1Width: Int? = null, // 사이트 크기1 가로
    val siteSize1Height: Int? = null, // 사이트 크기1 세로
    val siteSize1Count: Int? = null, // 사이트 크기1 수량
    val siteSize2Width: Int? = null, // 사이트 크기2 가로
    val siteSize2Height: Int? = null, // 사이트 크기2 세로 
    val siteSize2Count: Int? = null, // 사이트 크기2 수량
    val siteSize3Width: Int? = null, // 사이트 크기3 가로
    val siteSize3Height: Int? = null, // 사이트 크기3 세로
    val siteSize3Count: Int? = null, // 사이트 크기3 수량

    // 운영 정보
    val weekdayOpen: Boolean = false, // 평일 운영 여부
    val weekendOpen: Boolean = false, // 주말 운영 여부
    val springOpen: Boolean = false, // 봄 운영 여부
    val summerOpen: Boolean = false, // 여름 운영 여부
    val fallOpen: Boolean = false, // 가을 운영 여부
    val winterOpen: Boolean = false, // 겨울 운영 여부

    // 부대시설
    val hasElectricity: Boolean = false, // 전기
    val hasHotWater: Boolean = false, // 온수
    val hasWifi: Boolean = false, // 무선인터넷
    val hasFirewood: Boolean = false, // 장작판매
    val hasWalkingTrail: Boolean = false, // 산책로
    val hasWaterPlay: Boolean = false, // 물놀이장
    val hasPlayground: Boolean = false, // 놀이터
    val hasMart: Boolean = false, // 마트
    val hasFirepit: Boolean = false, // 화로대
    val hasDumpStation: Boolean = false, // 덤프스테이션

    // 시설 개수
    val toiletCount: Int? = null, // 화장실 수
    val showerCount: Int? = null, // 샤워실 수
    val sinkCount: Int? = null, // 씽크대 수
    val fireExtinguisherCount: Int? = null, // 소화기 수
    val fireWaterCount: Int? = null, // 방화수 개수
    val fireSandCount: Int? = null, // 방화사 개수
    val smokeDetectorCount: Int? = null, // 화재감지기 개수

    // 주변 시설
    val nearbyFishing: Boolean = false, // 낚시
    val nearbyWalkingTrail: Boolean = false, // 산책로
    val nearbyBeach: Boolean = false, // 물놀이(해수욕)
    val nearbyWaterSports: Boolean = false, // 물놀이(수상레저)
    val nearbyValley: Boolean = false, // 물놀이(계곡)
    val nearbyRiver: Boolean = false, // 물놀이(강)
    val nearbyPool: Boolean = false, // 물놀이(수영장)
    val nearbyYouthFacility: Boolean = false, // 청소년체험시설
    val nearbyRuralExperience: Boolean = false, // 농어촌체험시설
    val nearbyKidsPlayground: Boolean = false, // 어린이놀이시설

    // 글램핑 시설
    val glampingBed: Boolean = false, // 침대
    val glampingTv: Boolean = false, // TV
    val glampingFridge: Boolean = false, // 냉장고
    val glampingInternet: Boolean = false, // 유무선인터넷
    val glampingToilet: Boolean = false, // 내부화장실
    val glampingAircon: Boolean = false, // 에어컨
    val glampingHeater: Boolean = false, // 난방기구
    val glampingCooking: Boolean = false, // 취사도구

    // 추가 정보
    val theme: String?, // 테마환경
    val equipmentRental: String?, // 캠핑장비대여
    val petAllowed: Boolean = false, // 반려동물출입
    val facilities: String?, // 부대시설 (추가 설명)
    val nearbyFacilities: String?, // 주변이용가능시설
    val features: String?, // 시설 특징
    val introduction: String?, // 시설 소개
    val licenseDate: String?, // 인허가일자

    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
) {
    companion object {
        fun create(
            campsiteId: Long,
            name: String,
            managementType: String? = null,
            province: String? = null,
            city: String? = null,
            address: String? = null,
            roadAddress: String? = null,
            zipCode: String? = null,
            phone: String? = null,
            homepage: String? = null,
            latitude: Double? = null,
            longitude: Double? = null,
        ): Campsite {
            require(name.isNotBlank()) { "캠핑장 이름은 필수입니다." }

            return Campsite(
                campsiteId = campsiteId,
                name = name.trim(),
                managementType = managementType?.trim(),
                province = province?.trim(),
                city = city?.trim(),
                address = address?.trim(),
                roadAddress = roadAddress?.trim(),
                zipCode = zipCode?.trim(),
                phone = phone?.trim(),
                homepage = homepage?.trim(),
                latitude = latitude,
                longitude = longitude,
                theme = null,
                equipmentRental = null,
                facilities = null,
                nearbyFacilities = null,
                features = null,
                introduction = null,
                licenseDate = null,
            )
        }
    }

    fun updateBasicInfo(
        name: String? = null,
        managementType: String? = null,
        province: String? = null,
        city: String? = null,
        address: String? = null,
        roadAddress: String? = null,
        zipCode: String? = null,
        phone: String? = null,
        homepage: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
    ): Campsite {
        return copy(
            name = name?.trim() ?: this.name,
            managementType = managementType?.trim() ?: this.managementType,
            province = province?.trim() ?: this.province,
            city = city?.trim() ?: this.city,
            address = address?.trim() ?: this.address,
            roadAddress = roadAddress?.trim() ?: this.roadAddress,
            zipCode = zipCode?.trim() ?: this.zipCode,
            phone = phone?.trim() ?: this.phone,
            homepage = homepage?.trim() ?: this.homepage,
            latitude = latitude ?: this.latitude,
            longitude = longitude ?: this.longitude,
            updatedAt = LocalDateTime.now()
        )
    }
} 