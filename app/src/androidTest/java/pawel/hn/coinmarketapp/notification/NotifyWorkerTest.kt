package pawel.hn.coinmarketapp.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyWorkerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

   @Test
   fun testNotifyWorker() {
       val worker = TestListenableWorkerBuilder<NotifyWorker>(context).build()
       runBlocking {
           val result = worker.doWork()
           assertThat(result).isEqualTo(ListenableWorker.Result.success())
       }
   }
}