package com.abz.agency.testtask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abz.agency.testtask.ui.theme.TesttaskTheme

/**
 * Custom RadioButton with the theme from the design.
 *
 * @param value represents value which is set when RadioButton is selected
 * @param groupSelectedState current selected state in RadioButton group
 */
@Composable
fun <T> PrimaryRadioButton(value: T, groupSelectedState: MutableState<T>) {
    RadioButton(
        selected = value == groupSelectedState.value,
        onClick = {
            groupSelectedState.value = value
        },
        colors = RadioButtonDefaults.colors(
            selectedColor = MaterialTheme.colorScheme.secondary,
            unselectedColor = MaterialTheme.colorScheme.secondary,
        ),
    )
}

/**
 * Custom RadioButton with the theme from the design and its Label.
 *
 * @see PrimaryRadioButton
 */
@Composable
fun <T> PrimaryRadioButtonLabeled(
    value: T,
    groupSelectedState: MutableState<T>,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryRadioButton(value = value, groupSelectedState = groupSelectedState)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryRadioButtonPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val radioGroupState = remember{ mutableIntStateOf(1) }
            PrimaryRadioButton(value = 1, groupSelectedState = radioGroupState)
            PrimaryRadioButton(value = 2, groupSelectedState = radioGroupState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryRadioButtonLabeledPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val radioGroupState = remember{ mutableIntStateOf(1) }
            PrimaryRadioButtonLabeled(value = 1, groupSelectedState = radioGroupState, "Designer")
            PrimaryRadioButtonLabeled(value = 2, groupSelectedState = radioGroupState, "QA")
        }
    }
}
