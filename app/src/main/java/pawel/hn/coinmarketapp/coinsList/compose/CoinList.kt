package pawel.hn.coinmarketapp.coinsList.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.util.CURRENCY_USD
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.ValueType
import pawel.hn.coinmarketapp.util.formatPriceAndVolForView

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CoinsBody(
    state: Resource<List<Coin>>,
    lazyColumnState: LazyListState,
    coroutineScope: CoroutineScope,
    favouritesToggle: Boolean,
    coinItemFavouriteClick: (coinId: Int, isFavourite: Boolean) -> Unit,
    onRefreshClick: () -> Unit,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefreshClick
    )

    val scrollToFirstVisible by remember { derivedStateOf { lazyColumnState.firstVisibleItemIndex > 0 } }

    LaunchedEffect(favouritesToggle) {
        if (!favouritesToggle) {
            coroutineScope.launch { lazyColumnState.animateScrollToItem(0) }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        isRefreshing = state is Resource.Loading

        CoinsState(
            coins = state,
            state = lazyColumnState,
            favouriteClick = { id, fav -> coinItemFavouriteClick(id, fav) }
        )
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            visible = scrollToFirstVisible
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        lazyColumnState.animateScrollToItem(0)
                    }
                }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "scroll up")
            }
        }
    }
}

@Composable
fun ErrorCoins(
    modifier: Modifier,
    text: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

@Composable
fun CoinsState(
    coins: Resource<List<Coin>>,
    state: LazyListState,
    favouriteClick: (Int, Boolean) -> Unit
) {
    when (coins) {
        is Resource.Error -> {
            ErrorCoins(
                modifier = Modifier.fillMaxSize(),
                text = coins.message ?: "ffs"
            )
        }

        is Resource.Loading -> {
            ShimmerLoading()
        }

        is Resource.Success -> {
            val list = coins.data ?: emptyList()
            if (list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nothing to show")
                }
            } else {
                CoinsList(coins = list, state = state) { id, fav ->
                    favouriteClick(id, fav)
                }
            }
        }
    }
}

@Composable
fun CoinsList(
    coins: List<Coin>,
    state: LazyListState,
    starClick: (Int, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(),
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.small_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = state
    ) {
        items(items = coins, key = { it.coinId }) { coin ->
            CoinItem(coin = coin, onStarClick = { id, fav -> starClick(id, fav) })
        }
    }
}

@Composable
fun CoinItem(
    coin: Coin,
    onStarClick: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
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
            StarButtonAnimated(coin.favourite) { fav ->
                onStarClick(coin.coinId, fav)
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                text = formatPriceAndVolForView(
                    coin.price,
                    ValueType.Fiat,
                    CURRENCY_USD
                ).toString(),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(32.dp))
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