package pawel.hn.coinmarketapp.util

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.R


class BindingAdapter {

    companion object {


        @BindingAdapter("progressBarVisibility")
        @JvmStatic
        fun progressBarVisibility(view: View, isVisible: Boolean) {
            if (isVisible) {
                when (view) {
                    is RecyclerView -> view.visibility = View.GONE
                    is LinearLayout -> view.visibility = View.GONE
                    is ProgressBar -> view.visibility = View.VISIBLE
                }
            } else {
                when (view) {
                    is RecyclerView -> view.visibility = View.VISIBLE
                    is LinearLayout -> view.visibility = View.GONE
                    is ProgressBar -> view.visibility = View.GONE
                }
            }
        }

        @BindingAdapter("errorViewVisibility")
        @JvmStatic
        fun errorVisibility(view: View, isVisible: Boolean) {
            if (isVisible) {
                when (view) {
                    is TextView -> {
                        view.visibility = View.VISIBLE
                        view.text = view.context.getString(R.string.response_error)
                    }
                }
            } else {
                when (view) {
                    is TextView -> view.visibility = View.GONE
                }
            }
        }
    }
}