package pawel.hn.coinmarketapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.prof.rssparser.Parser
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.adapters.NewsAdapter
import pawel.hn.coinmarketapp.databinding.FragmentNewsBinding
import pawel.hn.coinmarketapp.viewmodels.NewsViewModel

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var parser: Parser
    private lateinit var binding: FragmentNewsBinding

    private val viewModel: NewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewsBinding.bind(view)

        binding.lifecycleOwner = this
        binding.newsViewModel = viewModel

        parser = Parser.Builder().build().also {

        }

        viewModel.rssChannel.observe(viewLifecycleOwner) { channel ->
            if (channel != null) {

                newsAdapter = NewsAdapter(channel.articles) {
                    val action = NewsFragmentDirections.actionNewsFragmentToNewsWebFragment(it)
                    findNavController().navigate(action)
                }

            }
        }

    }

}