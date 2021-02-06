package pawel.hn.coinmarketapp.ui

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.ItemCoinsBinding
import java.text.DecimalFormat

class CoinsAdapter(private val list: List<Coin>) : RecyclerView.Adapter<CoinsAdapter.CoinsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        val binding = ItemCoinsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        val coin = list[position]
        holder.bind(coin)
    }

    override fun getItemCount(): Int {
        Log.d("PHN", "getItemcount ${list.size}")
        return list.size
    }

    class CoinsViewHolder(private val binding: ItemCoinsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: Coin) {
            val percentage = coin.change24h.toString()
            val percentageForView = if (percentage.substring(percentage.indexOf('.')).length > 2) {
                percentage.substring(0, percentage.indexOf('.') + 3)
            } else {
                percentage
            }
            val formatter = DecimalFormat("#,###.00")

            binding.apply {
                textViewName.text = coin.name
                textViewSymbol.text = coin.symbol
                textViewUsd.text = formatter.format(coin.price).toString()

                if(coin.symbol == "XTZ") {
                    Log.d(TAG, "tezos:" + coin.change24h)
                }
                if (coin.change24h < 0.000000000) {
                    textView24hChange.setTextColor(Color.RED)
                }
                textView24hChange.text = percentageForView + " %"

            }
        }
    }
}