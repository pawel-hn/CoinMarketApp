package pawel.hn.coinmarketapp.ui.portfolio

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.database.Wallet
import pawel.hn.coinmarketapp.databinding.ItemCoinWalletBinding

class WalletAdapter(private val list: List<Wallet>) : RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        Log.d(TAG,"onCreateViewHolder, parent: $parent")
        val binding = ItemCoinWalletBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
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
}
