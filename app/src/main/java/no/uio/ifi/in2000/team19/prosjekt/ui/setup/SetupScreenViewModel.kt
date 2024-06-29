package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.createTemporaryUserinfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class SetupScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private var _userInfo: MutableStateFlow<UserInfo> = MutableStateFlow(createTemporaryUserinfo())
    var userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _userInfo.value = settingsRepository.getUserInfo()
        }
    }

    // SCREEN ONE
    fun updateUserName(userName: String) {
        val updatedUserName = _userInfo.value.copy(userName = userName)
        _userInfo.value = updatedUserName
        Log.d("SETUP_DEBUG", _userInfo.value.userName)

    }

    fun updateDogName(dogName: String) {
        val updatedDogName = _userInfo.value.copy(dogName = dogName)
        _userInfo.value = updatedDogName

        Log.d("SETUP_DEBUG", _userInfo.value.dogName)
    }


    // SCREEN TWO
    private var _selectedAgeIndex: MutableStateFlow<Int?> =
        MutableStateFlow(null) // Null when none are chosen.
    var selectedAgeIndex: StateFlow<Int?> = _selectedAgeIndex.asStateFlow()

    fun updateAgeIndex(newIndex: Int) {
        _selectedAgeIndex.value = newIndex
    }

    fun updateIsPuppy(newValue: Boolean) {
        _userInfo.value.isPuppy = newValue
    }

    fun updateIsAdult(newValue: Boolean) {
        _userInfo.value.isAdult = newValue
    }

    fun updateIsSenior(newValue: Boolean) {
        _userInfo.value.isSenior = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isSenior.toString())
    }


    // SCREEN THREE

    private var _selectedNoseIndex: MutableStateFlow<Int?> =
        MutableStateFlow(null) // Null when none are chosen.
    var selectedNoseIndex: StateFlow<Int?> = _selectedNoseIndex.asStateFlow()

    fun updateIsFlatNosed(newValue: Boolean) {
        _userInfo.value.isFlatNosed = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isFlatNosed.toString())
    }

    fun updateIsNormalNosed(newBoolean: Boolean) {
        _userInfo.value.isNormalNosed = newBoolean
    }

    fun updateNoseIndex(newIndex: Int) {
        _selectedNoseIndex.value = newIndex
    }


    // SCREEN FOUR

    private var _selectedBodyIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    var selectedThinIndex: StateFlow<Int?> = _selectedBodyIndex.asStateFlow()

    fun updateIsThin(newValue: Boolean) {
        _userInfo.value.isThin = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isThin.toString())
    }

    fun updateIsMedium(newValue: Boolean) {
        _userInfo.value.isMediumBody = newValue
    }

    fun updateIsThick(newValue: Boolean) {
        _userInfo.value.isThickBody = newValue
    }

    fun updateBodyIndex(newIndex: Int) {
        _selectedBodyIndex.value = newIndex
    }


    // SCREEN FIVE
    fun updateFilterCategories(categoryName: String, newValue: Boolean) {
        when (categoryName) {
            "tynnPels" -> {
                _userInfo.value.isThinHaired = newValue
            }

            "tykkPels" -> {
                _userInfo.value.isThickHaired = newValue
            }

            "langPels" -> {
                _userInfo.value.isLongHaired = newValue
            }

            "kortPels" -> {
                _userInfo.value.isShortHaired = newValue
            }

            "lysPels" -> {
                _userInfo.value.isLightHaired = newValue
            }

            "moerkPels" -> {
                _userInfo.value.isDarkHaired = newValue
            }
        }
    }


    // SAVE EVERY CHOICE. Only done at the end to make sure the user finnished the Setup process.
    fun saveUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                settingsRepository.updateUserInfo(_userInfo.value)
                Log.d("SETUP_DEBUG", _userInfo.value.toString())
            } catch (e: IOException) {
                println(e)
                Log.d("SETUP_DEBUG", e.toString())
            }
        }
    }

    fun handleUserSkip() {
        val defaultUserInfo = UserInfo(
            id = 0,
            userName = "",
            dogName = "",
            isSenior = true,
            isAdult = false,
            isPuppy = false,
            isFlatNosed = true,
            isNormalNosed = false,
            isThin = true,
            isMediumBody = false,
            isThickBody = false,
            isLongHaired = true,
            isShortHaired = true,
            isThinHaired = true,
            isThickHaired = true,
            isLightHaired = true,
            isDarkHaired = true,
        )
        _userInfo.value = defaultUserInfo
        saveUserInfo()
        saveSetupState(true)
    }

    fun saveSetupState(isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveSetupState(isCompleted)
        }
    }

    fun updateSelectedIndexesBasedOnUserData() {
        val ui = userInfo.value
        Log.d("debug", ui.toString())

        if (ui.isSenior) updateAgeIndex(2)
        else if (ui.isAdult) updateAgeIndex(1)
        else if (ui.isPuppy) updateAgeIndex(0)

        if (ui.isNormalNosed) updateNoseIndex(0)
        else if (ui.isFlatNosed) updateNoseIndex(1)

        if (ui.isThin) updateBodyIndex(0)
        else if (ui.isMediumBody) updateBodyIndex(1)
        else if (ui.isThickBody) updateBodyIndex(2)

    }
}