package pardo.tarin.uv.fallas

import java.io.Serializable

data class Seccion(
    val nombre: String,
    val fallas: List<Falla>
) : Serializable
