package com.mono.backend.port.infra.campsite.csv

import com.mono.backend.domain.campsite.Campsite
import org.springframework.http.codec.multipart.FilePart

interface CampsiteCsvParserPort {
    suspend fun parseFile1(filePart: FilePart): List<Campsite>
    suspend fun parseFile2(filePart: FilePart): List<Campsite>
} 