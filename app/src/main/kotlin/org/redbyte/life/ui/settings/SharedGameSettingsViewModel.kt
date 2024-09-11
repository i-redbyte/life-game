package org.redbyte.life.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.redbyte.life.common.GameBoard
import org.redbyte.life.common.data.GameSettings
import org.redbyte.life.common.domain.ClassicRule

class SharedGameSettingsViewModel : ViewModel() {
    private var _settings: GameSettings? = null
    private val _gameBoard: MutableLiveData<GameBoard> = MutableLiveData()

    fun setupSettings(newSettings: GameSettings) {
        _settings = newSettings
        resetGameBoard()
    }

    fun resetGameBoard() {
        val settings = _settings ?: GameSettings(
            width = 32,
            height = 64,
            initialPopulation = 256,
            rule = ClassicRule
        )
        _gameBoard.value = GameBoard(settings)
    }

    fun getGameBoard(): GameBoard {
        return _gameBoard.value ?: resetGameBoardAndGet()
    }

    private fun resetGameBoardAndGet(): GameBoard {
        resetGameBoard()
        return _gameBoard.value ?: throw RuntimeException("Game board could not be reset")
    }

    fun getGameSettings(): GameSettings {
        return _settings ?: GameSettings(
            width = 32,
            height = 64,
            initialPopulation = 256,
            rule = ClassicRule
        )
    }
}



