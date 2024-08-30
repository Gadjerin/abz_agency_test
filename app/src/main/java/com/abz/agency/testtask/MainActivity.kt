package com.abz.agency.testtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abz.agency.testtask.ui.AppNavHost
import com.abz.agency.testtask.ui.Destination
import com.abz.agency.testtask.ui.components.BottomNavBar
import com.abz.agency.testtask.ui.components.PrimaryTopAppBar
import com.abz.agency.testtask.ui.theme.TesttaskTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If no internet then it would be Destination.NoInternet
        val startDestination = Destination.Users

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
