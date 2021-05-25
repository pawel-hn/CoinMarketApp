package pawel.hn.coinmarketapp.ui.coins

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pawel.hn.coinmarketapp.getOrAwaitValue
import pawel.hn.coinmarketapp.repository.FakeRepository
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel


class CoinsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var viewModel: CoinsViewModel
    private lateinit var repository: FakeRepository

    @Before
    fun setup() {

        repository = FakeRepository()
        viewModel = CoinsViewModel(repository)
    }

    @Test
    fun refreshData() {
        viewModel.refreshData()
        val list = repository.coinsRepository.getOrAwaitValue()

        assertThat(list).isNotEmpty()
    }
}