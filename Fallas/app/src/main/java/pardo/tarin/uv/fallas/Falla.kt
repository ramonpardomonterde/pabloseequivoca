package pardo.tarin.uv.fallas

import java.io.Serializable

data class Falla(
    val id: Int,
    val nombre: String?,
    val escudo: String?,
    val seccion: String?,
    val premio: String?,
    val premioE: String?,
    val fallera: String?,
    val presidente: String?,
    val artista: String?,
    val lema: String?,
    val boceto: String?,
    val experim: Int,
    val coordenadas: Pair<Double, Double>?
) : Serializable