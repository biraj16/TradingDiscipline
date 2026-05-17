package com.biraj.tradediscipline.logic

import com.biraj.tradediscipline.data.AppMode
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TradingModeResolver {
    private val istZone: ZoneId = ZoneId.of("Asia/Kolkata")

    fun todayDateIst(): String = ZonedDateTime.now(istZone).toLocalDate().toString()

    fun nowIst(): ZonedDateTime = ZonedDateTime.now(istZone)

    fun resolve(now: ZonedDateTime = nowIst()): AppMode {
        val day = now.dayOfWeek
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return AppMode.WEEKEND_CLOSED
        }

        val time = now.toLocalTime()
        return when {
            time.isBefore(LocalTime.of(5, 0)) -> AppMode.REST
            time.isBefore(LocalTime.of(9, 15)) -> AppMode.PRE_MARKET
            time.isBefore(LocalTime.of(15, 31)) -> AppMode.LIVE_TRADING
            else -> AppMode.POST_MARKET
        }
    }
}
