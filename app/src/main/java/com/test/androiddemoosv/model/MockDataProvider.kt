package com.test.androiddemoosv.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.time.Instant
import java.time.temporal.ChronoUnit

object MockDataProvider {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(): ApiResponse {

        val start = Instant.now()
        val end = start.plus(2, ChronoUnit.MINUTES)


        val json = """
        {
          "user": {
            "userType": "active",
            "coolingStartTime": "$start",
            "coolingEndTime": "$end",
            "accessibleModules": ["payments", "account_info"]
          },
          "modules": [
            { "id": "payments", "title": "Payments", "requiresConsent": true },
            { "id": "account_info", "title": "Account Info", "requiresConsent": false },
            { "id": "consent_center", "title": "Consent Center", "requiresConsent": true }
          ]
        }
        """

        return Gson().fromJson(json, ApiResponse::class.java)
    }

}
