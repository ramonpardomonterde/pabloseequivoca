package pardo.tarin.uv.fallas

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pardo.tarin.uv.fallas.databinding.ActivityMainBinding
import pardo.tarin.uv.fallas.ui.infantiles.InfantilesFragment
import pardo.tarin.uv.fallas.ui.infantiles.InfantilesViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var fallasAdultas = ArrayList<Falla>()
    var currentLanguage = "es"

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        currentLanguage = sharedPref.getString("language", "es").toString()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        //Configuración idioma
        val botonIdioma = binding.appBarMain.botonIdioma
        val initialImage = if (currentLanguage == "es") R.drawable.espanya else R.drawable.ingles
        var scaledDrawable = scaleDrawable(initialImage, 30, 30)
        botonIdioma.setImageDrawable(scaledDrawable)
        // Configura un OnClickListener en el botón de idioma
        botonIdioma.setOnClickListener {
            // Cambia el idioma y la imagen del botón cuando se pulse el botón
            val newLanguage = if (currentLanguage == "es") "en" else "es"
            val newImage = if (newLanguage == "es") R.drawable.espanya else R.drawable.ingles
            currentLanguage = newLanguage

            scaledDrawable = scaleDrawable(newImage, 30, 30)
            botonIdioma.setImageDrawable(scaledDrawable)

            // Guarda el nuevo idioma en las preferencias compartidas
            with (sharedPref.edit()) {
                putString("language", newLanguage)
                apply()
            }
            newLanguage != currentLanguage
            val newLocale = Locale(newLanguage)
            Locale.setDefault(newLocale)
            val newConfig = Configuration()
            newConfig.locale = newLocale
            resources.updateConfiguration(newConfig, resources.displayMetrics)
            recreate()
        }

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

    fun scaleDrawable(drawableId: Int, width: Int, height: Int): Drawable {
        val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDrawable(resources, scaledBitmap)
    }
}