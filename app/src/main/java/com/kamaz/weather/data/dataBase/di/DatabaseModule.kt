package com.kamaz.weather.data.dataBase.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kamaz.weather.data.dataBase.AppDatabase
import com.kamaz.weather.data.dataBase.CityDao
import com.kamaz.weather.data.dataBase.CityEntity
import com.kamaz.weather.data.dataBase.MIGRATION_1_2


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase {
        val db = Room.databaseBuilder(ctx, AppDatabase::class.java, "app.db")
            .addMigrations(MIGRATION_1_2)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("DB", "onCreate: DB created")
                }
            })
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = db.cityDao()
                val count = dao.count()
                if (count == 0) {
                    dao.insertAll(
                        listOf(
                            CityEntity("Moscow", 55.7558, 37.6173, isVisible = true,  isUserAdded = false),
                            CityEntity("Saint Petersburg", 59.9343, 30.3351, isVisible = true,  isUserAdded = false),
                            CityEntity("Novosibirsk", 55.0084, 82.9357, isVisible = true,  isUserAdded = false),
                            CityEntity("Yekaterinburg", 56.8389, 60.6057, isVisible = true, isUserAdded = false)
                        )
                    )
                }
            } catch (e: Throwable) {
            }
        }

        return db
    }

    @Provides
    fun provideCityDao(db: AppDatabase): CityDao = db.cityDao()
}

