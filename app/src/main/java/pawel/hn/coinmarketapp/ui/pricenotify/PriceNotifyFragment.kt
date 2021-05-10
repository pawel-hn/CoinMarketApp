package pawel.hn.coinmarketapp.ui.pricenotify

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentPriceNotifyBinding
import pawel.hn.coinmarketapp.util.*

@AndroidEntryPoint
class PriceNotifyFragment : Fragment(R.layout.fragment_price_notify) {

    private val viewModel: PriceNotifyViewModel by viewModels()
    lateinit var binding: FragmentPriceNotifyBinding
    private var latestPrice = 0.0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPriceNotifyBinding.bind(view)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        binding.apply {

            sharedPref.run {

                tvCurrPriceAlert.text = formatPriceAndVolForView(
                    getSavedPriceAlert(this).toDouble(), ValueType.Fiat
                )
                viewModel.setCurrentPriceAlert(getSavedPriceAlert(this))
            }

            notificationSwitch.apply {
                isChecked = sharedPref.getBoolean(SAVE_SWITCH_ON_OFF, false)

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        sharedPref.put {
                            putBoolean(SAVE_SWITCH_ON_OFF, isChecked)
                        }
                        viewModel.setNotificationOn()
                    } else {
                        sharedPref.put {
                            putBoolean(SAVE_SWITCH_ON_OFF, isChecked)
                        }
                        viewModel.setNotificationOff()
                    }
                }
            }

            btnPriceAlertUpdate.setOnClickListener {
                var priceAlert: Int? = null
                if (!editTextPriceToAlert.text.isNullOrEmpty()) {
                    priceAlert = editTextPriceToAlert.text.toString().toInt()
                }

                when {
                    (priceAlert == null) || (priceAlert == 0) -> {
                        hideKeyboard(view)
                        showSnack(requireView(), "Error")
                    }

                    priceAlert - latestPrice < 1000 -> {
                        hideKeyboard(view)
                        showSnack(
                            requireView(),
                            "Alert must be higher at least by $1000 than latest price "
                        )
                    }

                    else -> {
                        sharedPref.put {
                            putInt(SAVE_CURRENT_PRICE_ALERT, priceAlert)
                        }
                        viewModel.setPriceAlert(priceAlert)
                        tvCurrPriceAlert.text =
                            formatPriceAndVolForView(priceAlert.toDouble(), ValueType.Fiat)
                        hideKeyboard(view)
                        editTextPriceToAlert.apply {
                            text.clear()
                            clearFocus()
                        }
                    }
                }
            }
        }

        subscribeToObservers(binding)
    }

    private fun getSavedPriceAlert(sharedPref: SharedPreferences): Int =
        sharedPref.getInt(SAVE_CURRENT_PRICE_ALERT, DEFAULT_PRICE_ALERT)

    private fun subscribeToObservers(binding: FragmentPriceNotifyBinding) {

        viewModel.latestPrice.observe(viewLifecycleOwner) {
            latestPrice = it
            binding.tvLatestPrice.text = formatPriceAndVolForView(it, ValueType.Fiat)

            viewModel.notificationOnOff.observe(viewLifecycleOwner) {
                viewModel.notifyWorker(it)
                binding.notificationSwitch.isChecked = it
                showLog("ischecked: ${binding.notificationSwitch.isChecked}")
            }

            viewModel.notifications.observe(viewLifecycleOwner) { list ->
                showLog("notifications obs, list: ${list.size}")
                if (list.isEmpty()) {
                    viewModel.setNotificationOff()
                }
            }

        }
    }
}
