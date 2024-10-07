package pardo.tarin.uv.fallas.bdRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pardo.tarin.uv.fallas.CalendarioAdapter

@Dao
interface EventosDao {
    @Query("SELECT * FROM eventos")
    fun getAll(): List<CalendarioAdapter.Evento>
    @Query("SELECT * FROM eventos WHERE id_e = :id")
    fun getEvento(id: String): CalendarioAdapter.Evento?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvento(vararg ev: CalendarioAdapter.Evento)
    @Update
    fun updateEvento(ev: CalendarioAdapter.Evento)
    @Delete
    fun delete(ev: CalendarioAdapter.Evento)
    @Query("DELETE FROM eventos")
    fun deleteAll()
}