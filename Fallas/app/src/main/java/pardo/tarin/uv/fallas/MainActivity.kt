package pardo.tarin.uv.fallas

import android.content.Context
import android.os.Bundle
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

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        botonfav = binding.appBarMain.botonfav
        setSupportActionBar(binding.appBarMain.toolbar)

        /*val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Integración de Firebase completada")
        analytics.logEvent("InitScreen", bundle)*/

        /*val infantilesViewModel =
            ViewModelProvider(this).get(InfantilesViewModel::class.java)

        coroutineScope.launch(Dispatchers.IO) {
            /*infantilesViewModel.getFallas("https://mural.uv.es/pajotape/fallas_infantiles") { fallas ->
                infantilesViewModel.originalfallasInfantiles = fallas
                infantilesViewModel.infantilesPorSeccion = infantilesViewModel.ordenarPorSeccion(infantilesViewModel.originalfallasInfantiles)
                Log.d("FallaInf", infantilesViewModel.infantilesPorSeccion.toString())
            }*/
            infantilesViewModel.getFallas("https://mural.uv.es/pajotape/fallas_infantiles")
        }*/

        /* binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        /*val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView*/
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_infantiles, R.id.nav_adultas
            )/*, drawerLayout*/
        )*/

        // Recoge los extras del Intent
        val email = intent.getStringExtra("email")
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()

        // Crea un Bundle y añade los extras
        val bundle = Bundle()
        bundle.putString("email", email)

        // Crea una instancia de HomeFragment y establece los argumentos
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle

        navController.setGraph(R.navigation.mobile_navigation, bundle)

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