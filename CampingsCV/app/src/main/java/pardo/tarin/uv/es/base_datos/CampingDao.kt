package pardo.tarin.uv.es.base_datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pardo.tarin.uv.es.Camping

@Dao
interface CampingDao {
    @Query("SELECT * FROM favourite_campings")
    fun getAll(): List<Camping>

    @Query("SELECT * FROM favourite_campings WHERE id_c = :id")
    fun getCamping(id: Int): Camping?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCamping(vararg camping: Camping)

    @Update
    fun updateCamping(camping: Camping)

    @Delete
    fun delete(camping: Camping)

    @Query("DELETE FROM favourite_campings")
    fun deleteAll()
}