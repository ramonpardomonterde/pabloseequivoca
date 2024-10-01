package pardo.tarin.uv.fallas

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale

class FallasViewModel: ViewModel() {

    val _fallasInfantiles = MutableLiveData<List<List<Any>>?>()

    val _fallasAdultas = MutableLiveData<List<List<Any>>?>()

    val currentLenguage = Locale.getDefault().language

    init {
        getFallas()
    }

    private fun getFallas() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fallas =
                    getFallasHttp("https://mural.uv.es/pajotape/fallas_infantiles")// Realiza la operación de red para obtener los datos de las fallas infantiles
                withContext(Dispatchers.Main) {
                    _fallasInfantiles.value = fallas
                }

                val fallasAdultas = getFallasHttp("https://mural.uv.es/pajotape/fallas_adultas")
                withContext(Dispatchers.Main) {
                    _fallasAdultas.value = fallasAdultas
                }
            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _fallasInfantiles.value = null
                    _fallasAdultas.value = null
                    Toast.makeText(
                        null,
                        "Error de conexión. Comprueba tu conexión a internet",
                        Toast.LENGTH_LONG
                    ).show()
                    // Aquí puedes mostrar un mensaje al usuario para informarle del error
                    // y darle la opción de volver a intentarlo
                }
            }
        }
    }

    suspend fun getFallasHttp(_url: String): List<List<Any>> {

        return withContext(Dispatchers.IO){
            val url = _url
            val listaFallas = ArrayList<Falla>()
            val fallasOrdenadas: List<List<Any>>

            val request = Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                .addHeader("Accept", "application/json").addHeader("Accept-Charset", "es").build()

            val client = OkHttpClient()

            val response = client.newCall(request).execute()

            if(!response.isSuccessful) throw IOException("Unexpected code $response")
            val responseBody = response.body?.string()
            val jsonObject = JSONObject(responseBody)
            val jsonResult = jsonObject.getJSONObject("result")
            val records = jsonResult.getJSONArray("records")

            if(records != null){
                for(i in 0 until records.length()){
                    val record = records.getJSONObject(i)
                    val objid = record.optInt("objectid")
                    val id = record.optInt("id_falla")
                    val nombre = record.optString("nombre", null)
                    val escudo = record.optString("escudo", null)
                    val seccion = record.optString("seccion", null)
                    val premio = record.optString("Premio", "Sin premio")
                    val premioE = record.optString("PremioE", "Sin premio")
                    val fallera = record.optString("fallera", null)
                    val presidente = record.optString("presidente", null)
                    val artista = record.optString("artista", null)
                    val lema = record.optString("lema", null)
                    val boceto = record.optString("boceto", null)
                    val experim = record.optInt("experim")
                    val geo = record.getJSONObject("geo_point_2d")
                    val lat = geo.optDouble("lat")
                    val lon = geo.optDouble("lon")
                    //val coordenadas = Pair(lat, lon)

                    val falla = Falla(objid, id, nombre, escudo, seccion, premio, premioE, fallera, presidente, artista, lema, boceto, experim, lat, lon)
                    listaFallas.add(falla)
                }
            }

            fallasOrdenadas = ordenarPorSeccion(listaFallas)

            return@withContext fallasOrdenadas
        }
    }

    fun ordenarPorSeccion(originalFallasData: ArrayList<Falla>): List<List<Any>> {
        val fallasPorSeccion: Map<String, List<Falla>> = originalFallasData.groupBy { it.seccion.toString() }
        val secciontext = if(currentLenguage == "es") "Sección " else "Section "
        val seccionEspecialText = if(currentLenguage == "es") "Sección Especial" else "Special Section"
        val seccionFCText = if(currentLenguage == "es") "Sección Fuera de Concurso" else "Out of Contest Section"
        val matriz = fallasPorSeccion.map { (seccion, fallas) ->
            val fila = mutableListOf<Any>()
            if(seccion == "IE" || seccion == "E"){
                fila.add(seccionEspecialText)
            }
            else if (seccion == "FC"){
                fila.add(seccionFCText)
            }
            else if (seccion != null){
                fila.add("$secciontext $seccion") // Se añade el nombre de la sección a la fila
            }
            fila.addAll(fallas)
            fila
        }

        val matrizIE = matriz.filter { it[0] == seccionEspecialText }
        /*val matrizNoIE = matriz.filter { it[0] != "Sección Especial" && it[0] != "Sección null"}.sortedBy {
            val seccion = it[0].toString().removePrefix("Sección ").toIntOrNull() ?: Int.MAX_VALUE
            seccion
        }*/
        val matrizNoIE = matriz.filter { it[0] != seccionEspecialText && it[0] != "$secciontext null"}.sortedWith(
            compareBy(
                { it[0].toString().removePrefix("$secciontext ").filter { char -> char.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE },
                { it[0].toString().removePrefix("$secciontext ").filter { char -> char.isLetter() } }
            )
        )

        val matrizOrdenada = matrizIE + matrizNoIE

        /*val fila = matrizOrdenada[2]
        for (i in 1 until fila.size) {
            val falla = fila[i] as Falla
            val nombreFalla = falla.nombre ?: "Sin nombre"
            val seccion = falla.seccion ?: "Sin sección"
            Log.d("Falla", "Sección $seccion - $nombreFalla")
        }*/

        return matrizOrdenada
    }
}