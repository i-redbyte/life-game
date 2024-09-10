package org.redbyte.life.common

interface Rule {
    fun apply(isAlive: Boolean, neighbors: Int): Boolean
}

class ClassicRule : Rule {
    override fun apply(isAlive: Boolean, neighbors: Int): Boolean =
        if (isAlive) neighbors in 2..3 else neighbors == 3
}