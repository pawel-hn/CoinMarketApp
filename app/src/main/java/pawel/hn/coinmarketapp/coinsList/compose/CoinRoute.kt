package pawel.hn.coinmarketapp.coinsList.compose


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CoinsRoute(paddingValues: PaddingValues, ) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) {
        CoinScreen()
    }
}


enum class ButtonState { Idle, Pressed }

val CoinItemColor = Color(0xFCFFFFFF)
val ColorStar = Color(0xFFCFFDB6)

