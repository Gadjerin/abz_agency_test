package com.abz.agency.testtask.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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

@Preview(showBackground = true)
@Composable
fun PrimaryTopAppBarPreview() {
    TesttaskTheme {
        PrimaryTopAppBar(title = "Working with POST request")
    }
}
