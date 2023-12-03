package pawel.hn.coinmarketapp.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CoinEntityDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: CoinDatabase
    private lateinit var coinDao: CoinDao


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CoinDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        coinDao = database.coinDao
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertCoin_returnsInsertedCoin() = runBlockingTest {
        val testCoinEntity = CoinEntity(1, "testCoin",
            "ct", false, 1.0, 0.02, 0.1, 1)
        coinDao.insert(testCoinEntity)
        val testList = coinDao.observeCoins("").getOrAwaitValue()

        assertThat(testList).contains(testCoinEntity)
    }

    @Test
    fun getCheckedCoins_returnsOnlyFavourite() = runBlockingTest {
        val testCoin1Entity = CoinEntity(1, "testCoin",
            "ct", false, 1.0, 0.02, 0.1, 1)
        val testCoin2Entity = CoinEntity(2, "testCoin",
            "ct", true, 1.0, 0.02, 0.1, 1)
        val testCoin3Entity = CoinEntity(3, "testCoin",
            "ct", true, 1.0, 0.02, 0.1, 1)
        val coinTestListInput = listOf(testCoin1Entity, testCoin2Entity, testCoin3Entity)
        coinDao.insertAll(coinTestListInput)
        val coinTestListOutput = coinDao.getCheckedCoins("").getOrAwaitValue()

        assertThat(coinTestListOutput).containsExactly(testCoin2Entity, testCoin3Entity)
    }

}