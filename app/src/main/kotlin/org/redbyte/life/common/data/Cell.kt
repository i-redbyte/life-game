package org.redbyte.life.common.data

data class Cell(var isAlive: Boolean, var genes: MutableSet<Int>, var turnsLived: Int = 0)

