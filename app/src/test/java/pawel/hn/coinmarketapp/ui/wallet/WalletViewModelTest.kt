package pawel.hn.coinmarketapp.ui.wallet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.viewmodels.WalletViewModel


class WalletViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var viewModel: WalletViewModel
    private lateinit var walletList: List<Wallet>

    @Before
    fun setup() {

    }

    @Test
    fun `test calculate total balance`(){
        val total = viewModel.calculateTotalBalance(walletList)
        assertThat(total).isEqualTo("15 USD")
    }



}