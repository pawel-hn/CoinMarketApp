package pawel.hn.coinmarketapp.ui.coins

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.ItemCoinsBinding
import pawel.hn.coinmarketapp.util.*

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
            val change7d = formatPriceChange(coin.change7d)
            val change24h = formatPriceChange(coin.change24h)


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

                setPriceChangeColor(textView24hChange, coin.change24h)
                setPriceChangeColor(textView7dChange, coin.change7d)

                textView24hChange.text = change24h
                textView7dChange.text = change7d

                val imageUri = Uri.parse(LOGO_URL).buildUpon()
                    .appendPath(LOGO_SIZE_PX)
                    .appendPath(coin.coinId.toString() + LOGO_FILE_TYPE)
                    .build()

                Glide.with(itemView)
                    .load(imageUri)
                    .centerCrop()
                    .transform(CircleCrop())
                    .into(coinLogo)
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