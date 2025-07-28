package com.mono.backend.web.campsite

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.campsite.CampsiteUseCase
import com.mono.backend.web.common.DefaultHandler
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitMultipartData
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class CampsiteHandler(
    private val campsiteUseCase: CampsiteUseCase
) : DefaultHandler {
    private val log = logger()

    suspend fun importCsvFile1(serverRequest: ServerRequest): ServerResponse {
        return try {
            val multipartData = serverRequest.awaitMultipartData()
            val filePart = multipartData["file"]?.first() as? FilePart
                ?: return badRequest("CSV 파일이 필요합니다.")

            if (!isValidCsvFile(filePart)) {
                return badRequest("유효하지 않은 파일 형식입니다. CSV 파일만 허용됩니다.")
            }

            log.info("CSV 파일 1 가져오기 시작: ${filePart.filename()}")
            val response = campsiteUseCase.importCsvFile1(filePart)

            if (response.success) {
                ok(response)
            } else {
                badRequest(response)
            }
        } catch (e: Exception) {
            log.error("CSV 파일 1 가져오기 실패", e)
            badRequest("CSV 파일 처리 중 오류가 발생했습니다: ${e.message}")
        }
    }

    suspend fun importCsvFile2(serverRequest: ServerRequest): ServerResponse {
        return try {
            val multipartData = serverRequest.awaitMultipartData()
            val filePart = multipartData["file"]?.first() as? FilePart
                ?: return badRequest("CSV 파일이 필요합니다.")

            if (!isValidCsvFile(filePart)) {
                return badRequest("유효하지 않은 파일 형식입니다. CSV 파일만 허용됩니다.")
            }

            log.info("CSV 파일 2 가져오기 시작: ${filePart.filename()}")
            val response = campsiteUseCase.importCsvFile2(filePart)

            if (response.success) {
                ok(response)
            } else {
                badRequest(response)
            }
        } catch (e: Exception) {
            log.error("CSV 파일 2 가져오기 실패", e)
            badRequest("CSV 파일 처리 중 오류가 발생했습니다: ${e.message}")
        }
    }

    suspend fun getCampsites(serverRequest: ServerRequest): ServerResponse {
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        val response = campsiteUseCase.getCampsites(page, size)
        return ok(response)
    }

    suspend fun getCampsite(serverRequest: ServerRequest): ServerResponse {
        val campsiteId = serverRequest.pathVariable("campsiteId").toLongOrNull()
            ?: return badRequest("유효하지 않은 캠핑장 ID입니다.")

        val response = campsiteUseCase.getCampsite(campsiteId)
        return if (response != null) {
            ok(response)
        } else {
            notFound()
        }
    }

    suspend fun getCampsitesByProvince(serverRequest: ServerRequest): ServerResponse {
        val province = serverRequest.pathVariable("province")
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        val response = campsiteUseCase.getCampsitesByProvince(province, page, size)
        return ok(response)
    }

    suspend fun getCampsitesByCity(serverRequest: ServerRequest): ServerResponse {
        val province = serverRequest.pathVariable("province")
        val city = serverRequest.pathVariable("city")
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        val response = campsiteUseCase.getCampsitesByCity(province, city, page, size)
        return ok(response)
    }

    suspend fun searchCampsitesByName(serverRequest: ServerRequest): ServerResponse {
        val name = serverRequest.queryParamOrNull("name")
            ?: return badRequest("검색할 캠핑장 이름이 필요합니다.")
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        val response = campsiteUseCase.searchCampsitesByName(name, page, size)
        return ok(response)
    }

    suspend fun getStatistics(serverRequest: ServerRequest): ServerResponse {
        val response = campsiteUseCase.getStatistics()
        return ok(response)
    }

    suspend fun clearAllData(serverRequest: ServerRequest): ServerResponse {
        val success = campsiteUseCase.clearAllData()
        return if (success) {
            ok(mapOf("message" to "모든 캠핑장 데이터가 삭제되었습니다."))
        } else {
            badRequest("데이터 삭제 중 오류가 발생했습니다.")
        }
    }

    // Spatial 검색 핸들러들
    suspend fun getCampsitesWithinRadius(serverRequest: ServerRequest): ServerResponse {
        val latitude = serverRequest.queryParamOrNull("latitude")?.toDoubleOrNull()
            ?: return badRequest("위도(latitude) 파라미터가 필요합니다.")
        val longitude = serverRequest.queryParamOrNull("longitude")?.toDoubleOrNull()
            ?: return badRequest("경도(longitude) 파라미터가 필요합니다.")
        val radius = serverRequest.queryParamOrNull("radius")?.toDoubleOrNull()
            ?: return badRequest("반경(radius) 파라미터가 필요합니다. (단위: km)")
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        if (radius <= 0 || radius > 100) {
            return badRequest("반경은 0보다 크고 100km 이하여야 합니다.")
        }

        val response = campsiteUseCase.getCampsitesWithinRadius(latitude, longitude, radius, page, size)
        return ok(response)
    }

    suspend fun getNearestCampsites(serverRequest: ServerRequest): ServerResponse {
        val latitude = serverRequest.queryParamOrNull("latitude")?.toDoubleOrNull()
            ?: return badRequest("위도(latitude) 파라미터가 필요합니다.")
        val longitude = serverRequest.queryParamOrNull("longitude")?.toDoubleOrNull()
            ?: return badRequest("경도(longitude) 파라미터가 필요합니다.")
        val limit = serverRequest.queryParamOrNull("limit")?.toIntOrNull() ?: 10

        if (limit <= 0 || limit > 50) {
            return badRequest("limit은 1 이상 50 이하여야 합니다.")
        }

        val response = campsiteUseCase.getNearestCampsites(latitude, longitude, limit)
        return ok(response)
    }

    suspend fun getCampsitesWithinBounds(serverRequest: ServerRequest): ServerResponse {
        val minLat = serverRequest.queryParamOrNull("minLatitude")?.toDoubleOrNull()
            ?: return badRequest("최소 위도(minLatitude) 파라미터가 필요합니다.")
        val maxLat = serverRequest.queryParamOrNull("maxLatitude")?.toDoubleOrNull()
            ?: return badRequest("최대 위도(maxLatitude) 파라미터가 필요합니다.")
        val minLng = serverRequest.queryParamOrNull("minLongitude")?.toDoubleOrNull()
            ?: return badRequest("최소 경도(minLongitude) 파라미터가 필요합니다.")
        val maxLng = serverRequest.queryParamOrNull("maxLongitude")?.toDoubleOrNull()
            ?: return badRequest("최대 경도(maxLongitude) 파라미터가 필요합니다.")
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        if (minLat >= maxLat || minLng >= maxLng) {
            return badRequest("경계 좌표가 올바르지 않습니다. (min < max)")
        }

        val response = campsiteUseCase.getCampsitesWithinBounds(minLat, maxLat, minLng, maxLng, page, size)
        return ok(response)
    }

    private fun isValidCsvFile(filePart: FilePart): Boolean {
        val filename = filePart.filename()
        val contentType = filePart.headers().contentType

        return filename != null &&
                (filename.endsWith(".csv", ignoreCase = true) ||
                        contentType == MediaType.parseMediaType("text/csv") ||
                        contentType == MediaType.parseMediaType("application/csv"))
    }
} 