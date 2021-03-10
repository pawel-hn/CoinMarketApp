package pawel.hn.coinmarketapp

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        val x = 4
        assertThat(x).isEqualTo(2+2)
    }


    @Test
    fun formatter() {

        assertThat(formatter.format(1234.567)).matches("1,234.57")
    }

    @Test
    fun formatterTotal() {
        val doubleForFormatter = 12345.6789
        val stringExpected = "12,346"

        assertThat(formatterTotal.format(doubleForFormatter)).matches(stringExpected)
    }

}