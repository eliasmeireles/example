package com.softwareplace.fileserver.controller

import com.softwareplace.fileserver.rest.controller.FileController
import com.softwareplace.fileserver.rest.model.DataRest
import com.softwareplace.fileserver.rest.model.UploadFileResponseRest
import com.softwareplace.fileserver.service.FileStorageService
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FileControllerImpl(
    private val fileStorageService: FileStorageService
) : FileController {

    override suspend fun downloadFile(filePath: String): ResponseEntity<Resource> {
        val resource = fileStorageService.loadFileAsResource(filePath = filePath)

        val contentType = "application/octet-stream"

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename + "\"")
            .body(resource)
    }

    override suspend fun list(authorization: String, resource: String): ResponseEntity<DataRest> {
        return ResponseEntity.ok(fileStorageService.list(resource))
    }

    override suspend fun uploadFile(
        authorization: String,
        resource: Resource,
        dirName: String,
        fileName: String
    ): ResponseEntity<UploadFileResponseRest> =
        fileStorageService.storeFile(resource, normalizeFileName(dirName, fileName, resource)).run {

            val uploadFileResponse = UploadFileResponseRest(
                fileName,
                "file/download/$this",
                getFileExtension(resource.filename!!),
                resource.contentLength(),
                getFileExtension(resource.filename!!)
            )
            return ResponseEntity.ok(uploadFileResponse)
        }

    private fun normalizeFileName(dirName: String, fileName: String, resource: Resource): String {
        val normalizedFileName = fileName.replace("/", "")
            .replace(Regex("\\s"), "")

        val fileExtension = getFileExtension(resource.filename)
        return "$dirName/$normalizedFileName$fileExtension"
    }

    private fun getFileExtension(fileOriginalName: String?): String {
        return when (fileOriginalName) {
            null -> ""
            else -> fileOriginalName.substring(fileOriginalName.lastIndexOf("."))
        }
    }
}
