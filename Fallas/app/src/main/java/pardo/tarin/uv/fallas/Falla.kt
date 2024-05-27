package pardo.tarin.uv.fallas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "fallasFavoritas")
data class Falla(
    @PrimaryKey @ColumnInfo(name = "id_f") val objid: Int,
    val id: Int,
    @ColumnInfo(name = "nombre_f") val nombre: String?,
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
    //val coordenadas: Pair<Double, Double>?
    val coordLat: Double?,
    val coordLong: Double?,
    var favorito: Boolean = false
) : Serializable