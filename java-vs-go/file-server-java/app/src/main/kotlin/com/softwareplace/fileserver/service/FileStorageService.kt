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

    class FileStorageException(message: String, cause: Throwable) : Exception(message, cause)

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
            val path = Paths.get("${properties.storagePath}/$filePath")
                .toAbsolutePath()
                .normalize()

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

        val path = Paths.get("${properties.storagePath}/${resource ?: ""}")
            .toAbsolutePath()
            .normalize()

        val files = path.toFile().listFiles()
        files?.forEach {
            if (it.isDirectory) {
                it.listFiles()?.forEach { file ->
                    list.add(file.path.replace(fileStorageLocation.toString(), "/files/download?filePath="))
                }
            } else {
                list.add(it.path.replace(fileStorageLocation.toString(), "/files/download?filePath="))
            }
        }

        return DataRest(list)
    }


    fun delete(resource: String) {
        try {
            val baseStoragePath = properties.storagePath

            // Normalize the file path
            val normalizedFilePath = Paths.get("${properties.storagePath}/$resource")
                .toAbsolutePath()
                .normalize()

            // Ensure the file path is within the base storage path
            val basePath = Paths.get(baseStoragePath).toAbsolutePath().normalize()
            if (!normalizedFilePath.startsWith(basePath)) {
                throw SecurityException("Illegal operation: file path is outside the base storage path")
            }

            // Check if the path exists
            if (Files.exists(normalizedFilePath)) {
                // Delete the file or directory recursively
                if (Files.isDirectory(normalizedFilePath)) {
                    // Recursively delete directory and its contents
                    Files.walk(normalizedFilePath)
                        .sorted(Comparator.reverseOrder()) // Delete files before directories
                        .forEach { path ->
                            try {
                                Files.deleteIfExists(path)
                            } catch (ex: IOException) {
                                throw FileStorageException("Failed to delete $path", ex)
                            }
                        }
                } else {
                    // Delete a single file
                    Files.deleteIfExists(normalizedFilePath)
                }
            } else {
                println("File or directory does not exist: $normalizedFilePath")
            }
        } catch (ex: IOException) {
            throw FileStorageException("Could not delete the file or directory. Please try again!", ex)
        } catch (ex: SecurityException) {
            throw FileStorageException("Illegal operation: ${ex.message}", ex)
        }
    }
}
