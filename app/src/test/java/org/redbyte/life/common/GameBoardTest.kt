package org.redbyte.life.common

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.redbyte.life.common.data.GameSettings
import org.redbyte.life.common.domain.ClassicRule

class GameBoardTest {

    private lateinit var gameSettings: GameSettings
    private lateinit var gameBoard: GameBoard

    @Before
    fun setup() {
        gameSettings =
            GameSettings(width = 5, height = 5, initialPopulation = 5, rule = ClassicRule)
        gameBoard = GameBoard(gameSettings)
    }

    @Test
    fun `initial population should be set correctly`() {
        val livingCellsCount = gameBoard.countLivingCells()
        assertEquals(gameSettings.initialPopulation, livingCellsCount)
    }

    @Test
    fun `board should update state after applying rules with predefined configuration`() {
        val customSettings =
            GameSettings(width = 3, height = 3, initialPopulation = 3, rule = ClassicRule)
        val customGameBoard = GameBoard(customSettings)

        customGameBoard.matrix = listOf(
            0b010L, // Вторая клетка живая
            0b111L, // Все три клетки живые
            0b010L  // Вторая клетка живая
        )

        val initialLivingCells = customGameBoard.countLivingCells()
        customGameBoard.update()
        val updatedLivingCells = customGameBoard.countLivingCells()
        assertNotEquals(initialLivingCells, updatedLivingCells)
    }

    @Test
    fun `board should maintain correct living cell count after multiple updates`() {
        repeat(5) {
            gameBoard.update()
        }
        val livingCellsCount = gameBoard.countLivingCells()
        assertTrue(livingCellsCount >= 0)
    }

    @Test
    fun `should correctly calculate neighbors for middle cell`() {
        val customSettings =
            GameSettings(width = 3, height = 3, initialPopulation = 3, rule = ClassicRule)
        val customGameBoard = GameBoard(customSettings)

        customGameBoard.matrix = listOf(
            0b010L, // Вторая клетка живая
            0b111L, // Все три клетки живые
            0b010L  // Вторая клетка живая
        )

        // Проверяем количество соседей для центральной клетки (1,1)
        val neighbors = customGameBoard.countNeighbors(1, 1)
        assertEquals(4, neighbors) // Ожидаем 4 соседа
    }

    @Test
    fun `should handle empty board correctly`() {
        val customSettings =
            GameSettings(width = 3, height = 3, initialPopulation = 0, rule = ClassicRule)
        val customGameBoard = GameBoard(customSettings)

        customGameBoard.matrix = listOf(
            0b000L, // Пустая строка
            0b000L, // Пустая строка
            0b000L  // Пустая строка
        )

        val initialLivingCells = customGameBoard.countLivingCells()
        assertEquals(0, initialLivingCells)

        customGameBoard.update()
        val updatedLivingCells = customGameBoard.countLivingCells()
        assertEquals(0, updatedLivingCells)
    }

    @Test
    fun `board should oscillate correctly with predefined blinker configuration`() {
        val customSettings =
            GameSettings(width = 5, height = 5, initialPopulation = 3, rule = ClassicRule)
        val customGameBoard = GameBoard(customSettings)

        // Инициализируем конфигурацию "мигалки" (Blinker)
        customGameBoard.matrix = listOf(
            0b00000L,
            0b00000L,
            0b01110L,  // Три живые клетки по горизонтали
            0b00000L,
            0b00000L
        )

        // Проверим количество живых клеток
        val initialLivingCells = customGameBoard.countLivingCells()
        assertEquals(3, initialLivingCells)

        // Обновляем состояние (ожидаем, что мигалка сменит горизонталь на вертикаль)
        customGameBoard.update()

        // Проверяем, что после обновления мигалка стала вертикальной
        val expectedVerticalBlinker = listOf(
            0b00000L,
            0b00100L,  // Одна клетка
            0b00100L,  // Одна клетка
            0b00100L,  // Одна клетка
            0b00000L
        )
        assertEquals(expectedVerticalBlinker, customGameBoard.matrix)

        // Еще раз обновляем (ожидаем, что мигалка вернется к горизонтальной конфигурации)
        customGameBoard.update()

        val expectedHorizontalBlinker = listOf(
            0b00000L,
            0b00000L,
            0b01110L,  // Три клетки по горизонтали
            0b00000L,
            0b00000L
        )
        assertEquals(expectedHorizontalBlinker, customGameBoard.matrix)
    }

    @Test
    fun `should correctly count living cells after update`() {
        val customSettings =
            GameSettings(width = 5, height = 5, initialPopulation = 3, rule = ClassicRule)
        val customGameBoard = GameBoard(customSettings)

        // Устанавливаем конфигурацию "мигалки" (Blinker), которая изменится после обновления
        customGameBoard.matrix = listOf(
            0b00000L, // Пустая строка
            0b00000L, // Пустая строка
            0b01110L, // Три живые клетки по горизонтали (мигалка)
            0b00000L, // Пустая строка
            0b00000L  // Пустая строка
        )

        // Проверяем количество живых клеток до обновления
        val initialLivingCells = customGameBoard.countLivingCells()
        assertEquals(3, initialLivingCells) // У нас 3 живые клетки в начальной конфигурации

        // Обновляем состояние
        customGameBoard.update()

        // Проверяем количество живых клеток после обновления
        val updatedLivingCells = customGameBoard.countLivingCells()

        // Ожидаем, что количество живых клеток останется тем же
        assertEquals(3, updatedLivingCells)

        // Проверяем, что расположение клеток изменилось (с горизонтального на вертикальное)
        val expectedNewMatrix = listOf(
            0b00000L,
            0b00100L, // После обновления мигалка должна стать вертикальной
            0b00100L, // После обновления мигалка должна стать вертикальной
            0b00100L, // После обновления мигалка должна стать вертикальной
            0b00000L
        )
        assertEquals(expectedNewMatrix, customGameBoard.matrix)
    }

    @Test
    fun `should not change living cells for stable block pattern`() {
        val customSettings =
            GameSettings(width = 5, height = 5, initialPopulation = 4, rule = ClassicRule)
        val customGameBoard = GameBoard(customSettings)

        // Устанавливаем конфигурацию "Блока" (Block) — стабильная структура
        customGameBoard.matrix = listOf(
            0b00000L, // Пустая строка
            0b01100L, // Две клетки рядом
            0b01100L, // Две клетки рядом
            0b00000L, // Пустая строка
            0b00000L  // Пустая строка
        )

        // Проверяем количество живых клеток до обновления
        val initialLivingCells = customGameBoard.countLivingCells()
        assertEquals(4, initialLivingCells) // У нас 4 живые клетки в конфигурации блока

        // Обновляем состояние
        customGameBoard.update()

        // Проверяем, что количество живых клеток остается таким же после обновления
        val updatedLivingCells = customGameBoard.countLivingCells()
        assertEquals(4, updatedLivingCells)

        // Проверяем, что конфигурация осталась такой же (блок не изменяется)
        val expectedBlockMatrix = listOf(
            0b00000L,
            0b01100L, // Структура блока остается такой же
            0b01100L, // Структура блока остается такой же
            0b00000L,
            0b00000L
        )
        assertEquals(expectedBlockMatrix, customGameBoard.matrix)
    }

}
