package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel

@Composable
fun NamesSetupScreen(viewModel: SetupScreenViewModel, onDone: () -> Unit) {


    val userInfo = viewModel.userInfo.collectAsStateWithLifecycle().value

    var userName by remember {
        mutableStateOf(userInfo.userName)
    }

    var dogName by remember {
        mutableStateOf(userInfo.dogName)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween

    ) {

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dog_normal),
                    contentDescription = stringResource(R.string.dog_normal_description),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(100.dp)
                )
            }




            Text(
                text = stringResource(R.string.what_is_your_name_and_dog_name_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userName,
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.icon_of_person_description)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                onValueChange = { userName = it },
                label = { Text(text = stringResource(R.string.your_name)) }
            )
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dogName,
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Pets, contentDescription = stringResource(
                            R.string.icon_of_pet_description
                        )
                    )
                },
                onValueChange = { dogName = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {

                        keyboardController?.hide()
                    }
                ),

                label = { Text(text = stringResource(R.string.your_dogs_name)) }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.why_store_name_disclaimer),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = (userName != "" && dogName != ""),

                onClick = {
                    viewModel.updateUserName(userName)
                    viewModel.updateDogName(dogName)
                    onDone()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = stringResource(R.string.next))
                Icon(Icons.Filled.ChevronRight, contentDescription = stringResource(R.string.next))
            }

            TextButton(
                onClick = {
                    viewModel.updateUserName("")
                    viewModel.updateDogName("")
                    onDone()
                }
            )
            {
                Text(text = stringResource(R.string.skip_this_step))
            }
        }
    }
}