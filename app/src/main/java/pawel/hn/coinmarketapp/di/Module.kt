package pawel.hn.coinmarketapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.data.CoinsData
import pawel.hn.coinmarketapp.data.RemoteData
import pawel.hn.coinmarketapp.data.WalletData
import pawel.hn.coinmarketapp.database.CoinDao
import pawel.hn.coinmarketapp.database.CoinDatabase
import pawel.hn.coinmarketapp.database.WalletDao
import pawel.hn.coinmarketapp.repository.CoinsRepository
import pawel.hn.coinmarketapp.util.BASE_URL_COINS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_COINS)
        .addConverterFactory(GsonConverterFactory.create())
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
        CoinsRepository(coinsData, walletData, remoteData)

    @Provides
    @Singleton
    fun provideCoinDao(database: CoinDatabase): CoinDao = database.coinDao

    @Provides
    @Singleton
    fun provideWalletDao(database: CoinDatabase): WalletDao = database.walletDao

}