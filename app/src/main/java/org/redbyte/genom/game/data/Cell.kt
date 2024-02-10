package org.redbyte.genom.game.data

data class Cell(var isAlive: Boolean, var genes: MutableSet<Int>, var turnsLived: Int = 0)

