package pawel.hn.coinmarketapp.paging

class DefaultPaginator<E>(
    private inline val isLoading: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Int) -> Result<List<E>>,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess: suspend (items: List<E>, newKey: Int) -> Unit
) : Paginator<E> {

    private var currentKey = 1
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        isLoading(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        val items = result.getOrElse {
            onError(it)
            isLoading(false)
            return
        }
        currentKey += 1
        onSuccess(items, currentKey)
        isLoading(false)
    }

    override fun reset() {
        currentKey = 1
    }
}