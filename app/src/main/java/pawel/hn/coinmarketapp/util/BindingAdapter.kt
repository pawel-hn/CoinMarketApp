package pawel.hn.coinmarketapp.util

import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pawel.hn.coinmarketapp.R

/**
 * Binding adapters used for showing progress bar and error massages.
 */

class BindingAdapter {

    companion object {

        @BindingAdapter("progressBarVisibility")
        @JvmStatic
        fun progressBarVisibility(view: View, isVisible: Boolean) {
            if (isVisible) {
                showLog("proressBard called")
                when (view) {
                    is RecyclerView -> view.visibility = View.GONE
                    is LinearLayout -> view.visibility = View.GONE
                    is ProgressBar -> view.visibility = View.VISIBLE
                }
            } else {
                showLog("proressBard hide")
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
                    is RecyclerView -> {
                        view.visibility = View.GONE
                        showLog("errorVisibility called: true, recyclerView")
                        view.isClickable = false
                    }
                }
            } else {
                when (view) {
                    is TextView -> view.visibility = View.GONE
                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        view.isClickable = true
                    }
                }
            }
        }

        @BindingAdapter("visibilityWithinMotionLayout")
        @JvmStatic
        fun visibilityWithinMotionLayout(view: View, isVisible: Boolean) {
            showLog("visibilityWithinMotionLayout $isVisible")
            if (view.parent is MotionLayout) {
                val layout = view.parent as MotionLayout
                val setToVisibility = if(isVisible) View.VISIBLE else View.GONE
                val setToAlpha = if(isVisible) 1f else 0f

                for(id in layout.constraintSetIds) {
                    val constraint = layout.getConstraintSet(id)
                    constraint?.let {
                        it.setVisibility(view.id, setToVisibility)
                        it.setAlpha(view.id, setToAlpha)
                    }
                }
            }

        }






    }
}