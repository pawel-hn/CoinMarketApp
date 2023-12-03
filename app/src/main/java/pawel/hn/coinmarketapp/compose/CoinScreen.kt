package pawel.hn.coinmarketapp.compose


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.util.CURRENCY_USD
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.util.ValueType
import pawel.hn.coinmarketapp.util.formatPriceAndVolForView
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel

@Composable
fun CoinsBody(paddingValues: PaddingValues) {
    var showFavourites by remember { mutableStateOf(false) }
    val coinsViewModel: CoinsViewModel = hiltViewModel()

    Column(Modifier.padding(paddingValues)) {
        TopCoinBar(
            title = "Coins",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SearchCoinBar(searchQuery = { coinsViewModel.observeCoins(it) })
                ToggleFavourites(favouritesToggle = {
                    coinsViewModel.showFavouritesClick(it)
                    showFavourites = it
                })
            }
        }
        TopRow()
        Body(coinsViewModel, showFavourites)
    }
}

@Composable
fun SearchCoinBar(searchQuery: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier.width(250.dp),
        value = query,
        onValueChange = {
            query = it
            searchQuery(it)
        },
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable {
                        query = ""
                        searchQuery("")
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        },
        keyboardActions = KeyboardActions(onAny = { focusManager.clearFocus() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ToggleFavourites(favouritesToggle: (Boolean) -> Unit) {
    var favourite by remember { mutableStateOf(false) }
    Image(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = Color.Gray, shape = CircleShape)
            .clip(CircleShape)
            .clickable {
                favourite = !favourite
                favouritesToggle(favourite)
            },
        painter = painterResource(id = R.drawable.ic_star_unchecked),
        colorFilter = ColorFilter.tint(if (favourite) ColorStar else Color.White),
        contentDescription = ""
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Body(
    coinsViewModel: CoinsViewModel,
    favouritesToggle: Boolean
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { coinsViewModel.getCoins() }
    )

    val coins by coinsViewModel.coinResult.collectAsState(Resource.Loading())
    val lazyColumnState = rememberLazyListState()
    val scrollToFirstVisible by remember { derivedStateOf { lazyColumnState.firstVisibleItemIndex > 0} }
    val coroutineScope = rememberCoroutineScope()

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
        isRefreshing = coins is Resource.Loading

        CoinsState(
            coins = coins,
            state = lazyColumnState,
            favouriteClick = { id, fav ->
                if (!fav) {
                    coroutineScope.launch {
                        lazyColumnState.animateScrollToItem(0)
                    }
                }
                coinsViewModel.favouriteClick(id, fav)
            }
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
                text = "Lololo...."
            )
        }

        is Resource.Loading -> {
            ShimmerLoading()
        }

        is Resource.Success -> {
            CoinsList(coins = coins.data ?: emptyList(), state = state) { id, fav ->
                favouriteClick(id, fav)
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
            .animateContentSize(
                animationSpec = tween(1000)
            ),
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.small_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.spacedBy(8.dp),
        state = state
    ) {
        items(items = coins, key = { it.coinId }) { coin ->
            CoinItem(coin = coin, onStarClick = { id, fav -> starClick(id, fav) })
        }
    }
}

@Composable
fun ShimmerLoading(repeat: Int = 5) {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(repeat) {
            ShimmerItem()
        }
    }
}

@Composable
fun ShimmerItem() {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .height(75.dp)
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20))
            .clip(RoundedCornerShape(20))
            .shimmerEffect()
    )
}

@Composable
fun CoinItem(
    coin: Coin,
    onStarClick: (Int, Boolean) -> Unit
) {
    var isFavourite by remember { mutableStateOf(coin.favourite) }
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
            StarButtonAnimated(isFavourite) { fav ->
                onStarClick(coin.coinId, fav)
                isFavourite = fav
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

@Preview
@Composable
fun TopRow() {
    Row(
        modifier = Modifier
            .padding(8.dp)
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
            Spacer(modifier = Modifier.width(61.dp))
            Text(text = "Coin", color = Color.Black)

        }
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price", color = Color.Black
            )
            Spacer(modifier = Modifier.width(48.dp))
            Text(
                text = "24h/7d", color = Color.Black
            )
        }
    }
}

@Composable
fun StarButtonAnimated(
    favourite: Boolean,
    starClick: (Boolean) -> Unit
) {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val animatedVertical by animateDpAsState(
        targetValue = if (buttonState == ButtonState.Pressed) (-10).dp else 0.dp,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing), label = ""
    ) {
        buttonState = ButtonState.Idle
    }

    Box(
        modifier = Modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .absoluteOffset { IntOffset(x = 0, y = animatedVertical.roundToPx()) }
                .background(color = Color.Gray, shape = CircleShape)
                .size(24.dp)
                .drawBehind {
                    if (favourite) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(ColorStar, Color.Transparent),
                                radius = 100F
                            ),
                            radius = 100F,
                            alpha = 0.3F
                        )
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        starClick(!favourite)
                        buttonState = ButtonState.Pressed
                    }
                ),
            painter = painterResource(id = R.drawable.ic_star_unchecked),
            colorFilter = ColorFilter.tint(if (favourite) ColorStar else Color.White),
            contentDescription = ""
        )
    }

}

enum class ButtonState { Idle, Pressed }


val CoinItemColor = Color(0xFCFFFFFF)
val ColorStar = Color(0xFFCFFDB6)

