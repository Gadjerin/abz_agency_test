package com.abz.agency.testtask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abz.agency.testtask.ui.theme.Blue
import com.abz.agency.testtask.ui.theme.DarkBlue
import com.abz.agency.testtask.ui.theme.Orange
import com.abz.agency.testtask.ui.theme.TesttaskTheme
import com.abz.agency.testtask.ui.theme.TransparentBlackDisabled
import com.abz.agency.testtask.ui.theme.TransparentBlackEnabled

/*
    To make buttons exactly as in design - custom ripple theme must be provided
 */
private class CustomRippleTheme(val defaultColor: Color, val rippleAlpha: RippleAlpha) : RippleTheme {
    @Composable
    override fun defaultColor() = defaultColor

    @Composable
    override fun rippleAlpha(): RippleAlpha = rippleAlpha
}

/**
 * Ripple for MainButton to make it orange when pressed.
 */
private val MainButtonRipple = CustomRippleTheme(
    defaultColor = Orange,
    rippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 1f)
)

/**
 * Ripple for SecondaryButton to make it transient blue when pressed.
 */
private val SecondaryButtonRipple = CustomRippleTheme(
    defaultColor = Blue,
    rippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.1f)
)

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier, text: String = "", enabled: Boolean = true, onClick: () -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelLarge

    CompositionLocalProvider(LocalRippleTheme provides MainButtonRipple) {
        Button(
            modifier = modifier
                .defaultMinSize(minHeight = 48.dp)
                .width(140.dp)
                .drawWithContent {
                    val textLayoutResult = textMeasurer.measure(
                        text = text,
                        style = textStyle
                    )
                    drawContent()
                    drawText(
                        textLayoutResult,
                        /*
                            Text color according to design, we need to set it to text directly
                            because of Ripple overlap behavior.
                        */
                        color = if (enabled) TransparentBlackEnabled else TransparentBlackDisabled,
                        topLeft = Offset(
                            x = (size.width - textLayoutResult.size.width) / 2,
                            y = (size.height - textLayoutResult.size.height) / 2,
                        )
                    )
                },
            enabled = enabled,
            onClick = onClick,
        ) {}
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier, text: String = "", enabled: Boolean = true, onClick: () -> Unit
) {
    CompositionLocalProvider(LocalRippleTheme provides SecondaryButtonRipple) {
        TextButton(
            modifier = modifier
                .defaultMinSize(
                    minWidth = 87.dp, minHeight = 40.dp
                ),
            enabled = enabled,
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = DarkBlue,
                disabledContentColor = TransparentBlackDisabled
            )
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrimaryButton(text = "Normal") {}
            PrimaryButton(text = "Disabled", enabled = false) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondaryButtonPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SecondaryButton(text = "Normal") {}
            SecondaryButton(text = "Disabled", enabled = false) {}
        }
    }
}
