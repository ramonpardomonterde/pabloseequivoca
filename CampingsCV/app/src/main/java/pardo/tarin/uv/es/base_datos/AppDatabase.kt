package pardo.tarin.uv.es.base_datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pardo.tarin.uv.es.Camping

@Database(entities = [Camping::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campingDao(): CampingDao
}