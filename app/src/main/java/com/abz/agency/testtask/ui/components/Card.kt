package com.abz.agency.testtask.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abz.agency.testtask.R
import com.abz.agency.testtask.ui.theme.TesttaskTheme
import com.abz.agency.testtask.ui.theme.TransparentBlackEnabled
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DeveloperCard(
    name: String,
    position: String,
    email: String,
    phoneNumber: String,
    imageUrl: String? = null
) {
    ListItem(
        leadingContent = {
            GlideImage(
                model = imageUrl,
                contentDescription = "Developer photo",
                loading = placeholder(R.drawable.photo_placeholder),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        },
        headlineContent = {
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                color = TransparentBlackEnabled
            )
        },
        supportingContent = {
            Column {
                // Vertical padding is 2*2
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    position,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.6F)
                )
                // Vertical padding is 4*2
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    email,
                    style = MaterialTheme.typography.bodySmall,
                    color = TransparentBlackEnabled,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                // Vertical padding is 2*2
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    formatPhoneNumber(phoneNumber),
                    style = MaterialTheme.typography.bodySmall,
                    color = TransparentBlackEnabled
                )
                // Space between the last Text and Divider is 24.dp according to the design
                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                HorizontalDivider()
            }
        },
    )
}

/**
 * Turns +380982787624 to +38 (098) 278 76 24
 */
private fun formatPhoneNumber(phoneNumber: String): String {
    // Ignore numbers of wrong size
    if (phoneNumber.length != 13)
        return phoneNumber

    return "%s (%s) %s %s %s".format(
        // +38
        phoneNumber.slice(
            0..2
        ),
        // 098
        phoneNumber.slice(
            3..5
        ),
        // 278
        phoneNumber.slice(
            6..8
        ),
        // 76
        phoneNumber.slice(
            9..10
        ),
        // 24
        phoneNumber.slice(
            11..12
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun DeveloperCardPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeveloperCard(
                "Malcolm Bailey",
                "Frontend developer",
                "jany_murazik51@hotmail.com",
                "+380982787624"
            )
            DeveloperCard(
                "Seraphina Anastasia Isolde Aurelia Celestina von Hohenzollern",
                "Backend developer",
                "maximus_wilderman_ronaldo_schuppe@gmail.com",
                "+380982787624"
            )
        }
    }
}
