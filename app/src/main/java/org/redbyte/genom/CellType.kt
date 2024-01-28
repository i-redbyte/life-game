package org.redbyte.genom


enum class CellType {
    SUICIDAL,
    AGGRESSIVE,
    PEACEFUL,
    DEAD,
    SCAVENGER;

    companion object {
        fun randomType(): CellType {
            return enumValues<CellType>().filter { it != DEAD }.random()
        }
    }
}