package pardo.tarin.uv.fallas.bdRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import pardo.tarin.uv.fallas.CalendarioAdapter
import pardo.tarin.uv.fallas.Falla

@Database(entities = [CalendarioAdapter.Evento::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun eventDao(): EventosDao
}