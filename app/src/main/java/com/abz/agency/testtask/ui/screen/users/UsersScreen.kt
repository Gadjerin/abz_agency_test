package com.abz.agency.testtask.ui.screen.users

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abz.agency.testtask.R
import com.abz.agency.testtask.ui.components.DeveloperCard
import kotlinx.coroutines.launch
import com.abz.agency.testtask.ui.screen.users.UsersViewModel.Event
import kotlinx.coroutines.delay

@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel(),
    navigateToNoInternet: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(key1 = Unit) {
        launch {
            while (uiState.users.isEmpty()) {
                // If there are no users then we should busy wait for them,
                // to avoid stucking in the same state,
                // after first users were loaded this coroutine no more needed
                viewModel.loadMoreUsers()
                delay(3000)
            }
        }
        launch {
            viewModel.singleEvents.collect { event ->
                when(event) {
                    is Event.NavigateToNoInternet -> navigateToNoInternet.invoke()
                }
            }
        }
    }

    when {
        uiState.users.isEmpty() && uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        uiState.users.isNotEmpty() -> {
            val listState = rememberLazyListState(
                viewModel.firstVisibleItemIndex,
                viewModel.firstVisibleItemScrollOffset
            )
            // Save scroll state for better UX
            LaunchedEffect(listState) {
                snapshotFlow {
                    listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
                }.collect { (_, _) ->
                    viewModel.saveUsersListScrollPosition(listState)
                }
            }
            LazyColumn(
                state = listState
            ) {
                item {
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                }
                items(
                    items = uiState.users,
                    key = {
                        it.email
                    }
                ) {
                    DeveloperCard(
                        name = it.name,
                        position = it.position,
                        email = it.email,
                        phoneNumber = it.phone,
                        imageUrl = it.photo
                    )
                }
                if (uiState.hasMoreUsers) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                    }
                }
                item {
                    // If LaunchedEffect enters composition then we need to load more users
                    LaunchedEffect(key1 = Unit) {
                        Log.d("LaunchedEffect", "user has reached the end, loading more data")
                        viewModel.loadMoreUsers()
                    }
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        painterResource(R.drawable.no_users_placeholder),
                        "No users image",
                        modifier = Modifier.size(200.dp),
                    )
                    Text(
                        "There are no users yet",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        }
    }
}
