package no.uio.ifi.in2000.team19.prosjekt.ui.extendedAdvice


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.jeziellago.compose.markdowntext.MarkdownText
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.model.dto.Advice
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /** Displays in-depth advice information by adviceId.
         *
         * Uses this [markdown library](https://github.com/jeziellago/compose-markdown?tab=readme-ov-file)
         * We chose this to not be limited by technical formatting using compose. We found that we ended up trying to recreate libraries like
         * this one when trying to style our stringResources.
         *
         * In general this allows us to actually focus on content rather than implementing new composables for every screen
         * Of course this also limits us somewhat is custom interaction, but we ended up enjoying this because of the easy flexibility.
         *
         * */
fun AdviceScreen(adviceId: Int, navController: NavController, viewModel: HomeScreenViewModel) {

    val advice: Advice = viewModel.collectAdviceById(adviceId)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = advice.title) },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = stringResource(id = R.string.GoBack_icon_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(
                    top = innerPadding.calculateTopPadding() + 20.dp,
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 20.dp,
                    end = 20.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownText(markdown = advice.description)
        }
    }
}



