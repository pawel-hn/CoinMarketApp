package pawel.hn.coinmarketapp.coinsList.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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