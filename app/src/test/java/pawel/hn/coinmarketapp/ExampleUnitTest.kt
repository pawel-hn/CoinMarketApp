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
        val number = formatter.format(1234.57).replace(",", ".")

        assertThat(number).isEqualTo("1 234.57")
    }

    @Test
    fun numberFormat() {
        val number = numberUtil.format(12345.6789)

        assertThat(number).isEqualTo("12 345,679")
    }



    @Test
    fun formatterTotal() {
        val doubleForFormatter = 12345.6789
        val stringExpected = "12,346"

        assertThat(formatterTotal.format(doubleForFormatter)).matches(stringExpected)
    }

}