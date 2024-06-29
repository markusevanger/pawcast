package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.createTemporaryUserinfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import javax.inject.Inject


data class SettingsUiState(
    var userInfo: UserInfo
)


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(
        SettingsUiState(
            createTemporaryUserinfo()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun fetchUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.userInfo = settingsRepository.getUserInfo()
        }
    }
}


