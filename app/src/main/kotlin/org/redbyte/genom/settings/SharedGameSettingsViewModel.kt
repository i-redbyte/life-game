package org.redbyte.genom.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.redbyte.genom.common.GameBoard
import org.redbyte.genom.common.data.GameSettings

class SharedGameSettingsViewModel : ViewModel() {
    private var gameBoard: GameBoard? = null
    private val _settings = MutableLiveData(GameSettings())
    private val settings: LiveData<GameSettings> = _settings

    fun setupSettings(newSettings: GameSettings) {
        _settings.value = newSettings
        gameBoard = GameBoard(newSettings)
    }

    fun getGameBoard(): GameBoard = gameBoard ?: GameBoard(
        settings.value ?: throw RuntimeException("Game settings cannot be null")
    )
}
