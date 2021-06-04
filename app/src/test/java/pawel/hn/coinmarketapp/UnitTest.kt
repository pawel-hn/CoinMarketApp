import com.google.common.truth.Truth.assertThat
import io.mockk.mockkClass
import org.junit.Before
import org.junit.Test
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.formatPriceChange
import pawel.hn.coinmarketapp.viewmodels.WalletViewModel


class UnitTest {

    private val walletCoin1 = Wallet(1,1,"Bitcoin",
        "BTC",1.0, 2.0, 2.0, 0 )

    private val walletCoin2 = Wallet(2,1,"Bitcoin",
        "BTC",1.0, 2.0, 2.0, 1 )

    private val walletCoin3 = Wallet(3,1,"Bitcoin",
        "BTC",1.0, 2.0, 2.0, 2 )

    private val walletCoin4 = Wallet(3,2,"Ethereum",
        "ETH",1.0, 2.0, 2.0, 2 )

    private val walletCoin5 = Wallet(3,2,"Ethereum",
        "ETH",1.0, 2.0, 2.0, 2 )

    private val list = listOf(walletCoin1, walletCoin2, walletCoin3, walletCoin4, walletCoin5)

    private lateinit var totalList: List<Wallet>

    private lateinit var repo: Repository
    private lateinit var walletViewModel: WalletViewModel

    @Before
    fun setUp() {
        repo = mockkClass(Repository::class, relaxed = true)
        walletViewModel = WalletViewModel(repo)
        totalList = walletViewModel.totalWallet(list)
    }


    @Test
    fun `test that total wallet, has expected size`() {
        assertThat(totalList.size).isEqualTo(2)
    }


    @Test
    fun `test that total wallet, does not have duplicated same coins`() {
        val btc = totalList.filter { it.symbol == "BTC" }
        assertThat(btc.size).isEqualTo(1)
    }

    @Test
    fun `test that total wallet, has correct total for particular coin`() {
        val btc = totalList.find { it.symbol == "BTC"}
        assertThat(btc?.total).isEqualTo(6)
    }

    @Test
    fun `test total wallet balances`(){
        val walletsBalance = walletViewModel.calculateTotalBalance(list)
        val totalBalance  = walletViewModel.calculateTotalBalance(totalList)
        assertThat(walletsBalance).isEqualTo(totalBalance)
    }

    @Test
    fun `test formatPriceChange from utils`() {
        val change = formatPriceChange(12.3456)
        assertThat(change).isEqualTo("12.34 %")
    }


}