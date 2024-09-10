package org.redbyte.life.common.data

import org.redbyte.life.common.domain.ClassicRule
import org.redbyte.life.common.domain.Rule

data class GameSettings(
    val width: Int = 32,
    val height: Int = 32,
    val initialPopulation: Int = 128,
    val rule: Rule = ClassicRule

)
