package com.test.androiddemoosv.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.temporal.ChronoUnit

object MockDataProvider {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(): ApiResponse {

        val curUTCTime = Instant.now() // Current UTC
        val endUTCTime = curUTCTime.plus(2, ChronoUnit.MINUTES).plus(14, ChronoUnit.SECONDS) // 02 min 14 seconds - cooling period

        Log.d("current_utc",curUTCTime.toString())
        Log.d("current_utc",endUTCTime.toString())

        /*val startTime = "2026-01-17T17:12:00Z";
        val endTime = "2026-01-17T17:14:14Z";*/

        val user = User(
            userType = "active",
            coolingStartTime = curUTCTime.toString(), // ISO-8601 UTC
            coolingEndTime = endUTCTime.toString(),
            accessibleModules = listOf("payments", "account_info")
        )

        val modules = listOf(
            Module("payments", "Payments", requiresConsent = true),
            Module("account_info", "Account Info", requiresConsent = false),
            Module("consent_center", "Consent Center", requiresConsent = true)
        )

        return ApiResponse(user, modules)
    }

}
