package pawel.hn.coinmarketapp.compoe


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel


@Composable
fun MainScreen() {
    Scaffold(
        topBar = {

        },
        bottomBar = {

        },
        content = {

        }
    )
}

@Composable
fun CoinsList(
    coinsViewModel: CoinsViewModel = viewModel()
) {

    val data by coinsViewModel.coinResult.coll

    LazyColumn(content = { data.value }) {
        items {

        }
    }

}

@Composable
fun CoinItem(
    modifier: Modifier = Modifier
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
        Row(verticalAlignment = Alignment.CenterVertically,) {
            Image(
                painter = painterResource(id = R.drawable.ic_star_unchecked), contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Bitcoin", fontWeight = FontWeight.Normal)
                Text(text = "BTC", fontWeight = FontWeight.Thin)
            }
        }

        Text(text = "$ 30,000")
    }
}


@Composable
@Preview
fun CoinItemPreview() {
    CoinItem()
}