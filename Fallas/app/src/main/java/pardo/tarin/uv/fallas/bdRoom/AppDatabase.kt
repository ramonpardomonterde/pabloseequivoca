package pardo.tarin.uv.fallas.bdRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import pardo.tarin.uv.fallas.CalendarioAdapter

@Database(entities = [CalendarioAdapter.Evento::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun eventDao(): EventosDao
}