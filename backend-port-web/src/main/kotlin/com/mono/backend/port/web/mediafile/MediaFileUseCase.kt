package com.mono.backend.port.web.mediafile

import com.mono.backend.domain.mediafile.OwnerType
import org.springframework.http.codec.multipart.FilePart

interface MediaFileUseCase {
    suspend fun uploadMediaFile(ownerId: Long, ownerType: OwnerType, filePart: FilePart): Pair<String, String>
    suspend fun uploadMediaFiles(ownerId: Long, ownerType: OwnerType, fileParts: List<FilePart>): Map<String, String>
    suspend fun deleteMediaFiles(ownerId: Long, ownerType: OwnerType)
}