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
    val coordLong: Double?
    //var favorito: Boolean = false
) : Serializable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "objid" to objid,
            "id" to id,
            "nombre" to nombre,
            "escudo" to escudo,
            "seccion" to seccion,
            "premio" to premio,
            "premioE" to premioE,
            "fallera" to fallera,
            "presidente" to presidente,
            "artista" to artista,
            "lema" to lema,
            "boceto" to boceto,
            "experim" to experim,
            "coordLat" to coordLat,
            "coordLong" to coordLong
            //"favorito" to favorito
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Falla {
            return Falla(
                objid = (map["objid"] as Long).toInt(),
                id = (map["id"] as Long).toInt(),
                nombre = map["nombre"] as String?,
                escudo = map["escudo"] as String?,
                seccion = map["seccion"] as String?,
                premio = map["premio"] as String?,
                premioE = map["premioE"] as String?,
                fallera = map["fallera"] as String?,
                presidente = map["presidente"] as String?,
                artista = map["artista"] as String?,
                lema = map["lema"] as String?,
                boceto = map["boceto"] as String?,
                experim = (map["experim"] as Long).toInt(),
                coordLat = map["coordLat"] as Double?,
                coordLong = map["coordLong"] as Double?
                //favorito = map["favorito"] as Boolean
            )
        }
    }
}