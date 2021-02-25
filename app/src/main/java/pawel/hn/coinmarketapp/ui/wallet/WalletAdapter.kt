package pawel.hn.coinmarketapp.ui.wallet

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.databinding.ItemCoinWalletBinding

class WalletAdapter
    : ListAdapter<Wallet, WalletAdapter.WalletViewHolder>(WalletDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        Log.d(TAG,"onCreateViewHolder, parent: $parent")
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
                Log.d(TAG,"bind: $coin")
                binding.apply {
                    textViewNamePortfolio.text = coin.name
                    textViewVolume.text = coin.volume
                    textViewPrice.text = coin.price
                    textViewTotal.text = coin.total
                }
            }
    }


    class WalletDiffCallBack : DiffUtil.ItemCallback<Wallet>() {
        override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
           return oldItem.name == newItem.name
        }
    }
}
