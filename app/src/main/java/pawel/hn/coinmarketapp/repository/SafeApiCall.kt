package pawel.hn.coinmarketapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.util.Resource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class SafeApiCall<T, E> {

    suspend fun safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<E> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()
                val data = response.body()
                if (response.isSuccessful && data != null) {
                    val domainData = convertResponseToDomainObject(data)
                    Resource.Success(data = domainData)
                } else {
                    Resource.Error(response.message() ?: "Something went wrong")
                }
            } catch (e: HttpException) {
                Resource.Error(e.message ?: "Something went wrong")
            } catch (e: IOException) {
                Resource.Error(e.message ?: "Please check your network connection")
            } catch (e: Exception) {
                Resource.Error("Something went wrong")
            }
        }
    }

    abstract fun convertResponseToDomainObject(data: T): E
}
