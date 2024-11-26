package pardo.tarin.uv.fallas.ui.infantiles

import androidx.lifecycle.ViewModel

class InfantilesViewModel : ViewModel() {

    /*private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    var _view: View? = null

    lateinit var originalfallasInfantiles: ArrayList<Falla>
    //var infantilesPorSeccion: List<List<Any>> = listOf()
    private val _infantilesPorSeccion = MutableStateFlow<List<List<Any>>>(listOf())

    // Usa StateFlow para exponer los datos de manera segura
    val infantilesPorSeccion: StateFlow<List<List<Any>>> = _infantilesPorSeccion

    suspend fun getFallas(_url: String) {
        val url = _url
        var listaFallas = ArrayList<Falla>()

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
                val id = record.optInt("id_falla")
                val nombre = record.optString("nombre", null)
                val seccion = record.optString("seccion", null)
                val premio = record.optString("Premio", "Sin premio")
                val premioE = record.optInt("PremioE")
                val fallera = record.optString("fallera", null)
                val presidente = record.optString("presidente", null)
                val artista = record.optString("artista", null)
                val lema = record.optString("lema", null)
                val boceto = record.optString("boceto", null)
                val experim = record.optInt("experim")
                val geo = record.getJSONObject("geo_point_2d")
                val lat = geo.optDouble("lat")
                val lon = geo.optDouble("lon")
                val coordenadas = Pair(lat, lon)

                val falla = Falla(id, nombre, seccion, premio, premioE, fallera, presidente, artista, lema, boceto, experim, coordenadas)

                listaFallas.add(falla)
            }
        }


        //callback(listaFallas)
        _infantilesPorSeccion.value = ordenarPorSeccion(listaFallas)
        Log.d("FallaInf", _infantilesPorSeccion.value.toString())
    }

    fun ordenarPorSeccion(originalFallasData: ArrayList<Falla>): List<List<Any>> {
        val fallasPorSeccion: Map<String, List<Falla>> = originalFallasData.groupBy { it.seccion.toString() }

        val matriz = fallasPorSeccion.map { (seccion, fallas) ->
            val fila = mutableListOf<Any>()
            if(seccion == "IE" || seccion == "E"){
                fila.add("Sección Especial")
            }
            else {
                fila.add("Sección $seccion") // Se añade el nombre de la sección a la fila
            }
            fila.addAll(fallas)
            fila
        }

        val matrizIE = matriz.filter { it[0] == "Sección Especial" }
        /*val matrizNoIE = matriz.filter { it[0] != "Sección Especial" && it[0] != "Sección null"}.sortedBy {
            val seccion = it[0].toString().removePrefix("Sección ").toIntOrNull() ?: Int.MAX_VALUE
            seccion
        }*/
        val matrizNoIE = matriz.filter { it[0] != "Sección Especial" && it[0] != "Sección null"}.sortedWith(
            compareBy(
                { it[0].toString().removePrefix("Sección ").filter { char -> char.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE },
                { it[0].toString().removePrefix("Sección ").filter { char -> char.isLetter() } }
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
    }*/
}