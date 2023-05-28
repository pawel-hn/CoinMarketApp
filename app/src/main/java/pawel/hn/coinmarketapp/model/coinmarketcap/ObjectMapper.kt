package pawel.hn.coinmarketapp.model.coinmarketcap

interface ObjectMapper<T : Any, E : Any> {
    fun convert(value: T): E
}