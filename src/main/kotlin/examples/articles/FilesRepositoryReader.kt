package examples.articles

interface FilesRepositoryReader {

    fun getMetadata(predicate: (FileMetadata) -> Boolean): List<FileMetadata>

    fun getBytes(fileMetadata: FileMetadata): ByteArray

}