package com.biraj.tradediscipline.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DisciplineDao {
    @Query("SELECT * FROM trading_days WHERE date = :date LIMIT 1")
    fun observeTradingDay(date: String): Flow<TradingDayEntity?>

    @Query("SELECT * FROM trading_days WHERE date = :date LIMIT 1")
    suspend fun getTradingDay(date: String): TradingDayEntity?

    @Upsert
    suspend fun upsertTradingDay(day: TradingDayEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTradeIntent(intent: TradeIntentEntity): Long

    @Query("SELECT * FROM trade_intents WHERE tradingDate = :date ORDER BY createdAtEpochMillis DESC")
    fun observeTradeIntents(date: String): Flow<List<TradeIntentEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTradeReview(review: TradeReviewEntity): Long

    @Query("SELECT * FROM trade_reviews WHERE tradingDate = :date ORDER BY createdAtEpochMillis DESC")
    fun observeTradeReviews(date: String): Flow<List<TradeReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDisciplineEvent(event: DisciplineEventEntity): Long

    @Query("SELECT * FROM discipline_events WHERE tradingDate = :date ORDER BY createdAtEpochMillis DESC")
    fun observeEvents(date: String): Flow<List<DisciplineEventEntity>>
}
