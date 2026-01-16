package com.test.androiddemoosv.viewmodel
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.androiddemoosv.manager.AccessManager
import com.test.androiddemoosv.model.MockDataProvider
import com.test.androiddemoosv.model.Module
import java.time.Duration
import java.time.Instant
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel : ViewModel() {

    private val response = MockDataProvider.getData()
    private val user = response.user

    // Cooling window (UTC)
    private val startTime: Instant = Instant.parse(user.coolingStartTime)
    private val endTime: Instant = Instant.parse(user.coolingEndTime)

    private var watcherTimer: CountDownTimer? = null

    // --- UI state ---
    private val _modules = MutableLiveData<List<Module>>()
    val modules: LiveData<List<Module>> = _modules
    private val _accessableIds = MutableLiveData<List<String>>()
    val accessableIds: LiveData<List<String>> = _accessableIds

    private val _coolingMessage = MutableLiveData<String?>()
    val coolingMessage: LiveData<String?> = _coolingMessage

    private val _isCoolingActive = MutableLiveData(false)
    val isCoolingActive: LiveData<Boolean> = _isCoolingActive



    init {
        _modules.value = response.modules
        _accessableIds.value = response.user.accessibleModules
        startCoolingWatcher()

        Log.d("TIME_DEBUG", "Now UTC = ${Instant.now()}")
        Log.d("TIME_DEBUG", "Start UTC = $startTime")
        Log.d("TIME_DEBUG", "End UTC = $endTime")

    }

    /**
     * Watches time every second and updates banner + cooling state
     */
    private fun startCoolingWatcher() {
        watcherTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val now = Instant.now()

                when {
                    // Cooling not started yet
                    now.isBefore(startTime) -> {
                        _coolingMessage.value = null
                        _isCoolingActive.value = false
                    }

                    // Cooling finished
                    now.isAfter(endTime) -> {
                        _coolingMessage.value = null
                        _isCoolingActive.value = false
                        cancel()
                    }

                    // Cooling active
                    else -> {
                        _isCoolingActive.value = true

                        val remainingMillis =
                            Duration.between(now, endTime)
                                .toMillis()
                                .coerceAtLeast(0)

                        _coolingMessage.value =
                            formatTimeMMSS(remainingMillis)
                    }
                }
            }

            override fun onFinish() {
                // Not used
            }
        }.start()
    }

    /**
     * Access control used by adapter click
     */
    /*fun checkAccess(module: Module): Pair<Boolean, String> {
        return when {
            _isCoolingActive.value == true ->
                false to "Access denied: cooling period"

            !user.accessibleModules.contains(module.id) ->
                false to "Access denied: no permission"

            else ->
                true to "Allowed"
        }
    }*/
    fun checkAccess(module: Module): Pair<Boolean, String> {

        // Cooling applies ONLY to Payments
        if (module.id == "payments" && _isCoolingActive.value == true) {
            return false to "Access denied: cooling period"
        }
        if (!user.accessibleModules.contains(module.id)) {
            return false to "Access denied: no permission"
        }
        return true to "Allowed"
    }


    /**
     * Formats time as MM:SS
     */
    private fun formatTimeMMSS(millis: Long): String {
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

    override fun onCleared() {
        watcherTimer?.cancel()
        super.onCleared()
    }
}
