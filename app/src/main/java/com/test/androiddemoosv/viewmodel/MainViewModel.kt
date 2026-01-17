package com.test.androiddemoosv.viewmodel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.androiddemoosv.manager.AccessManager
import com.test.androiddemoosv.manager.CoolingManager
import com.test.androiddemoosv.model.CoolingState
import com.test.androiddemoosv.model.MockDataProvider
import com.test.androiddemoosv.model.Module

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel : ViewModel() {

    private val response = MockDataProvider.getData()
    private val user = response.user

    private val coolingManager = CoolingManager(
        coolingStartTime = user.coolingStartTime,
        coolingEndTime = user.coolingEndTime,
        scope = viewModelScope
    )

    private val accessManager = AccessManager(user.accessibleModules)

    private val _modules = MutableLiveData(response.modules)
    val modules: LiveData<List<Module>> = _modules

    private val _accessibleIds = MutableLiveData(user.accessibleModules)
    val accessibleIds: LiveData<List<String>> = _accessibleIds


    val coolingState: LiveData<CoolingState> = coolingManager.coolingState

    init { coolingManager.start() }
    fun checkAccess(module: Module): Pair<Boolean, String> {
        val isCoolingActive = coolingState.value?.isActive == true
        return accessManager.checkAccess(module, isCoolingActive)
    }

    override fun onCleared() {
        coolingManager.stop()
        super.onCleared()
    }
}
