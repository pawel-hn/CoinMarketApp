package pawel.hn.coinmarketapp.ui.coins

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.ItemCoinsBinding
import pawel.hn.coinmarketapp.util.numberUtil

class CoinsAdapter(private val listener: CoinsOnClick) :
    ListAdapter<Coin, CoinsAdapter.CoinsViewHolder>(CoinDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        val binding = ItemCoinsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        val coin = getItem(position)
        holder.bind(coin)
    }

    interface CoinsOnClick {
        fun onCheckBoxClicked(coin: Coin, isChecked: Boolean)
        fun onCoinClicked(coin: Coin)
    }

    inner class CoinsViewHolder(private val binding: ItemCoinsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onCoinClicked(getItem(adapterPosition))
            }
        }

        fun bind(coin: Coin) {
            val percentage = coin.change24h.toString()
            val percentageForView = if (percentage.substring(percentage.indexOf('.')).length > 2) {
                percentage.substring(0, percentage.indexOf('.') + 3)
            } else {
                percentage
            }


            binding.apply {
                textViewName.text = coin.name
                textViewSymbol.text = coin.symbol
                textViewUsd.text = numberUtil.format(coin.price).toString()
                checkboxFav.isChecked = coin.favourite

                checkboxFav.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onCheckBoxClicked(getItem(adapterPosition), checkboxFav.isChecked)
                    }
                }

                if (coin.change24h < 0.00) {
                    textView24hChange.setTextColor(Color.RED)
                } else {
                    textView24hChange
                        .setTextColor(
                            ContextCompat.getColor(
                                binding.root.context, R.color.design_default_color_primary_variant
                            )
                        )
                }
                textView24hChange.text = "$percentageForView %"

            }
        }
    }

    class CoinDiffCallback : DiffUtil.ItemCallback<Coin>() {
        override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
            return oldItem.coinId == newItem.coinId
        }

        override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
            return oldItem == newItem
        }
    }


}