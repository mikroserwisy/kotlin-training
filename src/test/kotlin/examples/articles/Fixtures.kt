package examples.articles

import java.nio.file.Paths

internal val basePath = Paths.get("")
internal const val relativeFilesPath = "src\\test\\resources"
internal val absoluteFilesPath = basePath.resolve(relativeFilesPath)
internal const val testFileName = "article"
internal val testFile = FileMetadata(testFileName, "md", "files\\article.md")
internal val notExistingFile = FileMetadata("test", "md", "files\\test.md")
internal val testFiles = listOf(testFile)
internal const val testContent = "Kotlin in action"