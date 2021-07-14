package pawel.hn.coinmarketapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import pawel.hn.coinmarketapp.R
import pawel.hn.coinmarketapp.viewmodels.CoinsViewModel


/**
 * Fragment which is responsible for answering user interactions with settings.
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

   private val viewModel: CoinsViewModel by viewModels()


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        val preferenceTheme = findPreference<ListPreference>(
            getString(R.string.settings_theme_key)
        )
        preferenceTheme?.setOnPreferenceChangeListener { _, newValue ->
            when(newValue.toString()) {
                getString(R.string.dark_theme) -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    true
                }
                getString(R.string.light_theme) -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    true
                }
                getString(R.string.follow_system) -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    true
                }
                else -> {false}
            }
        }

        val preferenceCurrency = findPreference<ListPreference>(
            getString(R.string.settings_currency_key)
        )
        preferenceCurrency?.setOnPreferenceChangeListener { _, newValue ->
            Toast.makeText(requireContext(),
                "${requireContext().getString(R.string.currency_set)} " +
                    newValue.toString(), Toast.LENGTH_SHORT).show()
            viewModel.refreshData(newValue.toString())

            true
        }
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val recyclerView =  super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        val dividerLine = DividerItemDecoration(context, RecyclerView.VERTICAL)
        recyclerView.addItemDecoration(dividerLine)
        return recyclerView
    }
}