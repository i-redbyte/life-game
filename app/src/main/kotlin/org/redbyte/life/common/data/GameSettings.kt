package org.redbyte.life.common.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val width: Int = 32,
    val height: Int = 32,
    val initialPopulation: Int = 128,

) : Parcelable
