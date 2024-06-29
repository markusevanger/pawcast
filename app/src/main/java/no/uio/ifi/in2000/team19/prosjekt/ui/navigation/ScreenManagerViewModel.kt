package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import javax.inject.Inject


@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _navBarSelectedIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    var navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

    fun updateNavBarSelectedIndex(newIndex: Int) {
        _navBarSelectedIndex.value = newIndex
    }

    private val _startDestination: MutableStateFlow<String> = MutableStateFlow("home")
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreRepository.readSetupState().collect { completed ->

                if (completed) {
                    _startDestination.value = "home"
                } else {
                    _startDestination.value = "setup/{STAGE}"
                }

            }
        }
    }

}