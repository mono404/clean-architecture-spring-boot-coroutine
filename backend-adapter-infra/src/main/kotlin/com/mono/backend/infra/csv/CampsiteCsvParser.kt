package com.mono.backend.infra.csv

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.campsite.Campsite
import com.mono.backend.port.infra.campsite.csv.CampsiteCsvParserPort
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
class CampsiteCsvParser : CampsiteCsvParserPort {

    override suspend fun parseFile1(filePart: FilePart): List<Campsite> {
        val content = readFileContent(filePart)
        val lines = content.split("\n").drop(1) // Skip header

        return lines.mapNotNull { line ->
            if (line.trim().isEmpty()) return@mapNotNull null
            parseFile1Line(line.trim())
        }
    }

    override suspend fun parseFile2(filePart: FilePart): List<Campsite> {
        val content = readFileContent(filePart)
        val lines = content.split("\n").drop(1) // Skip header

        return lines.mapNotNull { line ->
            if (line.trim().isEmpty()) return@mapNotNull null
            parseFile2Line(line.trim())
        }
    }

    private fun parseFile1Line(line: String): Campsite? {
        return try {
            val fields = line.split(",")
            if (fields.size < 31) return null

            val campsiteId = Snowflake.nextId()
            val name = fields[1].trim()
            if (name.isEmpty()) return null

            val managementType = fields[2].takeIf { it.isNotBlank() }
            val province = fields[3].takeIf { it.isNotBlank() }
            val city = fields[4].takeIf { it.isNotBlank() }
            val address = fields[5].takeIf { it.isNotBlank() }

            // 연락처 조합
            val phone = combinePhoneNumber(fields[6], fields[7], fields[8])

            // 파일 1에는 위치 정보가 없으므로 null 반환 (나중에 주소로 geocoding 예정)
            return null
        } catch (e: Exception) {
            null // 파싱 실패 시 null 반환
        }
    }

