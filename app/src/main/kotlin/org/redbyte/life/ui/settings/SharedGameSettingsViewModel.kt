package org.redbyte.life.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.redbyte.life.common.GameBoard
import org.redbyte.life.common.data.GameSettings

class SharedGameSettingsViewModel : ViewModel() {
    private lateinit var _settings: GameSettings
    private val _gameBoard: MutableLiveData<GameBoard> = MutableLiveData()

    fun setupSettings(newSettings: GameSettings) {
        _settings = newSettings
        resetGameBoard()
    }

    fun resetGameBoard() {
        if (!::_settings.isInitialized) {
            throw RuntimeException("Game settings must be initialized before resetting the game board")
        }
        _gameBoard.value = GameBoard(_settings)
    }

    fun getGameBoard(): GameBoard {
        if (!::_settings.isInitialized) {
            throw RuntimeException("Game settings must be initialized")
        }
        return _gameBoard.value ?: resetGameBoardAndGet()
    }

    private fun resetGameBoardAndGet(): GameBoard {
        resetGameBoard()
        return _gameBoard.value as GameBoard
    }
}


