package pawel.hn.coinmarketapp.coinsList

sealed interface CoinsUIState {
    class Loaded<T>(data: T) : CoinsUIState
    class Error<T>(errorMessage: String) : CoinsUIState
    object Loading :CoinsUIState
}