package examples.articles

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class LocalFilesRepositoryReaderTests {

    private val sut = LocalFilesRepositoryReader(absoluteFilesPath)

    @Test
    fun given_files_exists_when_get_metadata_with_predicate_then_returns_all_files_metadata_matching_predicate() {
        assertEquals(testFiles, sut.getMetadata { it.name == testFileName })
    }

    @Test
    fun `given files does not exist when get metadata with predicate then returns empty list`() {
        assertEquals(emptyList<FileMetadata>(), sut.getMetadata { it.name == "intro.md" })
    }

    @Test
    fun `given file exists when get bytes then returns bytes from file`() {
        val bytes = sut.getBytes(testFile)
        assertEquals(testContent, String(bytes))
    }

    @Test
    fun `given file does not exist when get bytes then throws exception`() {
        assertThrows(ReadFailedException::class.java) { sut.getBytes(notExistingFile) }
    }

}