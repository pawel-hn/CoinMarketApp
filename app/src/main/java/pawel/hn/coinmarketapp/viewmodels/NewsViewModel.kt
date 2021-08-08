package pawel.hn.coinmarketapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.util.BASE_URL_NEWS
import pawel.hn.coinmarketapp.util.hasInternetConnection
import pawel.hn.coinmarketapp.util.showLog
import pawel.hn.coinmarketapp.util.toMutableLiveData

class NewsViewModel : ViewModel() {

    val rssChannel: LiveData<Channel> = MutableLiveData()
    val eventError: LiveData<Boolean> = MutableLiveData()

    fun fetchFeed(parser: Parser, context: Context) {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                runCatching {
                    parser.getChannel(BASE_URL_NEWS)
                }.onSuccess {
                    if (it.articles.isNotEmpty()) {
                        rssChannel.toMutableLiveData().value = it
                        eventError.toMutableLiveData().value = false
                    } else {
                        eventError.toMutableLiveData().value = true
                    }
                }.onFailure {
                    showLog(it.message!!)
                    rssChannel.toMutableLiveData().postValue(
                        Channel(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            mutableListOf()
                        )
                    )
                }
            }
        } else {
            eventError.toMutableLiveData().value = true
        }
    }
}