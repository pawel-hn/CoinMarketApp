package pawel.hn.coinmarketapp.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCoinBar(
    title: String,
    content: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        actions = {
            content()
        },
    )
}