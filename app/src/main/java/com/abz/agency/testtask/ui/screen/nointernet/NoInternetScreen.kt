package com.abz.agency.testtask.ui.screen.nointernet

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abz.agency.testtask.R
import com.abz.agency.testtask.ui.components.PrimaryButton
import com.abz.agency.testtask.ui.screen.nointernet.InternetConnectionViewModel.Event
import kotlinx.coroutines.launch

@Composable
fun NoInternetScreen(
    viewModel: InternetConnectionViewModel = viewModel(),
    navigateToUsers: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        launch {
            viewModel.singleEvents.collect { event ->
                when(event) {
                    is Event.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is Event.NavigateToUsers -> navigateToUsers.invoke()
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painterResource(R.drawable.no_internet_sign),
                "No internet sign",
                modifier = Modifier.size(200.dp),
            )
            Text(
                "There is no internet connection",
                style = MaterialTheme.typography.displayLarge
            )
            PrimaryButton(text = "Try again") {
                viewModel.checkInternetConnection()
            }
        }
    }
}
