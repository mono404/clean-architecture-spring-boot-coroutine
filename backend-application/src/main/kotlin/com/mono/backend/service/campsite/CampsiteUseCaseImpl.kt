package com.mono.backend.service.campsite

import com.mono.backend.port.web.campsite.CampsiteUseCase
import com.mono.backend.port.web.campsite.dto.CampsiteListResponse
import com.mono.backend.port.web.campsite.dto.CampsiteResponse
import com.mono.backend.port.web.campsite.dto.CampsiteStatisticsResponse
import com.mono.backend.port.web.campsite.dto.CsvImportResponse
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class CampsiteUseCaseImpl(
    private val campsiteService: CampsiteService
) : CampsiteUseCase {

    override suspend fun importCsvFile1(filePart: FilePart): CsvImportResponse {
        val result = campsiteService.parseAndSaveCsvFile1(filePart)
        return CsvImportResponse(
            success = result.success,
            totalParsed = result.totalParsed,
            totalSaved = result.totalSaved,
            message = if (result.success) "CSV 파일 1 가져오기 성공" else (result.errorMessage ?: "알 수 없는 오류")
        )
    }

    override suspend fun importCsvFile2(filePart: FilePart): CsvImportResponse {
        val result = campsiteService.parseAndSaveCsvFile2(filePart)
        return CsvImportResponse(
            success = result.success,
            totalParsed = result.totalParsed,
            totalSaved = result.totalSaved,
            message = if (result.success) "CSV 파일 2 가져오기 성공" else (result.errorMessage ?: "알 수 없는 오류")
        )
    }

    override suspend fun getCampsites(page: Int, size: Int): CampsiteListResponse {
        val campsites = campsiteService.findAll(page, size)
        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) },
            currentPage = page,
            pageSize = size
        )
    }

    override suspend fun getCampsite(campsiteId: Long): CampsiteResponse? {
        val campsite = campsiteService.findById(campsiteId)
        return campsite?.let { CampsiteResponse.from(it) }
    }

    override suspend fun getCampsitesByProvince(province: String, page: Int, size: Int): CampsiteListResponse {
        val campsites = campsiteService.findByProvince(province, page, size)
        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) },
            currentPage = page,
            pageSize = size
        )
    }

    override suspend fun getCampsitesByCity(
        province: String,
        city: String,
        page: Int,
        size: Int
    ): CampsiteListResponse {
        val campsites = campsiteService.findByCity(province, city, page, size)
        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) },
            currentPage = page,
            pageSize = size
        )
    }

    override suspend fun searchCampsitesByName(name: String, page: Int, size: Int): CampsiteListResponse {
        val campsites = campsiteService.searchByName(name, page, size)
        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) },
            currentPage = page,
            pageSize = size
        )
    }

    override suspend fun getStatistics(): CampsiteStatisticsResponse {
        val stats = campsiteService.getStatistics()
        return CampsiteStatisticsResponse(
            totalCampsites = stats.totalCampsites
        )
    }

    override suspend fun clearAllData(): Boolean {
        return campsiteService.clearAllData()
    }

    override suspend fun getCampsitesWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double,
        page: Int,
        size: Int
    ): CampsiteListResponse {
        val campsites = campsiteService.findCampsitesWithinRadius(latitude, longitude, radiusInKm, page, size)
        val totalCount = campsiteService.countCampsitesWithinRadius(latitude, longitude, radiusInKm)

        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) },
            totalCount = totalCount,
            currentPage = page,
            pageSize = size
        )
    }

    override suspend fun getNearestCampsites(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): CampsiteListResponse {
        val campsites = campsiteService.findNearestCampsites(latitude, longitude, limit)

        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) }
        )
    }

    override suspend fun getCampsitesWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double,
        page: Int,
        size: Int
    ): CampsiteListResponse {
        val campsites = campsiteService.findCampsitesWithinBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude, page, size
        )
        val totalCount = campsiteService.countCampsitesWithinBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude
        )

        return CampsiteListResponse(
            campsites = campsites.map { CampsiteResponse.from(it) },
            totalCount = totalCount,
            currentPage = page,
            pageSize = size
        )
    }
} 