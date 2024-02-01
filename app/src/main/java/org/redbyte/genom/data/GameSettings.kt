package org.redbyte.genom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val hasPacifists: Boolean,
    val hasAggressors: Boolean,
    val allowMutations: Boolean
): Parcelable

