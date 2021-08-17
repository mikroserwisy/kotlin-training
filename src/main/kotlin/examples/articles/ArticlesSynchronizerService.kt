package examples.articles

class ArticlesSynchronizerService(private val filesRepositoryWriter: FilesRepositoryWriter) : ArticlesSynchronizer {

    override fun synchronize() {
        filesRepositoryWriter.synchronize()
    }

}