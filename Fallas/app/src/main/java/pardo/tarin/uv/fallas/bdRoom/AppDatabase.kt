package pardo.tarin.uv.fallas.bdRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import pardo.tarin.uv.fallas.Falla

@Database(entities = [Falla::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun fallaDao(): FallaDao
}