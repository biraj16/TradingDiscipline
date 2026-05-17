package com.biraj.tradediscipline.logic

import com.biraj.tradediscipline.data.EmotionalCondition
import com.biraj.tradediscipline.data.PermissionAction
import com.biraj.tradediscipline.data.TradeReason
import com.biraj.tradediscipline.data.TradingDayEntity

object DisciplineRulesEngine {
    data class PermissionResult(
        val action: PermissionAction,
        val title: String,
        val message: String,
        val severity: Int
    )

    fun evaluate(
        day: TradingDayEntity?,
        emotionalCondition: EmotionalCondition,
        currentPnl: Double,
        tradesTaken: Int,
        tradeReason: TradeReason
    ): PermissionResult {
        val plan = day ?: TradingDayEntity(date = TradingModeResolver.todayDateIst())
        val maxLoss = plan.maxDailyLoss
        val maxTrades = plan.maxTrades

        if (!plan.marketPlanDone) {
            return blocked("Morning plan is not completed. Trade permission is blocked until the day plan is set.")
        }

        if (maxLoss > 0 && currentPnl <= -kotlin.math.abs(maxLoss)) {
            return blocked("Daily max loss is reached. Trading should be locked for today.")
        }

        if (plan.moodLabel == "Not fit to trade") {
            return blocked("Morning condition was marked as not fit to trade. Trading is blocked for today.")
        }

        if (plan.moodLabel == "Emotional" && emotionalCondition != EmotionalCondition.CALM) {
            return caution("Morning condition and current state both show emotional risk. Use reduced size or skip.")
        }

        if (tradesTaken >= maxTrades) {
            return blocked("Maximum trade count reached. No more trades today.")
        }

        if (emotionalCondition == EmotionalCondition.REVENGE_FEELING) {
            return blocked("Revenge feeling detected. Do not trade from this state.")
        }

        if (tradeReason == TradeReason.RECOVERING_LOSS) {
            return blocked("Reason is recovery of loss. That is not a valid trade reason.")
        }

        if (tradeReason == TradeReason.JUST_FELT_LIKE_ENTERING || tradeReason == TradeReason.NOT_SURE) {
            return blocked("Trade reason is weak. No setup, no permission.")
        }

        if (emotionalCondition == EmotionalCondition.FOMO_CHASING || tradeReason == TradeReason.CHASING_FAST_MOVE) {
            return wait("Chasing/FOMO risk detected. Wait for next candle or a clean retest.")
        }

        if (emotionalCondition == EmotionalCondition.FRUSTRATED_AFTER_LOSS) {
            return caution("You are frustrated after a loss. Trade only if the setup is very clean and size is reduced.")
        }

        if (emotionalCondition == EmotionalCondition.OVERCONFIDENT) {
            return caution("Overconfidence risk detected. Protect the day and avoid oversized trades.")
        }

        if (maxLoss > 0 && currentPnl < 0 && kotlin.math.abs(currentPnl) >= maxLoss * 0.6) {
            return caution("You have used more than 60% of daily loss limit. Only best setup should be allowed.")
        }

        if (tradesTaken >= maxTrades - 1) {
            return caution("You are near the max trade limit. This should be a high-quality trade only.")
        }

        return PermissionResult(
            action = PermissionAction.ALLOWED,
            title = "Trade Allowed",
            message = "Emotional state, trade count, P&L, and trade reason are acceptable.",
            severity = 1
        )
    }

    private fun blocked(message: String) = PermissionResult(
        action = PermissionAction.BLOCKED,
        title = "Trade Blocked",
        message = message,
        severity = 4
    )

    private fun wait(message: String) = PermissionResult(
        action = PermissionAction.WAIT,
        title = "Wait",
        message = message,
        severity = 3
    )

    private fun caution(message: String) = PermissionResult(
        action = PermissionAction.CAUTION,
        title = "Trade With Caution",
        message = message,
        severity = 2
    )
}
