package pawel.hn.coinmarketapp.ui.pricenotify

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.databinding.FragmentPriceNotifyBinding
import pawel.hn.coinmarketapp.notification.NotifyWorker

@AndroidEntryPoint
class PriceNotifyFragment : Fragment(R.layout.fragment_price_notify) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPriceNotifyBinding.bind(view)



        binding.btnNotify.setOnClickListener {
            val workRequest = OneTimeWorkRequestBuilder<NotifyWorker>()
                .build()
            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        }
    }




}