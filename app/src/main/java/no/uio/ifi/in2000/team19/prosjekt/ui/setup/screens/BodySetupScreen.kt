package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.TipBox

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BodySetupScreen(viewModel: SetupScreenViewModel, onDone: () -> Unit) {

    val thinIndex = viewModel.selectedThinIndex.collectAsStateWithLifecycle().value



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,

        ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_normal),
                contentDescription = stringResource(id = R.string.dog_normal_description),
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(125.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(2f),
        ) {

            Text(
                text = stringResource(R.string.body_setup_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))


            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .weight(1f)
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (thinIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),
                    onClick = {
                        viewModel.updateBodyIndex(0)
                        viewModel.updateIsThin(true)
                        viewModel.updateIsMedium(false)
                        viewModel.updateIsThick(false)
                        onDone()
                    },

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = stringResource(R.string.skinnyBody))
                    }

                }


                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(125.dp)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (thinIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateBodyIndex(1)
                        viewModel.updateIsThin(false)
                        viewModel.updateIsMedium(true)
                        viewModel.updateIsThick(false)
                        onDone()
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = stringResource(R.string.mediumBody))
                    }

                }

                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .weight(1f)

                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (thinIndex == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateBodyIndex(2)
                        viewModel.updateIsThin(false)
                        viewModel.updateIsMedium(false)
                        viewModel.updateIsThick(true)
                        onDone()
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = stringResource(R.string.fatBody))
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))

            TipBox(tipText = stringResource(R.string.body_setup_screen_tip))

            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text = stringResource(R.string.chooseDogCategoryBottomScreenTip),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }


    }
}