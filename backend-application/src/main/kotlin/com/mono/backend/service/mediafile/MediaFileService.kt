package com.mono.backend.service.mediafile

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.mediafile.MediaFile
import com.mono.backend.domain.mediafile.OwnerType
import com.mono.backend.port.infra.mediafile.MediaFilePersistencePort
import com.mono.backend.port.infra.s3client.S3UploadClientPort
import com.mono.backend.port.web.mediafile.MediaFileUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class MediaFileService(
    private val mediaFilePersistencePort: MediaFilePersistencePort,
    private val s3UploadClientPort: S3UploadClientPort,
) : MediaFileUseCase {
    override suspend fun uploadMediaFile(
        ownerId: Long,
        ownerType: OwnerType,
        filePart: FilePart
    ): Pair<String, String> {
        val fileName = "${ownerType.code}/${ownerId}_${System.currentTimeMillis()}.png"
        val fileResponse = s3UploadClientPort.upload(fileName, filePart)

        val mediaFile = MediaFile(
            mediaFileId = Snowflake.nextId(),
            ownerType = ownerType,
            ownerId = ownerId,
            s3Path = fileResponse.path,
            mediaType = filePart.headers().contentType?.toString() ?: "application/octet-stream",
            size = filePart.headers().contentLength,
        )

        return filePart.filename() to mediaFilePersistencePort.save(mediaFile).s3Path
    }

    override suspend fun uploadMediaFiles(
        ownerId: Long,
        ownerType: OwnerType,
        fileParts: List<FilePart>
    ): Map<String, String> =
        coroutineScope {
            val pair = fileParts.map {
                async {
                    val fileName = "${ownerType.code}/${ownerId}_${System.currentTimeMillis()}.png"
                    val fileResponse = s3UploadClientPort.upload(fileName, it)
                    it.filename() to MediaFile(
                        mediaFileId = Snowflake.nextId(),
                        ownerType = ownerType,
                        ownerId = ownerId,
                        s3Path = fileResponse.path,
                        mediaType = it.headers().contentType?.toString() ?: "application/octet-stream",
                        size = it.headers().contentLength
                    )
                }
            }.awaitAll().toMap()

            mediaFilePersistencePort.saveAll(pair.values.toList())

            pair.map { it.key to it.value.s3Path }.toMap()
        }

    override suspend fun deleteMediaFiles(ownerId: Long, ownerType: OwnerType) {
        coroutineScope {
            mediaFilePersistencePort.findAllByOwner(ownerId, ownerType)
                .map { it.s3Path }
                .takeIf { it.isNotEmpty() }
                ?.let { s3Urls ->
                    launch { s3UploadClientPort.delete(s3Urls) }
                    launch { mediaFilePersistencePort.deleteByOwner(ownerId, ownerType) }
                }
        }
    }
}