package pawel.hn.coinmarketapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pawel.hn.coinmarketapp.TAG
import pawel.hn.coinmarketapp.api.CoinApi
import pawel.hn.coinmarketapp.api.CoinsApi
import pawel.hn.coinmarketapp.database.Coin
import pawel.hn.coinmarketapp.api.Repository
import pawel.hn.coinmarketapp.toCoinsWithCheckBox
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.Exception

class CoinsViewModel : ViewModel() {

    private val _coinsList = MutableLiveData<List<Coin>>()
    val coinsList: LiveData<List<Coin>>
        get() = _coinsList

    private var listCoinsData = emptyList<CoinApi.Data>()

    init {
        response()
    }

    private fun coinsDataListToCoinsWithCheckBox(response: CoinApi) {
        _coinsList.value = response.data.map{
        it.toCoinsWithCheckBox()
        }
    }



    fun response() {
        Log.d(TAG, "response called")

        viewModelScope.launch {
            try {
                val response = CoinsApi.retrofitService.getLatestQuotes(1, 50, "USD")
                coinsDataListToCoinsWithCheckBox(response)
            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }
        }
    }

    fun coinFavourite(checked: Boolean) {

    }


}