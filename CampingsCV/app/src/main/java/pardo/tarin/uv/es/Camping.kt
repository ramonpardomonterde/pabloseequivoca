package pardo.tarin.uv.es

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favourite_campings")
data class Camping(
    @PrimaryKey @ColumnInfo(name = "id_c") val id: Int,
    //val signatura: String,
    //val estado: String,
    val categoria: String,
    @ColumnInfo(name = "nombre_c") val nombre: String,
    //val provincia: String,
    val municipio: String,
    //val cp: Int,
    val direccion: String,
    /*val tipoVia: String,
    val via: String,
    val numero: String,
    val email: String,*/
    val web: String,
    /*val modalidad: String,
    val numParcelas: Int,
    val plazasParcela: String,
    val numBungalows: String,
    val plazaBungalows: String,
    val supLibreAcampada: String,
    val plazasLibreAcampada: String,*/
    val plazas: Int,
    /*val fechaAlta: String,
    val fechaBaja: String,
    val periodo: String,
    val diasPeriodo: String*/
): Serializable