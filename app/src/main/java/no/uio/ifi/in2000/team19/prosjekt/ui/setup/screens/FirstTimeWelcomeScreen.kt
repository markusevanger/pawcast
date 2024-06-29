package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Measurements


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(onDone: () -> Unit, onSkip: () -> Unit) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Measurements.HorizontalPadding.measurement + 40.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {


        Image(
            painter = painterResource(id = R.drawable.dog_normal),
            contentDescription = stringResource(id = R.string.dog_normal_description)
        )

        Text(
            text = stringResource(R.string.first_time_greeting),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

        Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))

        Text(
            text = stringResource(R.string.start_configuring_app),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

        Button(onClick = onDone) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = stringResource(R.string.configutr_app_button_text))
                Icon(
                    imageVector = Icons.Filled.Start,
                    contentDescription = stringResource(R.string.dog_paw_icon_description)
                )
            }
        }


        var showBottomSkipModal by remember { mutableStateOf(false) }
        TextButton(onClick = { showBottomSkipModal = true }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = stringResource(R.string.skip_config_button_text))
                Icon(
                    imageVector = Icons.Outlined.FastForward,
                    contentDescription = stringResource(R.string.skip_symbol_description)
                )

            }
        }



        if (showBottomSkipModal) {
            ModalBottomSheet(onDismissRequest = { showBottomSkipModal = false }) {

                Column(
                    modifier = Modifier.padding(Measurements.HorizontalPadding.measurement)
                ) {
                    Text(
                        text = stringResource(R.string.skip_modal_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.skip_modal_body),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.skip_modal_skip_button),
                                textAlign = TextAlign.Center
                            )
                        }
                        Button(
                            onClick = onDone, modifier = Modifier
                                .weight(1f)
                                .height(IntrinsicSize.Max)
                        ) {
                            Text(
                                text = stringResource(R.string.configure_button_text),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))
                }


            }
        }
    }

}

