package pawel.hn.coinmarketapp.ui.addeditdialog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.repository.FakeRepository
import pawel.hn.coinmarketapp.viewmodels.AddCoinViewModel

@SmallTest
class AddCoinViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AddCoinViewModel
    private lateinit var repository: FakeRepository


    @Before
    fun setup() {
        repository = FakeRepository()
        viewModel = AddCoinViewModel(repository)

    }

    @Test
    fun `test creating coin Wallet`() {
        val coinCreate = viewModel.createWalletCoin("testCoin3",2.0)
        val coinExpected = Wallet("testCoin3", 2.0, 2.0, 4.0  )

        assertThat(coinCreate).isEqualTo(coinExpected)
    }

    @Test
    fun `test inserting and getting coin from Wallet`() {
        val coinWallet = Wallet("testCoin4", 3.0, 2.0, 6.0  )
        viewModel.addToWallet(coinWallet)

        assertThat(repository.walletRepository.value).contains(coinWallet)
    }

    @Test
    fun `test inserting without name return error`() {
        val coinWallet = Wallet("", 3.0, 2.0, 6.0  )
        viewModel.addToWallet(coinWallet)

        assertThat(repository.walletRepository.value).doesNotContain(coinWallet)
    }

    @Test
    fun `test size of coin names list is same as size of coins list`(){
        val listOfName = viewModel.coinsNamesList()

        assertThat(repository.coinsRepository.value?.size).isEqualTo(listOfName.size)
    }


}