package pawel.hn.coinmarketapp.util

import kotlinx.coroutines.CoroutineExceptionHandler

val errorHandler = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    showLogN("CoroutineExceptionHandler")
}