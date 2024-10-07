package pardo.tarin.uv.fallas.bdRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventos")
data class Evento (
    @PrimaryKey @ColumnInfo(name = "id_e") val id: Int,
    val alarma: Boolean? = null
)