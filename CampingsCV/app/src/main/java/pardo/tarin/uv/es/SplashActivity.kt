package pardo.tarin.uv.es
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity(), OnDataLoaded {

    private val campingList = CampingList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Start loading data
        CoroutineScope(Dispatchers.Main).launch {
            val idDataset = withContext(Dispatchers.IO) { campingList.getIdDataset() }
            if (idDataset.isNotEmpty()) {
                withContext(Dispatchers.IO) { campingList.getDataHttp(idDataset, this@SplashActivity) }
            }
        }
    }

    override fun onDataLoaded() {
        // When data is loaded, finish the activity
        finish()
    }
}