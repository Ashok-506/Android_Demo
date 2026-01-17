package com.test.androiddemoosv.manager

import com.test.androiddemoosv.model.Module

class AccessManager(private val accessibleModules: List<String>) {

    fun checkAccess(
        module: Module,
        isCoolingActive: Boolean): Pair<Boolean, String> {

        // Cooling applies ONLY to payments
        if (module.id == "payments" && isCoolingActive) {
            return false to "Access denied: cooling period"
        }

        if (!accessibleModules.contains(module.id)) {
            return false to "Access denied: no permission"
        }

        return true to "Allowed"
    }
}

