package pawel.hn.coinmarketapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pawel.hn.coinmarketapp.api.BASE_URL
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.database.CoinDatabase
import pawel.hn.coinmarketapp.repository.Repository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Module {


    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
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
    fun provideRepository(database: CoinDatabase, coinApi: CoinApi): Repository =
        Repository(database.coinDao, coinApi)

    @Provides
    fun provideCoinDao(database: CoinDatabase) = database.coinDao

}