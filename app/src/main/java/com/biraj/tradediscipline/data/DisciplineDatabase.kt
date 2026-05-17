package com.biraj.tradediscipline.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TradingDayEntity::class,
        TradeIntentEntity::class,
        TradeReviewEntity::class,
        DisciplineEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DisciplineDatabase : RoomDatabase() {
    abstract fun disciplineDao(): DisciplineDao

    companion object {
        @Volatile private var instance: DisciplineDatabase? = null

        fun get(context: Context): DisciplineDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DisciplineDatabase::class.java,
                    "discipline_incharge.db"
                ).build().also { instance = it }
            }
        }
    }
}
