package pardo.tarin.uv.fallas

data class Falla(
    val id: Int,
    val nombre: String?,
    val seccion: String?,
    val premio: Int,
    val premioE: Int,
    val fallera: String?,
    val presidente: String?,
    val artista: String?,
    val lema: String?,
    val boceto: String?,
    val experim: Int,
    val coordenadas: Pair<Double, Double>?
)