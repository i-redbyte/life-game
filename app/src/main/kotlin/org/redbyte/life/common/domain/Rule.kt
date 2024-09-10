package org.redbyte.life.common.domain

sealed interface Rule {
    fun apply(isAlive: Boolean, neighbors: Int): Boolean
}

data object ClassicRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in 2..3 else neighbors == 3
}