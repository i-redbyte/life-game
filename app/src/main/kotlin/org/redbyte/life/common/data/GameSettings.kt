package org.redbyte.life.common.data

import org.redbyte.life.common.domain.Rule

data class GameSettings(
    val width: Int,
    val height: Int,
    val initialPopulation: Int,
    val rule: Rule
)
