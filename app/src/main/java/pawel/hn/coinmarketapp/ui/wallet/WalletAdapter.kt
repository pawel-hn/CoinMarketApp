package pawel.hn.coinmarketapp.ui.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.databinding.ItemCoinWalletBinding
import pawel.hn.coinmarketapp.util.formatter
import pawel.hn.coinmarketapp.util.numberUtil
import pawel.hn.coinmarketapp.util.showLog

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
                    textViewVolume.text = numberUtil.format(coin.volume)
                    textViewPrice.text = formatter.format(coin.price)
                    textViewTotal.text = formatter.format(coin.total)

                    root.setOnClickListener {
                        showLog(coin.toString())
                    }


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
