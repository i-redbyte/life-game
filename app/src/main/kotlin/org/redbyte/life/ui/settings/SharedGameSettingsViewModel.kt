package org.redbyte.life.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.redbyte.life.common.domain.ClassicRule
import org.redbyte.life.common.GameBoard
import org.redbyte.life.common.data.GameSettings

class SharedGameSettingsViewModel : ViewModel() {
    private var _gameBoard: GameBoard? = null
    private val _settings = MutableLiveData(GameSettings())
    private val settings: LiveData<GameSettings> = _settings

    fun setupSettings(newSettings: GameSettings) {
        _settings.value = newSettings
        resetGameBoard(newSettings)
    }

    fun resetGameBoard(newSettings: GameSettings? = null) {
        val gameSettings =
            newSettings ?: settings.value ?: throw RuntimeException("Game settings cannot be null")
        _gameBoard = GameBoard(gameSettings, ClassicRule)
    }

    fun getGameBoard(): GameBoard = _gameBoard ?: resetGameBoardAndGet()

    private fun resetGameBoardAndGet(): GameBoard {
        val gameSettings = settings.value ?: throw RuntimeException("Game settings cannot be null")
        _gameBoard = GameBoard(gameSettings, ClassicRule)
        return _gameBoard as GameBoard
    }
}

