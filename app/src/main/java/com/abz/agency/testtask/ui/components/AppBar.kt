package com.abz.agency.testtask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.abz.agency.testtask.ui.Destination
import com.abz.agency.testtask.ui.theme.BottomBarGray
import com.abz.agency.testtask.ui.theme.TesttaskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTopAppBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(title, style = MaterialTheme.typography.displayLarge)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

/**
 * Bottom navigation bar.
 * There are displayed destinations from Destination enum which has bottomNavEntry.
 * For its content configuration you only need to change Destination enum.
 *
 * @see Destination
 */
@Composable
fun BottomNavBar(
    currentDestination: Destination,
    navController: NavHostController
) {
    NavigationBar(
        containerColor = BottomBarGray
    ) {
        Destination.entries.forEach { destination ->
            // Draw only destinations which supposed to have appearance in BottomBar
            destination.bottomNavEntry?.let { bottomNavEntry ->
                val selected = currentDestination == destination
                NavigationBarItem(
                    onClick = {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Surface(
                            color = Color.Transparent,
                            contentColor = if (selected)
                                MaterialTheme.colorScheme.secondary
                            else
                                Color.Black.copy(alpha = 0.6F)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    painterResource(id = bottomNavEntry.iconResource),
                                    "Navigation icon",
                                )
                                Text(bottomNavEntry.title)
                            }
                        }
                    },
                    // Always false because else there is additional styles which can't be disabled
                    selected = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryTopAppBarPreview() {
    TesttaskTheme {
        PrimaryTopAppBar(title = "Working with POST request")
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    TesttaskTheme {
        BottomNavBar(
            currentDestination = Destination.Users,
            navController = rememberNavController()
        )
    }
}
