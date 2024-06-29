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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun FurSetupScreen(viewModel: SetupScreenViewModel, onDone: () -> Unit) {

    val userInfo = viewModel.userInfo.collectAsStateWithLifecycle().value // should have moved this to parent composable and only sent in the uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.fur_setup_screen_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))


            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp),
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 10.dp,
                            bottom = 10.dp,
                        ),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.Center
                ) {

                    FilterChip(
                        text = stringResource(R.string.thinFur),
                        categoryName = "tynnPels",
                        viewModel,
                        userInfo.isThinHaired
                    )
                    FilterChip(
                        text = stringResource(R.string.thickFur),
                        categoryName = "tykkPels",
                        viewModel,
                        userInfo.isThickHaired
                    )
                    FilterChip(
                        text = stringResource(R.string.longFur),
                        categoryName = "langPels",
                        viewModel,
                        userInfo.isLongHaired
                    )
                    FilterChip(
                        text = stringResource(R.string.shortFur),
                        categoryName = "kortPels",
                        viewModel,
                        userInfo.isShortHaired
                    )
                    FilterChip(
                        text = stringResource(R.string.lightFur),
                        categoryName = "lysPels",
                        viewModel,
                        userInfo.isLightHaired
                    )
                    FilterChip(
                        text = stringResource(R.string.darkFur),
                        categoryName = "moerkPels",
                        viewModel,
                        userInfo.isDarkHaired
                    )

                }


            }

            Spacer(modifier = Modifier.padding(10.dp))

            TipBox(tipText = stringResource(R.string.fur_screen_tip_box))

            Spacer(modifier = Modifier.padding(10.dp))


        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {


            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    onDone()
                }
            ) {
                Text(text = stringResource(R.string.done))
            }
        }
    }
}


@Composable
fun FilterChip(
    text: String,
    categoryName: String,
    viewModel: SetupScreenViewModel,
    selected: Boolean
) {

    var isSelected by remember {
        mutableStateOf(selected)
    }

    androidx.compose.material3.FilterChip(
        modifier = Modifier
            .padding(8.dp),
        onClick = {
            isSelected = !isSelected
            viewModel.updateFilterCategories(categoryName, isSelected)
        },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        selected = isSelected,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done_icon_descirption),
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}


