package pawel.hn.coinmarketapp.util

import pawel.hn.coinmarketapp.model.coinmarketcap.ObjectMapper
import retrofit2.HttpException
import retrofit2.Response

 suspend fun <T : Any, E : Any> handleApi(
     mapper: ObjectMapper<T, E>,
     execute: suspend () -> Response<T>,
): Resource<E> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val data = mapper.convert(body)
            Resource.Success(data)
        } else {
            Resource.Error("Something went wrong")
        }
    } catch (e: HttpException) {
        e.printStackTrace()
        Resource.Error("Something wrong with connection")
    }
    catch (e: Throwable) {
        e.printStackTrace()
        Resource.Error(e.message ?: "Unknown error")
    }
}