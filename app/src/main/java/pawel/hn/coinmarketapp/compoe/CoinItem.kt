package pawel.hn.coinmarketapp.compoe


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.viewmodels.CoinForView
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel


@Composable
fun MainScreen(
    coinsViewModel: CoinsViewModel = viewModel()
) {
    Log.d("PHN", "mainscreen")
    val data = coinsViewModel.coinResult.collectAsState()


    var isLoadingVisibleRemember by remember { mutableStateOf(data.value is Resource.Loading) }

    LaunchedEffect(data.value) {
        Log.d("PHN", "LaunchedEffect loading: $isLoadingVisibleRemember")
        isLoadingVisibleRemember = data.value is Resource.Loading
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (data.value) {
            is Resource.Error -> ErrorCoins("Error.....")
            is Resource.Loading -> LoadingCoins(isLoadingVisibleRemember)
            is Resource.Success -> {
                val coins = data.value.data
                if (coins.isNullOrEmpty()) {
                    ErrorCoins(text = "No data to display")
                } else {
                    CoinsList(
                        visible = !isLoadingVisibleRemember,
                        coins = coins
                    ) {
                        coinsViewModel.getCoins()
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingCoins(
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
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
fun CoinsList(
    visible: Boolean,
    coins: List<CoinForView>, onClick: () -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items = coins, key = { it.coinId }) {
                CoinItem(coin = it)
            }
            item {
                Button(onClick = onClick) {
                    Text(text = "Refresh")
                }
            }
        }
    }
}

@Composable
fun CoinItem(
    modifier: Modifier = Modifier,
    coin: CoinForView
) {
    Row(
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .background(CoinItemColor, RoundedCornerShape(20))
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(0.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .background(color = Color.Gray, shape = CircleShape)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_star_unchecked),
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(42.dp),
                imageModel = { coin.imageUri },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.FillBounds
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = coin.name, fontWeight = FontWeight.Normal)
                Text(text = coin.symbol, fontWeight = FontWeight.Thin)
            }
        }
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.widthIn(),
                text = coin.price,
                textAlign = TextAlign.End
            )
            Column(

                horizontalAlignment = Alignment.End
            ) {
                val color24h = if (coin.isChange24hUp) Color.Blue else Color.Red
                val color7d = if (coin.isChange7dUp) Color.Blue else Color.Red

                Text(text = coin.change24h, color = color24h)
                Text(text = coin.change7d, color = color7d)
            }
        }
    }
}


val CoinItemColor = Color(0xFCFFFFFF)