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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoseSetupScreen(viewModel: SetupScreenViewModel, onDone: () -> Unit) {

    val noseIndex = viewModel.selectedNoseIndex.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
            modifier = Modifier.weight(2f),

            ) {
            Text(
                text = stringResource(R.string.nose_setup_screen_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))


            FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .weight(1f)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (noseIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateNoseIndex(0)
                        viewModel.updateIsFlatNosed(false)
                        viewModel.updateIsNormalNosed(true)
                        onDone()
                    },

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = stringResource(R.string.normal_nose))
                    }

                }


                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .weight(1f)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (noseIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateNoseIndex(1)
                        viewModel.updateIsFlatNosed(true)
                        viewModel.updateIsNormalNosed(false)
                        onDone()
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = stringResource(R.string.flat_nose))
                    }
                }
            }

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
