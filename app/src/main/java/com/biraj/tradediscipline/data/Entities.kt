package com.biraj.tradediscipline.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trading_days")
data class TradingDayEntity(
    @PrimaryKey val date: String,
    val marketPlanDone: Boolean = false,
    val allowedInstrument: String = "NIFTY",
    val maxTrades: Int = 3,
    val maxDailyLoss: Double = 0.0,
    val moodLabel: String = "Normal",
    val notes: String = "",
    val disciplineScore: Int = 100,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "trade_intents")
data class TradeIntentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tradingDate: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val emotionalCondition: String,
    val currentPnl: Double,
    val tradesTaken: Int,
    val tradeReason: String,
    val permissionAction: String,
    val permissionMessage: String
)

@Entity(tableName = "trade_reviews")
data class TradeReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tradingDate: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val pnl: Double,
    val followedPlan: Boolean,
    val entryDiscipline: String,
    val exitDiscipline: String,
    val mistakeTags: String,
    val lesson: String
)

@Entity(tableName = "discipline_events")
data class DisciplineEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tradingDate: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val eventType: String,
    val severity: Int,
    val note: String
)
