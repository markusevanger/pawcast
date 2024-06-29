package no.uio.ifi.in2000.team19.prosjekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team19.prosjekt.ui.navigation.ScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.navigation.ScreenManagerViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Team19prosjektoppgaveTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            Team19prosjektoppgaveTheme {
                ScreenManager(
                    viewModel = hiltViewModel<ScreenManagerViewModel>(), // viewmodel for navbar and Scaffold.
                )
            }
        }
    }
}
