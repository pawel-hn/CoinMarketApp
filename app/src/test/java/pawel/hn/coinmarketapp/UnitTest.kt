import com.google.common.truth.Truth.assertThat
import io.mockk.mockkClass
import org.junit.Before
import org.junit.Test
import pawel.hn.coinmarketapp.database.WalletEntity
import pawel.hn.coinmarketapp.repository.Repository
import pawel.hn.coinmarketapp.util.formatPriceChange
import pawel.hn.coinmarketapp.viewmodels.WalletViewModel


class UnitTest {

    private val walletEntityCoin1 = WalletEntity(1,1,"Bitcoin",
        "BTC",1.0, 2.0, 2.0, 0 )

    private val walletEntityCoin2 = WalletEntity(2,1,"Bitcoin",
        "BTC",1.0, 2.0, 2.0, 1 )

    private val walletEntityCoin3 = WalletEntity(3,1,"Bitcoin",
        "BTC",1.0, 2.0, 2.0, 2 )

    private val walletEntityCoin4 = WalletEntity(3,2,"Ethereum",
        "ETH",1.0, 2.0, 2.0, 2 )

    private val walletEntityCoin5 = WalletEntity(3,2,"Ethereum",
        "ETH",1.0, 2.0, 2.0, 2 )

    private val list = listOf(walletEntityCoin1, walletEntityCoin2, walletEntityCoin3, walletEntityCoin4, walletEntityCoin5)

    private lateinit var totalList: List<WalletEntity>

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