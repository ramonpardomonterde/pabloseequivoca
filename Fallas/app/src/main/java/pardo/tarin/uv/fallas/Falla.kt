package pardo.tarin.uv.fallas

data class Falla(
    val id_falla: Int,
    val nombre: String,
    val seccion: Int,
    val premio: String,
    val fallera: String,
    val presidente: String,
    val artista: String,
    val lema: String,
    val boceto: String,
    val coordenadas: Pair<Double, Double>
)