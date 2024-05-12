package pawel.hn.coinmarketapp.coinsList.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pawel.hn.coinmarketapp.compose.shimmerEffect

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