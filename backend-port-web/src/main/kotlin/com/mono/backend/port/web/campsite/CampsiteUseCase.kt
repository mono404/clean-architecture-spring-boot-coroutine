package com.mono.backend.port.web.campsite

import com.mono.backend.port.web.campsite.dto.CampsiteListResponse
import com.mono.backend.port.web.campsite.dto.CampsiteResponse
import com.mono.backend.port.web.campsite.dto.CampsiteStatisticsResponse
import com.mono.backend.port.web.campsite.dto.CsvImportResponse
import org.springframework.http.codec.multipart.FilePart

interface CampsiteUseCase {
    suspend fun importCsvFile1(filePart: FilePart): CsvImportResponse
    suspend fun importCsvFile2(filePart: FilePart): CsvImportResponse
    suspend fun getCampsites(page: Int = 0, size: Int = 20): CampsiteListResponse
    suspend fun getCampsite(campsiteId: Long): CampsiteResponse?
    suspend fun getCampsitesByProvince(province: String, page: Int = 0, size: Int = 20): CampsiteListResponse
    suspend fun getCampsitesByCity(province: String, city: String, page: Int = 0, size: Int = 20): CampsiteListResponse
    suspend fun searchCampsitesByName(name: String, page: Int = 0, size: Int = 20): CampsiteListResponse
    suspend fun getStatistics(): CampsiteStatisticsResponse
    suspend fun clearAllData(): Boolean

    // Spatial 검색 메서드들
    suspend fun getCampsitesWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double,
        page: Int = 0,
        size: Int = 20
    ): CampsiteListResponse

    suspend fun getNearestCampsites(
        latitude: Double,
        longitude: Double,
        limit: Int = 10
    ): CampsiteListResponse

    suspend fun getCampsitesWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double,
        page: Int = 0,
        size: Int = 20
    ): CampsiteListResponse
} 