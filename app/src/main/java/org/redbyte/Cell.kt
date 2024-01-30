package org.redbyte

data class Cell(var isAlive: Boolean, var genes: MutableSet<Int>, var turnsLived: Int = 0)

