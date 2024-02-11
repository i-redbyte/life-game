package org.redbyte.genom.common.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val width: Int = 32,
    val height: Int = 32,
    val initialPopulation: Int= 256,
    val hasPacifists: Boolean = true,
    val hasAggressors: Boolean = false,
    val allowMutations: Boolean = false,
) : Parcelable {
    fun isPacificOnly(): Boolean = hasPacifists && !hasAggressors
    fun isAggressorsOnly(): Boolean = !hasPacifists && hasAggressors
    fun hasAllCells(): Boolean = hasPacifists && hasAggressors
}

