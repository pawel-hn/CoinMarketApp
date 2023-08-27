package pawel.hn.coinmarketapp.paging

interface Paginator<E> {
    suspend fun loadNextItems()
    fun reset()
}