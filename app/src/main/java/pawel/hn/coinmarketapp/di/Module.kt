package pawel.hn.coinmarketapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.api.HeaderInterceptor
import pawel.hn.coinmarketapp.data.CoinsData
import pawel.hn.coinmarketapp.data.RemoteData
import pawel.hn.coinmarketapp.data.WalletData
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.CoinDatabase
import pawel.hn.coinmarketapp.database.WalletDao
import pawel.hn.coinmarketapp.model.coinmarketcap.ApiResponseMapper
import pawel.hn.coinmarketapp.repository.CoinRepository
import pawel.hn.coinmarketapp.repository.CoinRepositoryImpl
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.API_HEADER
import pawel.hn.coinmarketapp.util.API_KEY
import pawel.hn.coinmarketapp.util.BASE_URL_COINS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_COINS)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideCoinApi(retrofit: Retrofit): CoinApi = retrofit.create(CoinApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CoinDatabase =
        CoinDatabase.getDataBase(context)

    @Provides
    @Singleton
    fun provideRepository(coinsData: CoinsData, walletData: WalletData, remoteData: RemoteData) =
        Repository(coinsData, walletData, remoteData)

    @Provides
    @Singleton
    fun provideCoinDao(database: CoinDatabase): CoinDao = database.coinDao

    @Provides
    @Singleton
    fun provideWalletDao(database: CoinDatabase): WalletDao = database.walletDao


    @Provides
    fun provideCoinRepository(coinApi: CoinApi): CoinRepository = CoinRepositoryImpl(coinApi, ApiResponseMapper())

}