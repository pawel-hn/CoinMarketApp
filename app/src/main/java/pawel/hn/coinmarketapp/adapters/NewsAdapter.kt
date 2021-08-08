package pawel.hn.coinmarketapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prof.rssparser.Article
import pawel.hn.coinmarketapp.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(val list: List<Article>, val onCLick: (String) -> Unit) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent,false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {

            val dateSourceString = article.pubDate
            val sourceSdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            if (dateSourceString != null) {
                val date = sourceSdf.parse(dateSourceString)
                if ( date != null) {
                    val textViewDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    binding.textViewNewsDate.text = textViewDateFormat.format(date)
                }
            }
            binding.textViewNewsTitle.text = article.title
            binding.textViewNewsCategories.text = article.categories.joinToString(limit = 5)
            itemView.setOnClickListener { onCLick(article.link!!) }

            Glide.with(itemView)
                .load(article.image)
                .centerCrop()
                .into(binding.imageViewNews)

        }
    }
}