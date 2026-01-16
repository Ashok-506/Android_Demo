package com.test.androiddemoosv.ui

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.test.androiddemoosv.R

enum class CardState {
    COOLING,
    ACTIVE,
    DISABLED
}

fun MaterialCardView.applyCardState(
    state: CardState,
    context: Context
) {
    when (state) {
        CardState.COOLING -> {
            setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.card_cooling)
            )
            alpha = 0.7f
        }

        CardState.ACTIVE -> {
            setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.card_active)
            )
            alpha = 1f
        }

        CardState.DISABLED -> {
            setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.card_disabled)
            )
            alpha = 0.5f
        }
    }
}
