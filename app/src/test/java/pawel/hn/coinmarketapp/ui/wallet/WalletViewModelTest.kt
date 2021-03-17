package pawel.hn.coinmarketapp.ui.wallet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.getOrAwaitValue
import pawel.hn.coinmarketapp.repository.FakeRepository

class WalletViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var viewModel: WalletViewModel
    private lateinit var repository: FakeRepository
    private lateinit var walletList: List<Wallet>

    @Before
    fun setup() {

        repository = FakeRepository()
        viewModel = WalletViewModel(repository)
        walletList = repository.walletRepository.getOrAwaitValue()
    }

    @Test
    fun `test total balance`(){
        val total = viewModel.calculateTotal(walletList)
        assertThat(total).isEqualTo("15 USD")
    }



}