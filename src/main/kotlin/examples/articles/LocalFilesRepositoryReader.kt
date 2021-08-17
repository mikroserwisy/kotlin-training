package examples.articles

import java.io.File
import java.io.IOException
import java.nio.file.Files.readAllBytes
import java.nio.file.Path

class LocalFilesRepositoryReader(private val localPath: Path) : FilesRepositoryReader {

    override fun getMetadata(predicate: (FileMetadata) -> Boolean) = localPath.toFile()
        .walkTopDown()
        .map(::toFileMetadata)
        .filter(predicate)
        .toList()

    private fun toFileMetadata(file: File): FileMetadata {
        val name = file.nameWithoutExtension
        val extension = file.extension
        val path = file.relativeTo(localPath.toFile()).path
        return FileMetadata(name, extension, path)
    }

    override fun getBytes(fileMetadata: FileMetadata): ByteArray {
        return try {
            readAllBytes(localPath.resolve(fileMetadata.path))
        } catch (exception: IOException) {
            throw ReadFailedException()
        }
    }

}