    private fun parseFile2Line(line: String): Campsite? {
        return try {
            val fields = line.replace("\"", "").split(",")

            val campsiteId = Snowflake.nextId()
            val name = fields[0].trim()
            if (name.isEmpty()) return null

            val province = fields[4].takeIf { it.isNotBlank() }
            val city = fields[5].takeIf { it.isNotBlank() }
            val roadAddress = buildRoadAddress(fields)
            val zipCode = fields[13].takeIf { it.isNotBlank() }
            val phone = fields[16].takeIf { it.isNotBlank() }
            val homepage = fields[17].takeIf { it.isNotBlank() }
            val managementType = fields[18].takeIf { it.isNotBlank() }

            val latitude = parseDouble(fields[11])
            val longitude = parseDouble(fields[12])

            // 위치 정보가 없으면 제외 (location이 NOT NULL이므로)
            if (latitude == null || longitude == null) {
                return null
            }

            Campsite(
                campsiteId = campsiteId,
                name = name,
                managementType = managementType,
                province = province,
                city = city,
                address = fields[15].takeIf { it.isNotBlank() }, // 지번주소
                roadAddress = roadAddress,
                zipCode = zipCode,
                phone = phone,
                homepage = homepage,
                latitude = latitude,
                longitude = longitude,

                // 운영 정보
                weekdayOpen = parseBoolean(fields.getOrNull(19)),
                weekendOpen = parseBoolean(fields.getOrNull(20)),
                springOpen = parseBoolean(fields.getOrNull(21)),
                summerOpen = parseBoolean(fields.getOrNull(22)),
                fallOpen = parseBoolean(fields.getOrNull(23)),
                winterOpen = parseBoolean(fields.getOrNull(24)),

                // 부대시설
                hasElectricity = parseBoolean(fields.getOrNull(25)),
                hasHotWater = parseBoolean(fields.getOrNull(26)),
                hasWifi = parseBoolean(fields.getOrNull(27)),
                hasFirewood = parseBoolean(fields.getOrNull(28)),
                hasWalkingTrail = parseBoolean(fields.getOrNull(29)),
                hasWaterPlay = parseBoolean(fields.getOrNull(30)),
                hasPlayground = parseBoolean(fields.getOrNull(31)),
                hasMart = parseBoolean(fields.getOrNull(32)),
                toiletCount = parseInt(fields.getOrNull(33)),
                showerCount = parseInt(fields.getOrNull(34)),
                sinkCount = parseInt(fields.getOrNull(35)),
                fireExtinguisherCount = parseInt(fields.getOrNull(36)),

                // 주변 시설
                nearbyFishing = parseBoolean(fields.getOrNull(37)),
                nearbyWalkingTrail = parseBoolean(fields.getOrNull(38)),
                nearbyBeach = parseBoolean(fields.getOrNull(39)),
                nearbyWaterSports = parseBoolean(fields.getOrNull(40)),
                nearbyValley = parseBoolean(fields.getOrNull(41)),
                nearbyRiver = parseBoolean(fields.getOrNull(42)),
                nearbyPool = parseBoolean(fields.getOrNull(43)),
                nearbyYouthFacility = parseBoolean(fields.getOrNull(44)),
                nearbyRuralExperience = parseBoolean(fields.getOrNull(45)),
                nearbyKidsPlayground = parseBoolean(fields.getOrNull(46)),

                // 글램핑 시설
                glampingBed = parseBoolean(fields.getOrNull(47)),
                glampingTv = parseBoolean(fields.getOrNull(48)),
                glampingFridge = parseBoolean(fields.getOrNull(49)),
                glampingInternet = parseBoolean(fields.getOrNull(50)),
                glampingToilet = parseBoolean(fields.getOrNull(51)),
                glampingAircon = parseBoolean(fields.getOrNull(52)),
                glampingHeater = parseBoolean(fields.getOrNull(53)),
                glampingCooking = parseBoolean(fields.getOrNull(54)),

                features = fields.getOrNull(55)?.takeIf { it.isNotBlank() },
                introduction = fields.getOrNull(56)?.takeIf { it.isNotBlank() },
                theme = null,
                equipmentRental = null,
                facilities = null,
                nearbyFacilities = null,
                licenseDate = null,
            )
        } catch (e: Exception) {
            null // 파싱 실패 시 null 반환
        }
    }

    private suspend fun readFileContent(filePart: FilePart): String {
        val dataBuffer = DataBufferUtils.join(filePart.content()).awaitSingle()
        val bytes = ByteArray(dataBuffer.readableByteCount())
        dataBuffer.read(bytes)
        DataBufferUtils.release(dataBuffer) // 메모리 누수 방지
        return String(bytes, Charset.forName("MS949"))
    }

    private fun combinePhoneNumber(part1: String?, part2: String?, part3: String?): String? {
        val p1 = part1?.trim()?.takeIf { it.isNotBlank() }
        val p2 = part2?.trim()?.takeIf { it.isNotBlank() }
        val p3 = part3?.trim()?.takeIf { it.isNotBlank() }

        return if (p1 != null && p2 != null && p3 != null) {
            "$p1-$p2-$p3"
        } else null
    }

    private fun buildRoadAddress(fields: List<String>): String? {
        val roadName = fields.getOrNull(9)?.takeIf { it.isNotBlank() }
        val buildingNumber = fields.getOrNull(10)?.takeIf { it.isNotBlank() }

        return if (roadName != null && buildingNumber != null) {
            "$roadName $buildingNumber"
        } else null
    }

    private fun parseBoolean(value: String?): Boolean {
        return when (value?.trim()?.lowercase()) {
            "y", "yes", "true", "1", "o", "가능", "있음", "운영" -> true
            else -> false
        }
    }

    private fun parseInt(value: String?): Int? {
        return value?.trim()?.takeIf { it.isNotBlank() }?.toIntOrNull()
    }

    private fun parseDouble(value: String?): Double? {
        return value?.trim()?.takeIf { it.isNotBlank() }?.toDoubleOrNull()
    }
} 