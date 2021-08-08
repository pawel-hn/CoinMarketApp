package pawel.hn.coinmarketapp.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentNewsWebviewBinding


class NewsWebFragment : Fragment(R.layout.fragment_news_webview) {

    lateinit var binding: FragmentNewsWebviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewsWebviewBinding.bind(view)
        val url = NewsWebFragmentArgs.fromBundle(requireArguments()).url

        binding.apply {
            newsWebView.webViewClient = WebViewClient()
            newsWebView.loadUrl(url)
        }
    }
}