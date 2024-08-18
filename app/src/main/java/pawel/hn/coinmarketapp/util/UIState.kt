package pawel.hn.coinmarketapp.util

sealed class UIState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loaded<T>(data: T) : UIState<T>(data = data)
    class Error<T>(errorMessage: String) : UIState<T>(message = errorMessage)
    class Loading<T> : UIState<T>()
}