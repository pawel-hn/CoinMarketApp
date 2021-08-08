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

        binding.apply {
            lifecycleOwner = this@NewsFragment
            newsViewModel = viewModel
        }

        parser = Parser.Builder().build().also {
            getData(it, requireContext())
        }

        viewModel.rssChannel.observe(viewLifecycleOwner) { channel ->
            if (channel != null) {

                newsAdapter = NewsAdapter(channel.articles) {
                    val action = NewsFragmentDirections.actionNewsFragmentToNewsWebFragment(it)
                    findNavController().navigate(action)
                }

                binding.recyclerViewNews.adapter = newsAdapter
                binding.swipeLayout.isRefreshing = false
                if (channel.title != null) {
                    activity?.title = channel.title
                }
            }
            hideShimmerEffect()
        }

        binding.swipeLayout.setOnRefreshListener {
            getData(parser, requireContext())
        }
    }

    private fun getData(parser: Parser, context: Context) {
        showShimmerEffect()
        viewModel.fetchFeed(parser, context)
    }

    private fun hideShimmerEffect() {
        binding.recyclerViewNews.hideShimmer()
    }

    private fun showShimmerEffect() {
        binding.recyclerViewNews.showShimmer()
    }


}