package pawel.hn.coinmarketapp.util

import android.content.SharedPreferences

fun SharedPreferences.put(action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)
    editor.apply()
}