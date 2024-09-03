package com.abz.agency.testtask.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abz.agency.testtask.ui.theme.BorderGray
import com.abz.agency.testtask.ui.theme.TesttaskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTextField(
    textState: MutableState<String>,
    supportingText: String = "",
    labelText: String = "",
    trailingIcon:  @Composable (()->Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    val interactionSource = remember{ MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.secondary
        else -> BorderGray
    }

    /*
        To create TextField with Label behavior of TextField and appearance of OutlinedTextField
        we need to use BasicTextField with DecorationBox.
     */
    BasicTextField(
        value = textState.value,
        {
            newText ->
            textState.value = newText
        },
        interactionSource = interactionSource,
        decorationBox = { innerBox ->
            TextFieldDefaults.DecorationBox(
                value = textState.value,
                innerTextField = innerBox,
                enabled = true,
                singleLine = true,
                isError = isError,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                label = {
                    Text(labelText)
                },
                supportingText = {
                    Text(supportingText)
                },
                trailingIcon = trailingIcon,
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = MaterialTheme.colorScheme.secondary
                ),
                container = {
                    Box(
                        modifier = Modifier.border(
                            if (!isError && isFocused)
                                OutlinedTextFieldDefaults.FocusedBorderThickness
                            else
                                OutlinedTextFieldDefaults.UnfocusedBorderThickness,
                            borderColor,
                            RoundedCornerShape(4.dp)
                        )
                    )
                }
            )
        },
        singleLine = true,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun PrimaryTextFieldPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrimaryTextField(
                remember {
                    mutableStateOf("")
                },
                labelText = "Label",
                supportingText = "Supporting text"
            )
            PrimaryTextField(
                remember {
                    mutableStateOf("Input")
                },
                labelText = "Label",
                supportingText = "Supporting text"
            )
            PrimaryTextField(
                remember {
                    mutableStateOf("")
                },
                isError = true,
                labelText = "Label",
                supportingText = "Supporting text"
            )
            PrimaryTextField(
                remember {
                    mutableStateOf("Input")
                },
                isError = true,
                labelText = "Label",
                supportingText = "Supporting text"
            )
        }
    }
}
