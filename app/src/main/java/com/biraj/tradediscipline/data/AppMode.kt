package com.biraj.tradediscipline.data

enum class AppMode(val title: String, val subtitle: String) {
    PRE_MARKET(
        title = "Before Market",
        subtitle = "Prepare plan, limits, and permission for the day."
    ),
    LIVE_TRADING(
        title = "Live Trading",
        subtitle = "Fast trade permission gate. No long checklist."
    ),
    POST_MARKET(
        title = "Post Market",
        subtitle = "Review behaviour, mistakes, and discipline score."
    ),
    REST(
        title = "Rest Mode",
        subtitle = "Market is closed. Review history or settings."
    ),
    WEEKEND_CLOSED(
        title = "Weekend Closed",
        subtitle = "No live trading mode on Saturday/Sunday."
    )
}

enum class PermissionAction(val label: String) {
    ALLOWED("Trade Allowed"),
    CAUTION("Trade With Caution"),
    WAIT("Wait"),
    BLOCKED("Trade Blocked")
}

enum class EmotionalCondition(val label: String) {
    CALM("Calm / Normal"),
    SLIGHTLY_EXCITED("Slightly Excited"),
    FEARFUL("Fearful / Hesitating"),
    FRUSTRATED_AFTER_LOSS("Frustrated After Loss"),
    REVENGE_FEELING("Revenge Feeling"),
    FOMO_CHASING("FOMO / Chasing Feeling"),
    OVERCONFIDENT("Overconfident"),
    TIRED_DISTRACTED("Tired / Distracted")
}

enum class TradeReason(val label: String) {
    BREAKOUT_CONTINUATION("Breakout Continuation"),
    BREAKDOWN_CONTINUATION("Breakdown Continuation"),
    PULLBACK_ENTRY("Pullback Entry"),
    REVERSAL_FROM_LEVEL("Reversal From Important Level"),
    TRAP_FALSE_BREAKOUT("Trap / False Breakout"),
    RANGE_SCALP("Range Scalp"),
    VWAP_RECLAIM("VWAP / MA Reclaim"),
    RETEST_OF_LEVEL("Retest Of Level"),
    PREMIUM_MOMENTUM("Premium Momentum Confirmation"),
    SYSTEM_CONFIRMED("App / System Confirmed Setup"),
    NOT_SURE("I Am Not Sure"),
    JUST_FELT_LIKE_ENTERING("Just Felt Like Entering"),
    RECOVERING_LOSS("Recovering Loss"),
    CHASING_FAST_MOVE("Chasing Fast Move")
}

enum class EntryDiscipline(val label: String) {
    GOOD("Good"),
    EARLY("Early"),
    LATE("Late"),
    CHASE("Chase"),
    NO_SETUP("No Setup")
}

enum class ExitDiscipline(val label: String) {
    GOOD("Good"),
    PANIC("Panic Exit"),
    GREEDY("Greedy Hold"),
    IGNORED_INVALIDATION("Ignored Invalidation"),
    EXITED_TOO_EARLY("Exited Too Early")
}
