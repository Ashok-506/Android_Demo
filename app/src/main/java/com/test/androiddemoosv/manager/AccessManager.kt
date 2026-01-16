package com.test.androiddemoosv.manager

import com.test.androiddemoosv.model.Module
import com.test.androiddemoosv.model.User
import java.time.Duration
import java.time.Instant
import java.util.Locale

class AccessManager(private val user: User) {

    private val start = Instant.parse(user.coolingStartTime)
    private val end = Instant.parse(user.coolingEndTime)

    fun shouldStartCoolingTimer(): Boolean {
        val now = Instant.now()
        return now.isAfter(start) && now.isBefore(end)
    }

    fun isCoolingFinished(): Boolean {
        return Instant.now().isAfter(end)
    }

    fun millisUntilStart(): Long {
        val now = Instant.now()
        return if (now.isBefore(start)) {
            Duration.between(now, start).toMillis()
        } else 0L
    }

    fun remainingMillis(): Long {
        val now = Instant.now()
        return Duration.between(now, end).toMillis().coerceAtLeast(0)
    }

    fun formatTimeMMSS(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = (totalSeconds / 60) % 60
        val seconds = totalSeconds % 60
        return String.format(
            Locale.US,
            "Cooling ends in %02d:%02d",
            minutes,
            seconds
        )
    }

    fun canAccessModule(module: Module): Pair<Boolean, String> {
        if (shouldStartCoolingTimer()) {
            return false to "Access denied: cooling period"
        }
        if (!user.accessibleModules.contains(module.id)) {
            return false to "Access denied: no permission"
        }
        return true to "Allowed"
    }

}
