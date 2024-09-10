package org.redbyte.life.common.domain

sealed interface Rule {
    fun apply(isAlive: Boolean, neighbors: Int): Boolean
}

data object ClassicRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in 2..3 else neighbors == 3
}

data object DayAndNightRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in listOf(3, 6, 7, 8) else neighbors in listOf(3, 6, 7, 8)
}

data object HighLifeRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in 2..3 else neighbors == 3 || neighbors == 6
}

data object MorleyRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in listOf(2, 4, 5) else neighbors in listOf(3, 6)
}

data object TwoByTwoRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in listOf(1, 2, 5) else neighbors in listOf(3, 6)
}

data object DiamoebaRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in 5..8 else neighbors in listOf(3, 5, 6, 7)
}

data object LifeWithoutDeathRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        isAlive || neighbors == 3
}

data object ReplicatorRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        neighbors % 2 == 1
}

data object SeedsRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        !isAlive && neighbors == 2
}

