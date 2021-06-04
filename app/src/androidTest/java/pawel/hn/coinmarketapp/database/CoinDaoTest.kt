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
class CoinDaoTest {

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
        val testCoin = Coin(1, "testCoin",
            "ct", false, 1.0, 0.02, 0.1, 1)
        coinDao.insert(testCoin)
        val testList = coinDao.getAllCoins("").getOrAwaitValue()

        assertThat(testList).contains(testCoin)
    }

    @Test
    fun getCheckedCoins_returnsOnlyFavourite() = runBlockingTest {
        val testCoin1 = Coin(1, "testCoin",
            "ct", false, 1.0, 0.02, 0.1, 1)
        val testCoin2 = Coin(2, "testCoin",
            "ct", true, 1.0, 0.02, 0.1, 1)
        val testCoin3 = Coin(3, "testCoin",
            "ct", true, 1.0, 0.02, 0.1, 1)
        val coinTestListInput = listOf(testCoin1, testCoin2, testCoin3)
        coinDao.insertAll(coinTestListInput)
        val coinTestListOutput = coinDao.getCheckedCoins("").getOrAwaitValue()

        assertThat(coinTestListOutput).containsExactly(testCoin2, testCoin3)
    }

}