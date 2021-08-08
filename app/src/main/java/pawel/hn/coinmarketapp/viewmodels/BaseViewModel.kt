package pawel.hn.coinmarketapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pawel.hn.coinmarketapp.repository.CoinsRepository
import pawel.hn.coinmarketapp.util.toMutableLiveData

open class BaseViewModel(private val coinsRepository: CoinsRepository) : ViewModel() {

    val eventErrorResponse: LiveData<Boolean> = MutableLiveData()
    val eventProgressBar: LiveData<Boolean> = MutableLiveData()

    fun refreshData(currency: String) {
        eventProgressBar.toMutableLiveData().value = true

        viewModelScope.launch(Dispatchers.IO) {
            coinsRepository.getCoinsData(currency)

            withContext(Dispatchers.Main){
                eventProgressBar.toMutableLiveData().value = false
                eventErrorResponse.toMutableLiveData().value = coinsRepository.responseError
            }
        }
    }

}