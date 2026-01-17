package com.test.androiddemoosv.manager

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.test.androiddemoosv.model.CoolingState
import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class CoolingManager(
    coolingStartTime: String,
    coolingEndTime: String,
    private val scope: CoroutineScope
) {

    private val startTime: Instant = Instant.parse(coolingStartTime)
    private val endTime: Instant = Instant.parse(coolingEndTime)

    private var job: Job? = null

    private val _coolingState = MutableLiveData(CoolingState(false, null))
    val coolingState: LiveData<CoolingState> = _coolingState

    fun start() {
        if (job != null) return

        job = scope.launch {
            while (isActive) {
                val now = Instant.now()

                val state = when {
                    now.isBefore(startTime) -> CoolingState(false, null)
                    now.isAfter(endTime) -> {
                        CoolingState(false, null).also { stop() } // stop timer
                    }
                    else -> {
                        val remainingMillis = Duration.between(now, endTime).toMillis().coerceAtLeast(0)
                        CoolingState(true, formatTimeMMSS(remainingMillis))
                    }
                }

                _coolingState.postValue(state)

                // Drift-free delay to next full second
                val nextTickMillis = 1000 - (Instant.now().toEpochMilli() % 1000)
                delay(nextTickMillis)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private fun formatTimeMMSS(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = (totalSeconds / 60) % 60
        val seconds = totalSeconds % 60

        return String.format(
            Locale.US,
            "Cooling ends in %02d:%02d",
            minutes, seconds
        )
    }
}
