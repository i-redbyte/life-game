package org.redbyte.genom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val hasPacifists: Boolean,
    val hasAggressors: Boolean,
    val allowMutations: Boolean,
    val initialPopulation: Int = 1300
) : Parcelable {
    fun isPacificOnly(): Boolean = hasPacifists && !hasAggressors
    fun isAggressorsOnly(): Boolean = !hasPacifists && hasAggressors
    fun hasAllCells(): Boolean = hasPacifists && hasAggressors
}

