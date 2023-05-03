package pawel.hn.coinmarketapp.adapters

import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.databinding.ItemCoinsBinding
import pawel.hn.coinmarketapp.util.*

class CoinsAdapter(val onClick: (Coin, Boolean) -> Unit) :
    ListAdapter<Coin, CoinsAdapter.CoinsViewHolder>(CoinDiffCallback()) {

    private var currency: String = CURRENCY_USD

    fun setCurrency(ccy: String) {
        currency = ccy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsAdapter.CoinsViewHolder {
        val binding = ItemCoinsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinsAdapter.CoinsViewHolder, position: Int) {
        val coin = getItem(position)
        holder.bind(coin)
    }


    inner class CoinsViewHolder(private val binding: ItemCoinsBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(coin: Coin) {
            val change7d = formatPriceChange(coin.change7d)
            val change24h = formatPriceChange(coin.change24h)


            binding.apply {
                textViewName.text = coin.name
                textViewSymbol.text = coin.symbol

                val price = formatPriceAndVolForView(coin.price, ValueType.Fiat, currency)

                val spannablePrice = SpannableString(price)
                val dollarColor = ForegroundColorSpan(
                    ContextCompat.getColor(root.context, R.color.mediumGrey)
                )
                spannablePrice.setSpan(dollarColor, 0,2, Spanned.SPAN_COMPOSING)

                textViewPriceCoin.text = formatPriceAndVolForView(coin.price, ValueType.Fiat, currency)
                checkboxFav.isChecked = coin.favourite

                checkboxFav.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                       onClick(coin, checkboxFav.isChecked)
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