package com.softwareplace.fileserver.service

import com.softwareplace.fileserver.file.exception.FileNotFoundException
import com.softwareplace.fileserver.file.exception.FileStorageException
import com.softwareplace.fileserver.properties.AppProperties
import com.softwareplace.fileserver.rest.model.DataRest
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class FileStorageService(
    private val properties: AppProperties
) {

    private val fileStorageLocation: Path = Paths.get(properties.storagePath)
        .toAbsolutePath()
        .normalize()

    init {
        try {
            Files.createDirectories(this.fileStorageLocation)
        } catch (ex: Exception) {
            throw FileStorageException("Could not create the directory where the uploaded files will be stored.", ex)
        }

    }

    fun storeFile(file: Resource, fileName: String): String {
        try {
            val targetLocation = this.fileStorageLocation.resolve(fileName)

            val directory = File(targetLocation.toUri())

            if (!directory.exists()) {
                directory.mkdirs()
            }
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

            return fileName
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        throw FileStorageException("Could not store the file, Please try again!")
    }

    fun loadFileAsResource(filePath: String): Resource {
        try {
            val path = fileStorageLocation.resolve("${properties.storagePath}/$filePath").normalize()
            val resource = UrlResource(path.toUri())
            return if (resource.exists()) {
                resource
            } else {
                throw FileNotFoundException("File not found $$filePath")
            }
        } catch (ex: MalformedURLException) {
            throw FileNotFoundException("File not found $$filePath", ex)
        }

    }

    fun list(resource: String?): DataRest? {
        val list = mutableListOf<String>()

        val path = fileStorageLocation.resolve("${properties.storagePath}/${resource ?: ""}").normalize()

        val files = path.toFile().listFiles()
        files?.forEach {
            if (it.isDirectory) {
                it.listFiles()?.forEach { file ->
                    list.add(file.path.replace(fileStorageLocation.toString(), ""))
                }
            } else {
                list.add(it.path.replace(fileStorageLocation.toString(), ""))
            }
        }

        return DataRest(list)
    }
}
