package pawel.hn.coinmarketapp.coinsList.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.coinsList.CoinsViewModel
import pawel.hn.coinmarketapp.compose.TopCoinBar
import pawel.hn.coinmarketapp.compose.shimmerEffect
import pawel.hn.coinmarketapp.util.Resource

@Composable
fun CoinScreen() {
    val coinsViewModel: CoinsViewModel = hiltViewModel()
    val showFavourites by coinsViewModel.showFavourites.collectAsState()
    val searchQuery by coinsViewModel.query.collectAsState()
    val state by coinsViewModel.state.collectAsState()

    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val scrollToTop = {
        coroutineScope.launch {
            lazyColumnState.animateScrollToItem(0)
        }
    }

    Column {
        TopCoinBar(
            title = "Coins",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SearchCoinBar(
                    query = searchQuery,
                    searchQueryChange = {
                        coinsViewModel.queryChange(it)
                        if (it.isEmpty()) scrollToTop.invoke()

                    },
                    onSearchClear = {
                        scrollToTop.invoke()
                    }
                )
                ToggleFavourites(
                    showFavourites,
                    favouritesToggle =  coinsViewModel::showFavouritesClick
                )
            }
        }
        TopRow()
        CoinsBody(
            state = state,
            lazyColumnState = lazyColumnState,
            coroutineScope = coroutineScope,
            favouritesToggle = showFavourites,
            onRefreshClick = coinsViewModel::getCoins,
            coinItemFavouriteClick = coinsViewModel::favouriteClick
        )
    }
}

@Composable
fun SearchCoinBar(
    query: String,
    searchQueryChange: (String) -> Unit,
    onSearchClear: () -> Unit,
) {

    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier.width(250.dp),
        value = query,
        onValueChange = {
            searchQueryChange(it)
        },
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable {
                        searchQueryChange("")
                        focusManager.clearFocus()
                        onSearchClear()
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
fun ToggleFavourites(
    favourite: Boolean,
    favouritesToggle: (Boolean) -> Unit
) {
    Image(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = Color.Gray, shape = CircleShape)
            .clip(CircleShape)
            .clickable { favouritesToggle(!favourite) },
        painter = painterResource(id = R.drawable.ic_star_unchecked),
        colorFilter = ColorFilter.tint(if (favourite) ColorStar else Color.White),
        contentDescription = null
    )
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