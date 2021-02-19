package pawel.hn.coinmarketapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import pawel.hn.coinmarketapp.databinding.ActivityMainBinding


const val TAG = "PHN"
class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


       val binding = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment_container)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }
}