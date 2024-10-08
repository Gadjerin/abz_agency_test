package com.abz.agency.testtask.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abz.agency.testtask.R
import com.abz.agency.testtask.ui.screen.nointernet.NoInternetScreen
import com.abz.agency.testtask.ui.screen.signup.SignUpScreen
import com.abz.agency.testtask.ui.screen.signup.SignUpViewModel
import com.abz.agency.testtask.ui.screen.users.UsersScreen

/**
 * Holds composables of every `Destination`.
 *
 * @see Destination
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Destination
) {
    // To have same through screen transitions
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route
    ) {
        composable(
            Destination.Users.route
        ) {
            UsersScreen(
                navigateToNoInternet = {
                    navController.navigate(Destination.NoInternet.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            Destination.SignUp.route
        ) {
            SignUpScreen(
                viewModel = signUpViewModel,
                navigateToNoInternet = {
                    navController.navigate(Destination.NoInternet.route) {
                        launchSingleTop = true
                    }
                },
                navigateToUsers = {
                    navController.navigate(Destination.Users.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Destination.NoInternet.route) {
            NoInternetScreen(
                navigateToUsers = {
                    navController.navigate(Destination.Users.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

/**
 * Represents all possible app destinations and their additional parameters.
 * To check current destination properties you could get `Destination` instance
 * by using `Destination.valueOf(value: String)` where value is `Destination.route`.
 *
 * @param title text to appear in TopAppBar
 * @param shouldShowAppBars are TopAppBar and BottomNavBar should be shown on the destination
 * @param bottomNavEntry should be present if you want to add this destination to BottomAppBar
 *
 * @see BottomNavEntry
 */
enum class Destination(
    val title: String = "",
    val shouldShowAppBars: Boolean = true,
    val bottomNavEntry: BottomNavEntry? = null
) {
    Users(
        title = "Working with GET request",
        bottomNavEntry = BottomNavEntry("Users", R.drawable.people_icon)
    ),
    SignUp(
        title = "Working with POST request",
        bottomNavEntry = BottomNavEntry("Sign up", R.drawable.person_add_icon)
    ),
    NoInternet(shouldShowAppBars = false);

    val route = toString()
}

/**
 * @param title label of BottomNavBar destination
 * @param iconResource icon resource of BottomNavBar destination
 *
 * @see Destination
 */
data class BottomNavEntry(val title: String, @DrawableRes val iconResource: Int)
