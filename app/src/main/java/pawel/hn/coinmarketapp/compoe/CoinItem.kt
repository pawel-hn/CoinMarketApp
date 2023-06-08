package pawel.hn.coinmarketapp.compoe


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel


@Composable
fun MainScreen(
    coinsViewModel: CoinsViewModel = viewModel()
) {
    val data = coinsViewModel.coinResult.collectAsState()

    when (data.value) {
        is Resource.Error -> ErrorCoins("Error.....")
        is Resource.Loading -> LoadingCoins()
        is Resource.Success -> {
            val coins = data.value.data

            if (coins.isNullOrEmpty()) {
                ErrorCoins(text = "No data to display")
            } else {
                CoinsList(coins = coins)
            }
        }
    }

}

@Composable
fun LoadingCoins() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorCoins(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

@Composable
fun CoinsList(coins: List<Coin>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        items(items = coins, key = { it.coinId }) {
            CoinItem(coin = it)
        }
    }
}

@Composable
fun CoinItem(
    modifier: Modifier = Modifier,
    coin: Coin
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(20))
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_star_unchecked),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = coin.name, fontWeight = FontWeight.Normal)
                Text(text = coin.symbol, fontWeight = FontWeight.Thin)
            }
        }

        Text(text = "$ ${coin.price}")
    }
}
