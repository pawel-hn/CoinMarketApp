package pawel.hn.coinmarketapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
    return this as MutableLiveData<T>
}