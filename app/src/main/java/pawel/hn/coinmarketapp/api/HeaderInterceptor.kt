package pawel.hn.coinmarketapp.api

import okhttp3.Interceptor
import okhttp3.Response
import pawel.hn.coinmarketapp.util.API_HEADER
import pawel.hn.coinmarketapp.util.API_KEY

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val new = request.newBuilder().addHeader(API_HEADER, API_KEY)
        return chain.proceed(new.build())
    }
}