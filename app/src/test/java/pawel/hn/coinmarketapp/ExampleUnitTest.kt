package pawel.hn.coinmarketapp

import android.icu.text.NumberFormat
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun formatter() {
        assertEquals("1,234.57", formatter.format(1234.567))
    }

    @Test
    fun formatterTotal() {
        val doubleForFormatter = 12345.6789
        val stringExpected = "12,346"

        assertEquals(stringExpected, formatterTotal.format(doubleForFormatter))
    }

}