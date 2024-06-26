package pardo.tarin.uv.fallas.bdRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pardo.tarin.uv.fallas.Falla

@Dao
interface FallaDao {
    @Query("SELECT * FROM fallasFavoritas")
    fun getAll(): List<Falla>
    @Query("SELECT * FROM fallasFavoritas WHERE id_f = :id")
    fun getFalla(id: Int): Falla?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFalla(vararg falla: Falla)
    @Update
    fun updateFalla(falla: Falla)
    @Delete
    fun delete(falla: Falla)
    @Query("DELETE FROM fallasFavoritas")
    fun deleteAll()
}