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

class NewsViewModel : ViewModel() {

    private val _rssChannel = MutableLiveData<Channel>()
    val rssChannel: LiveData<Channel>
        get() = _rssChannel

    private val _eventError = MutableLiveData<Boolean>()
    val eventError: LiveData<Boolean>
        get() = _eventError


    fun fetchFeed(parser: Parser, context: Context) {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    val channel = parser.getChannel(BASE_URL_NEWS)
                    if (channel.articles.isNotEmpty()){
                        _rssChannel.value = channel
                        _eventError.value = false
                    } else {
                        _eventError.value = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _rssChannel.postValue(
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
            _eventError.value = true
        }
    }
}