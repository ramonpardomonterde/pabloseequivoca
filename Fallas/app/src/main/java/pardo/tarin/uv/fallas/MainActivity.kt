package pardo.tarin.uv.fallas

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.ImageButton
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import pardo.tarin.uv.fallas.databinding.ActivityMainBinding
import pardo.tarin.uv.fallas.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var botonfav: ImageButton
    //var email: String? = null

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        botonfav = binding.appBarMain.botonfav
        setSupportActionBar(binding.appBarMain.toolbar)

        // Recoge los extras del Intent
        DataHolder.publicEmail = intent.getStringExtra("email")!!
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", DataHolder.publicEmail)
        prefs.apply()

        // Crea un Bundle y añade los extras
        val bundle = Bundle()
        bundle.putString("email", DataHolder.publicEmail)
        Log.d("EmailMain", DataHolder.publicEmail.toString())

        // Crea una instancia de HomeFragment y establece los argumentos
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.nav_home, bundle)
        //navController.setGraph(R.navigation.mobile_navigation, bundle)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home))
        setupActionBarWithNavController(navController, appBarConfiguration)
        /*navView.setupWithNavController(navController)*/
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}