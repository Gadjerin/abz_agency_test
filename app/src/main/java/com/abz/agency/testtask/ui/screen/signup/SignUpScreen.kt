package com.abz.agency.testtask.ui.screen.signup

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.abz.agency.testtask.HiltApplication.Companion.PHONE_NUMBER_LENGTH
import com.abz.agency.testtask.R
import com.abz.agency.testtask.ui.components.ImagePicker
import com.abz.agency.testtask.ui.components.PrimaryButton
import com.abz.agency.testtask.ui.components.PrimaryRadioButtonLabeled
import com.abz.agency.testtask.ui.components.PrimaryTextField
import kotlinx.coroutines.launch
import com.abz.agency.testtask.ui.screen.signup.SignUpViewModel.Event

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel =  hiltViewModel(),
    navigateToNoInternet: () -> Unit,
    navigateToUsers: () -> Unit
) {
    val showFullScreenDialog = remember { mutableStateOf<FullScreenDialogData?>(null) }

    LaunchedEffect(key1 = Unit) {
        launch {
            viewModel.singleEvents.collect { event ->
                when(event) {
                    is Event.NavigateToNoInternet -> navigateToNoInternet.invoke()
                    is Event.UserRegisteredSuccessfully -> {
                        showFullScreenDialog.value = FullScreenDialogData(
                            "User successfully registered",
                            "Got it",
                            R.drawable.registered_successfully_sign,
                            onButtonClick = navigateToUsers,
                            onDismiss = {
                                showFullScreenDialog.value = null
                            }
                        )
                    }
                    is Event.ValidationFailed -> {
                        showFullScreenDialog.value = FullScreenDialogData(
                            event.message,
                            "Try again",
                            R.drawable.registration_failed_sign,
                            // Button click causes onDismiss call, no need to set `showDialogData.value = false`
                            onButtonClick = {},
                            onDismiss = {
                                showFullScreenDialog.value = null
                            }
                        )
                    }
                    is Event.NoOpenPositions -> {
                        showFullScreenDialog.value = FullScreenDialogData(
                            "No open positions for now, try later",
                            "Back to `Users`",
                            R.drawable.registration_failed_sign,
                            // Button click causes onDismiss call, no need to set `showDialogData.value = false`
                            onButtonClick = navigateToUsers,
                            onDismiss = {
                                showFullScreenDialog.value = null
                                navigateToUsers.invoke()
                            }
                        )
                    }
                }
            }
        }
    }

    showFullScreenDialog.value?.let {
        FullScreenDialog(
            dialogData = it,
        )
    }

    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            // Scroll to handle situation if there are to much positions
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val name = remember { mutableStateOf(uiState.name.value) }
        val email = remember { mutableStateOf(uiState.email.value) }
        val phone = remember { mutableStateOf(uiState.phone.value) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),

            // 12.dp because supporting takes 20.dp, together 32.dp as in design
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val focus = LocalFocusManager.current
            PrimaryTextField(
                textState = name,
                labelText = "Your name",
                supportingText = uiState.name.supportingText,
                isError = uiState.name.isError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // If email is already good, just hide ime
                        if (uiState.email.isError || email.value.isBlank()) {
                            focus.moveFocus(FocusDirection.Down)
                        }
                        else {
                            defaultKeyboardAction(ImeAction.Done)
                        }
                    }
                ),
                maxLength = 60
            )
            PrimaryTextField(
                textState = email,
                labelText = "Email",
                supportingText = uiState.email.supportingText,
                isError = uiState.email.isError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // If phone is already good, just hide ime
                        if (uiState.phone.isError || phone.value.isBlank()) {
                            focus.moveFocus(FocusDirection.Down)
                        }
                        else {
                            defaultKeyboardAction(ImeAction.Done)
                        }
                    }
                ),
                maxLength = 80
            )
            PrimaryTextField(
                textState = phone,
                labelText = "Phone",
                supportingText = uiState.phone.supportingText,
                isError = uiState.phone.isError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                ),
                maxLength = PHONE_NUMBER_LENGTH
            )
        }

        val selectedPosition = remember { mutableIntStateOf(uiState.positions.selectedValue) }
        LaunchedEffect(key1 = uiState.positions) {
            // To be able update remembered state when positions will be loaded
            selectedPosition.intValue = uiState.positions.selectedValue
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select your position", style = MaterialTheme.typography.bodyMedium)
            AnimatedVisibility(visible = uiState.positions.buttonsState.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.positions.buttonsState.forEach { optionButtonState ->
                        PrimaryRadioButtonLabeled(
                            value = optionButtonState.value,
                            groupSelectedState = selectedPosition,
                            label = optionButtonState.label
                        )
                    }
                }
            }
        }

        val imageUri = remember { mutableStateOf(uiState.photo.value) }
        Column(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImagePicker(
                modifier = Modifier.fillMaxWidth(),
                imageUri = imageUri,
                supportingText = uiState.photo.supportingText,
                isError = uiState.photo.isError
            )

            PrimaryButton(
                text = "Sign up",
                enabled = !uiState.isLoading
            ) {
                viewModel.signUp(
                    name.value,
                    email.value,
                    phone.value,
                    selectedPosition.intValue,
                    imageUri.value
                )
            }
        }
    }
}

private data class FullScreenDialogData(
    val message: String,
    val buttonText: String,
    @DrawableRes val imageRes: Int,
    val onButtonClick: () -> Unit,
    val onDismiss: () -> Unit
)

@Composable
private fun FullScreenDialog(
    dialogData: FullScreenDialogData,
) {
    Dialog(
        onDismissRequest = dialogData.onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp),
                    onClick = dialogData.onDismiss
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Close cross"
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        painterResource(dialogData.imageRes),
                        "Dialog image",
                        modifier = Modifier.size(200.dp)
                    )
                    Text(dialogData.message, style = MaterialTheme.typography.displayLarge)
                    PrimaryButton(
                        text = dialogData.buttonText,
                        onClick = {
                            dialogData.onButtonClick.invoke()
                            dialogData.onDismiss.invoke()
                        }
                    )
                }
            }
        }
    }
}