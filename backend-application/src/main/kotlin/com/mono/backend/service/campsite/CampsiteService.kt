package com.mono.backend.service.campsite

import com.mono.backend.common.log.logger
import com.mono.backend.domain.campsite.Campsite
import com.mono.backend.port.infra.campsite.csv.CampsiteCsvParserPort
import com.mono.backend.port.infra.campsite.persistence.CampsitePersistencePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class CampsiteService(
    private val campsitePersistencePort: CampsitePersistencePort,
    private val csvParser: CampsiteCsvParserPort,
) {
    private val log = logger()

    suspend fun parseAndSaveCsvFile1(filePart: FilePart): CsvImportResult = withContext(Dispatchers.IO) {
        try {
            log.info("Starting CSV file 1 parsing and import")

            val campsites = csvParser.parseFile1(filePart)
            log.info("Parsed ${campsites.size} campsites from CSV file 1")

            if (campsites.isEmpty()) {
                return@withContext CsvImportResult(
                    success = false,
                    totalParsed = 0,
                    totalSaved = 0,
                    errorMessage = "No valid campsites found in the CSV file"
                )
            }

            val savedCampsites = campsitePersistencePort.saveAll(campsites)
            log.info("Saved ${savedCampsites.size} campsites to database")

            CsvImportResult(
                success = true,
                totalParsed = campsites.size,
                totalSaved = savedCampsites.size
            )
        } catch (e: Exception) {
            log.error("Error parsing/saving CSV file 1", e)
            CsvImportResult(
                success = false,
                totalParsed = 0,
                totalSaved = 0,
                errorMessage = e.message
            )
        }
    }

    suspend fun parseAndSaveCsvFile2(filePart: FilePart): CsvImportResult = withContext(Dispatchers.IO) {
        try {
            log.info("Starting CSV file 2 parsing and import")

            val campsites = csvParser.parseFile2(filePart)
            log.info("Parsed ${campsites.size} campsites from CSV file 2")

            if (campsites.isEmpty()) {
                return@withContext CsvImportResult(
                    success = false,
                    totalParsed = 0,
                    totalSaved = 0,
                    errorMessage = "No valid campsites found in the CSV file"
                )
            }

            val savedCampsites = campsitePersistencePort.saveAll(campsites)
            log.info("Saved ${savedCampsites.size} campsites to database")

            CsvImportResult(
                success = true,
                totalParsed = campsites.size,
                totalSaved = savedCampsites.size
            )
        } catch (e: Exception) {
            log.error("Error parsing/saving CSV file 2", e)
            CsvImportResult(
                success = false,
                totalParsed = 0,
                totalSaved = 0,
                errorMessage = e.message
            )
        }
    }

    suspend fun findAll(page: Int = 0, size: Int = 20): List<Campsite> {
        val offset = page * size
        return campsitePersistencePort.findAll(size, offset)
    }

    suspend fun findById(campsiteId: Long): Campsite? {
        return campsitePersistencePort.findById(campsiteId)
    }

    suspend fun findByProvince(province: String, page: Int = 0, size: Int = 20): List<Campsite> {
        val offset = page * size
        return campsitePersistencePort.findByProvince(province, size, offset)
    }

    suspend fun findByCity(province: String, city: String, page: Int = 0, size: Int = 20): List<Campsite> {
        val offset = page * size
        return campsitePersistencePort.findByCity(province, city, size, offset)
    }

    suspend fun searchByName(name: String, page: Int = 0, size: Int = 20): List<Campsite> {
        val offset = page * size
        return campsitePersistencePort.findByName(name, size, offset)
    }

    suspend fun getStatistics(): CampsiteStatistics {
        val total = campsitePersistencePort.count()
        return CampsiteStatistics(
            totalCampsites = total
        )
    }

    suspend fun clearAllData(): Boolean {
        return try {
            log.info("Clearing all campsite data")
            campsitePersistencePort.deleteAll()
            log.info("Successfully cleared all campsite data")
            true
        } catch (e: Exception) {
            log.error("Error clearing campsite data", e)
            false
        }
    }

    // Spatial 검색 메서드들
    suspend fun findCampsitesWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double,
        page: Int = 0,
        size: Int = 20
    ): List<Campsite> {
        val offset = page * size
        val radiusInMeters = radiusInKm * 1000 // km를 미터로 변환
        return campsitePersistencePort.findWithinRadius(latitude, longitude, radiusInMeters, size, offset)
    }

    suspend fun countCampsitesWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double
    ): Long {
        val radiusInMeters = radiusInKm * 1000 // km를 미터로 변환
        return campsitePersistencePort.countWithinRadius(latitude, longitude, radiusInMeters)
    }

    suspend fun findNearestCampsites(
        latitude: Double,
        longitude: Double,
        limit: Int = 10
    ): List<Campsite> {
        return campsitePersistencePort.findNearestCampsites(latitude, longitude, limit)
    }

    suspend fun findCampsitesWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double,
        page: Int = 0,
        size: Int = 20
    ): List<Campsite> {
        val offset = page * size
        return campsitePersistencePort.findWithinBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude, size, offset
        )
    }

    suspend fun countCampsitesWithinBounds(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double
    ): Long {
        return campsitePersistencePort.countWithinBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude
        )
    }
}

data class CsvImportResult(
    val success: Boolean,
    val totalParsed: Int,
    val totalSaved: Int,
    val errorMessage: String? = null
)

data class CampsiteStatistics(
    val totalCampsites: Long
) 