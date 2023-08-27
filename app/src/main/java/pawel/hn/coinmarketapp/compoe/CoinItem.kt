package pawel.hn.coinmarketapp.compoe


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
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
import pawel.hn.coinmarketapp.viewmodels.CoinForView
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel
import pawel.hn.coinmarketapp.viewmodels.ListState

@Composable
fun MainScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFEEEEEE),
        topBar = { TopBar() }
    ) { paddingValues ->

        Body(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(text = "Coins")
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFEEEEEE)
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Body(
    modifier: Modifier,
    coinsViewModel: CoinsViewModel = viewModel()
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { coinsViewModel.resetPaging() }
    )
    val lazyColumnListState = rememberLazyListState()

    val coins = coinsViewModel.coins
    val canPaginate = coinsViewModel.canPaginate

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CoinsList(
            state = coinsViewModel.listState,
            coins = coins,
            lazyColumnState = lazyColumnListState,
            canPaginate = canPaginate
        ) {
            coinsViewModel.getNews()
        }

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
    state: ListState,
    coins: List<CoinForView> = emptyList(),
    lazyColumnState: LazyListState,
    canPaginate: Boolean,
    loadData: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyColumnState
    ) {
        items(coins.size) { i ->
            val coin = coins[i]
            if (i >= coins.size - 1 && canPaginate && state == ListState.IDLE) {
                loadData()
            }
            CoinItem(coin = coin)
        }
        item(key = state) {
            when (state) {
                ListState.IDLE -> {}
                ListState.LOADING -> { ShimmerLoading() }
                ListState.PAGINATING -> { PagingLoadingRow() }
                ListState.ERROR -> { ErrorCoins(text = "Lololo....") }
                ListState.PAGINATION_EXHAUST -> {}
            }
        }
    }
}

@Composable
fun PagingLoadingRow() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Pagination Loading")

        CircularProgressIndicator(color = Color.Black)
    }
}

@Composable
fun ShimmerLoading() {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(5) {
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
            .shimmerEffect()
    )
}

@Composable
fun CoinItem(
    coin: CoinForView,
) {
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
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
            Image(
                modifier = Modifier
                    .background(color = Color.Gray, shape = CircleShape)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_star_unchecked),
                contentDescription = ""
            )
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
                text = coin.price,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(8.dp))
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