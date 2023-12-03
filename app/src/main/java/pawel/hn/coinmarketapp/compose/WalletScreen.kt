package pawel.hn.coinmarketapp.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.domain.Coin
import pawel.hn.coinmarketapp.util.Resource
import pawel.hn.coinmarketapp.viewmodels.AddCoinViewModel


@Composable
fun WalletScreen(paddingValues: PaddingValues) {

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainContent(paddingValues = paddingValues) {
            showDialog = true
        }
        if (showDialog) {
            AddItemDialog { showDialog = false }
        }
    }

}

@Composable
fun MainContent(
    paddingValues: PaddingValues,
    addItem: () -> Unit
) {
    var items by remember { mutableIntStateOf(3) }
    var isChartVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopCoinBar(title = "Wallet")
        AnimatedVisibility(visible = isChartVisible) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Green)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (isChartVisible && dragAmount < -20) {
                            isChartVisible = false
                        } else if (!isChartVisible && dragAmount > 20) {
                            isChartVisible = true
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = "Total balance:")
                    Text(text = "$ 100,000")
                }
                TopRow()
                repeat(items) {
                    WalletItem()
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp),
                onClick = { addItem() }) {
                Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "")
            }
        }
    }
}


@Composable
fun WalletItem() {
    AnimatedVisibility(
        visible = true
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .background(CoinItemColor, RoundedCornerShape(20))
                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = R.drawable.in_one_coin),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(42.dp),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Bicoin", fontWeight = FontWeight.Normal)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "0.50000")
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .width(100.dp), color = Color.Gray
                )
                Text(text = "$ 30,000")
            }
            Text(text = "$ 36,000")
        }
    }
}


@Composable
fun AddItemDialog(
    onDismiss: () -> Unit
) {

    val viewModel: AddCoinViewModel = hiltViewModel()
    val coins by viewModel.coinList.collectAsState()
    var amount by remember { mutableStateOf("") }
    val isAddButtonEnabled by viewModel.isAddButtonEnabled.collectAsState()

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(text = "Choose coin:")
                when (coins) {
                    is Resource.Error -> Text(text = "No data")
                    is Resource.Loading -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerEffect()
                    )

                    is Resource.Success -> CoinsDropDown(
                        coins = coins.data ?: emptyList(),
                        onCoinSelected = { viewModel.selectedCoin(it) },
                        searchInput = { viewModel.observeCoins(it) }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "How many:")
                TextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        viewModel.inputAmount(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { onDismiss() }
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(

                        onClick = { },
                        enabled = isAddButtonEnabled
                    ) {
                        Text(text = "Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinsDropDown(
    coins: List<Coin>,
    onCoinSelected: (Coin) -> Unit,
    searchInput: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {
        Column {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = selectedText,
                onValueChange = {
                    searchInput(it)
                    selectedText = it
                },
                keyboardActions = KeyboardActions(onAny = { focusManager.clearFocus() }),
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                modifier = Modifier.height(200.dp),
                onDismissRequest = {  }
            ) {
                coins.forEach { coin ->
                    DropdownMenuItem(
                        text = { Text(text = coin.name) },
                        onClick = {
                            onCoinSelected(coin)
                            selectedText = coin.name
                            focusManager.clearFocus()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}