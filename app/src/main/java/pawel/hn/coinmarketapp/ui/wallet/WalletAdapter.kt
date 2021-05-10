package pawel.hn.coinmarketapp.ui.wallet

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.databinding.ItemCoinWalletBinding
import pawel.hn.coinmarketapp.util.*

class WalletAdapter
    : ListAdapter<Wallet, WalletAdapter.WalletViewHolder>(WalletDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding = ItemCoinWalletBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WalletViewHolder(private val binding: ItemCoinWalletBinding)
        : RecyclerView.ViewHolder(binding.root){

            fun bind(coin: Wallet){
                binding.apply {
                    textViewNamePortfolio.text = coin.name

                    textViewVolume.text = formatPriceAndVolForView(coin.volume, ValueType.Crypto)
                    textViewPrice.text = formatPriceAndVolForView(coin.price, ValueType.Fiat)
                    textViewTotal.text = formatPriceAndVolForView(coin.total, ValueType.Fiat)

                    root.setOnClickListener {
                        showLog(coin.toString())
                    }

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

    class WalletDiffCallBack : DiffUtil.ItemCallback<Wallet>() {
        override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
           return oldItem == newItem
        }
    }
}
