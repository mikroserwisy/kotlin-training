package examples.articles

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.nio.file.Path

class GitFilesRepositoryWriter(
    private val localPath: Path,
    private val sourceUri: String,
    username: String = "",
    password: String = ""
) : FilesRepositoryWriter {

    private val credentials = UsernamePasswordCredentialsProvider(username, password)

    override fun synchronize() {
        try {
            pullChanges()
        } catch (exception: SynchronizationFailedException) {
            cloneRepository()
        }
    }

    private fun pullChanges() {
        try {
            Git.open(localPath.toFile())
                .pull()
                .setCredentialsProvider(credentials)
                .call()
        } catch (exception: InvalidRemoteException) {
            throw InvalidUriRemoteException()
        } catch (exception: Exception) {
            throw SynchronizationFailedException()
        }
    }

    private fun cloneRepository() {
        try {
            Git.cloneRepository()
                .setURI(sourceUri)
                .setDirectory(localPath.toFile())
                .setCredentialsProvider(credentials)
                .call()
        } catch (exception: InvalidRemoteException) {
            throw InvalidUriRemoteException()
        } catch (exception: Exception) {
            throw SynchronizationFailedException()
        }
    }

    override fun deleteALl() {
        localPath.toFile().deleteRecursively()
    }

}