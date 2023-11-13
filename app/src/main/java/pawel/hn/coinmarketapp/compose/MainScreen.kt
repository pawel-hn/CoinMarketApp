package pawel.hn.coinmarketapp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFEEEEEE),
        bottomBar = {
            BottomBar(navController = navController)
        },
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = BottomNavigationItem.Home.title) {
            composable(route = BottomNavigationItem.Home.title) {
                CoinsBody(paddingValues)
            }
            composable(route = BottomNavigationItem.Wallet.title) {
                WalletScreen(paddingValues)
            }
            composable(route = BottomNavigationItem.News.title) {
                News()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Wallet,
        BottomNavigationItem.News,
    )

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination == item.title
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.title) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unSelectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = Color.Gray
                )
            )
        }
    }
}

sealed class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
) {
    object Home : BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )

    object Wallet : BottomNavigationItem(
        title = "Wallet",
        selectedIcon = Icons.Filled.AddCircle,
        unSelectedIcon = Icons.Outlined.Add
    )

    object News : BottomNavigationItem(
        title = "News",
        selectedIcon = Icons.Filled.DateRange,
        unSelectedIcon = Icons.Outlined.DateRange
    )
}

@Composable
fun Wallet() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray), contentAlignment = Alignment.Center
    ) {
        Text(text = "Wallet")
    }
}

@Composable
fun News() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "News")
    }
}