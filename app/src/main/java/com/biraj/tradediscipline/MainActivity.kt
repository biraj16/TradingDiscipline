package com.biraj.tradediscipline

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.biraj.tradediscipline.data.AppMode
import com.biraj.tradediscipline.data.DisciplineDatabase
import com.biraj.tradediscipline.data.EmotionalCondition
import com.biraj.tradediscipline.data.EntryDiscipline
import com.biraj.tradediscipline.data.ExitDiscipline
import com.biraj.tradediscipline.data.TradeIntentEntity
import com.biraj.tradediscipline.data.TradeReason
import com.biraj.tradediscipline.data.TradeReviewEntity
import com.biraj.tradediscipline.data.TradingDayEntity
import com.biraj.tradediscipline.logic.DisciplineRulesEngine
import com.biraj.tradediscipline.logic.TradingModeResolver
import com.biraj.tradediscipline.ui.TradingDisciplineApp
import com.biraj.tradediscipline.ui.theme.TradingDisciplineTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TradingDisciplineTheme {
                TradingDisciplineApp()
            }
        }
    }
}

data class DisciplineUiState(
    val mode: AppMode = AppMode.REST,
    val modeTitle: String = "",
    val modeSubtitle: String = "",
    val istStamp: String = "",
    val tradingDate: String = TradingModeResolver.todayDateIst(),
    val today: TradingDayEntity? = null,
    val intents: List<TradeIntentEntity> = emptyList(),
    val reviews: List<TradeReviewEntity> = emptyList(),
    val lastPermission: DisciplineRulesEngine.PermissionResult? = null,
    val message: String? = null
)

class DisciplineViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DisciplineDatabase.get(application).disciplineDao()
    private val tradingDate = TradingModeResolver.todayDateIst()
    private val lastPermission = MutableStateFlow<DisciplineRulesEngine.PermissionResult?>(null)
    private val userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DisciplineUiState> = combine(
        dao.observeTradingDay(tradingDate),
        dao.observeTradeIntents(tradingDate),
        dao.observeTradeReviews(tradingDate),
        lastPermission,
        userMessage
    ) { day, intents, reviews, permission, message ->
        val mode = TradingModeResolver.resolve()
        val istNow = TradingModeResolver.nowIst()
        DisciplineUiState(
            mode = mode,
            modeTitle = mode.title,
            modeSubtitle = mode.subtitle,
            istStamp = istNow.toLocalDate().toString() + " " + istNow.toLocalTime().withNano(0).toString() + " IST",
            tradingDate = tradingDate,
            today = day,
            intents = intents,
            reviews = reviews,
            lastPermission = permission,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DisciplineUiState()
    )

    fun saveMorningPlan(
        planDone: Boolean,
        allowedInstrument: String,
        maxTrades: Int,
        maxLoss: Double,
        moodLabel: String,
        notes: String
    ) {
        viewModelScope.launch {
            val existing = dao.getTradingDay(tradingDate)
            val updated = (existing ?: TradingDayEntity(date = tradingDate)).copy(
                marketPlanDone = planDone,
                allowedInstrument = allowedInstrument,
                maxTrades = maxTrades.coerceAtLeast(1),
                maxDailyLoss = maxLoss.coerceAtLeast(0.0),
                moodLabel = moodLabel,
                notes = notes,
                updatedAtEpochMillis = System.currentTimeMillis()
            )
            dao.upsertTradingDay(updated)
            userMessage.value = "Morning plan saved."
        }
    }

    fun checkTradePermission(
        emotionalCondition: EmotionalCondition,
        currentPnl: Double,
        tradesTaken: Int,
        tradeReason: TradeReason
    ) {
        viewModelScope.launch {
            val day = dao.getTradingDay(tradingDate)
            val result = DisciplineRulesEngine.evaluate(
                day = day,
                emotionalCondition = emotionalCondition,
                currentPnl = currentPnl,
                tradesTaken = tradesTaken,
                tradeReason = tradeReason
            )
            lastPermission.value = result
            dao.insertTradeIntent(
                TradeIntentEntity(
                    tradingDate = tradingDate,
                    emotionalCondition = emotionalCondition.label,
                    currentPnl = currentPnl,
                    tradesTaken = tradesTaken,
                    tradeReason = tradeReason.label,
                    permissionAction = result.action.label,
                    permissionMessage = result.message
                )
            )
            userMessage.value = result.title
        }
    }

    fun savePostTradeReview(
        pnl: Double,
        followedPlan: Boolean,
        entryDiscipline: EntryDiscipline,
        exitDiscipline: ExitDiscipline,
        mistakeTags: String,
        lesson: String
    ) {
        viewModelScope.launch {
            dao.insertTradeReview(
                TradeReviewEntity(
                    tradingDate = tradingDate,
                    pnl = pnl,
                    followedPlan = followedPlan,
                    entryDiscipline = entryDiscipline.label,
                    exitDiscipline = exitDiscipline.label,
                    mistakeTags = mistakeTags,
                    lesson = lesson
                )
            )
            userMessage.value = "Trade review saved."
        }
    }

    fun clearMessage() {
        userMessage.value = null
    }
}
