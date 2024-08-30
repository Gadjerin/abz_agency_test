package com.abz.agency.testtask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abz.agency.testtask.ui.AppNavHost
import com.abz.agency.testtask.ui.Destination
import com.abz.agency.testtask.ui.components.BottomNavBar
import com.abz.agency.testtask.ui.components.PrimaryTopAppBar
import com.abz.agency.testtask.ui.screen.nointernet.InternetConnectionViewModel
import com.abz.agency.testtask.ui.theme.TesttaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val internetViewModel: InternetConnectionViewModel by viewModels()

        val startDestination = if (internetViewModel.isInternetAvailable()) {
            Log.d(TAG, "Internet available, starting screen is Users")
            Destination.Users
        }
        else {
            Log.d(TAG, "Internet unavailable, starting screen is NoInternet")
            Destination.NoInternet
        }

        setContent {
            TesttaskTheme {
                val navController = rememberNavController()
                // if current route is null then we on startDestination
                val currentDestination = navController
                    .currentBackStackEntryAsState().value?.
                    destination?.route?.let { Destination.valueOf(it) } ?: startDestination

                Scaffold(
                    topBar = {
                        if (currentDestination.shouldShowAppBars) {
                            PrimaryTopAppBar(title = currentDestination.title)
                        }
                    },
                    bottomBar = {
                        if (currentDestination.shouldShowAppBars) {
                            BottomNavBar(
                                currentDestination = currentDestination,
                                navController = navController
                            )
                        }
                    }
                ) { scaffoldPaddingValues ->
                    AppNavHost(
                        modifier = Modifier.padding(scaffoldPaddingValues),
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
