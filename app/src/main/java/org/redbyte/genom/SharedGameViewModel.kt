package org.redbyte.genom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.redbyte.genom.data.GameSettings

class SharedGameViewModel : ViewModel() {
    private val _gameSettings = MutableLiveData<GameSettings?>()
    val gameSettings: LiveData<GameSettings?> = _gameSettings

    fun setGameSettings(settings: GameSettings) {
        _gameSettings.value = settings
    }
}