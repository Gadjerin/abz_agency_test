package com.abz.agency.testtask.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.abz.agency.testtask.R
import com.abz.agency.testtask.ui.theme.TesttaskTheme
import com.abz.agency.testtask.ui.theme.TransparentBlackDisabled
import java.io.File

/**
 * After user will choose a file or take a photo its `uri` will be stored in `imageUri`.
 *
 * @param imageUri uri of chosen image
 */
@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    imageUri: MutableState<Uri>,
    isError: Boolean = false,
    supportingText: String = "",
) {
    val context = LocalContext.current

    val newImageUri = remember {
        mutableStateOf(Uri.EMPTY)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Toast.makeText(context, "Loaded successfully", Toast.LENGTH_SHORT).show()
            imageUri.value = it
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            Toast.makeText(context, "Loaded successfully", Toast.LENGTH_SHORT).show()
            imageUri.value = newImageUri.value
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(newImageUri.value)
        } else {
            Toast.makeText(context, "Camera permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    val showImagePickerBottomSheet = remember { mutableStateOf(false) }
    if (showImagePickerBottomSheet.value) {
        ImagePickerBottomSheet(
            onDismiss = {
                showImagePickerBottomSheet.value = false
            },
            onCameraClick = {
                newImageUri.value = createCachedImage(context)
                val permission = Manifest.permission.CAMERA

                if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(newImageUri.value)
                }
                else {
                    cameraPermissionLauncher.launch(permission)
                }

                showImagePickerBottomSheet.value = false
            },
            onGalleryClick = {
                chooseJpegFromGallery(galleryLauncher)
                showImagePickerBottomSheet.value = false
            }
        )
    }

    ImagePickerBody(
        modifier,
        isError,
        supportingText,
        onClick = {
            showImagePickerBottomSheet.value = true
        }
    )
}

private fun chooseJpegFromGallery(launcher: ActivityResultLauncher<String>) {
    launcher.launch("image/jpeg")
}

private fun createCachedImage(context: Context): Uri {
    val tmpImageFile = File.createTempFile("tmp", ".jpg", context.externalCacheDir)

    return FileProvider.getUriForFile(
        context, "${context.packageName}.provider", tmpImageFile
    )
}

/**
 * Default visible part of ImagePicker.
 */
@Composable
private fun ImagePickerBody(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String = "",
    onClick: () -> Unit
) {
    val colors = if (isError) {
        OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.error,
            disabledLabelColor = MaterialTheme.colorScheme.error,
            disabledSupportingTextColor = MaterialTheme.colorScheme.error,
        )
    }
    else {
        OutlinedTextFieldDefaults.colors()
    }
    // Have to use OutlinedTextField instead fo PrimaryTextField due to
    // exclusive colors behavior for disabled widget.
    // Usually disabled state colors have more priority then error colors,
    // and we need to correct it.
    OutlinedTextField(
        modifier = modifier,
        value = "", onValueChange = {},
        enabled = false,
        isError = isError,
        label = {
            Text("Upload your photo")
        },
        trailingIcon = {
            SecondaryButton(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "Upload",
                onClick = onClick
            )
        },
        colors = colors,
        supportingText = {
            Text(supportingText)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImagePickerBottomSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                "Choose how you want to add a photo",
                style = MaterialTheme.typography.bodyLarge,
                color = TransparentBlackDisabled
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(112.dp),
            ) {
                ImageButtonWithBottomLabel(
                    "Camera",
                    imageResource = R.drawable.camera_detailed_icon,
                    onClick = onCameraClick
                )
                ImageButtonWithBottomLabel(
                    "Gallery",
                    imageResource = R.drawable.galery_detailed_icon,
                    onClick = onGalleryClick
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ImageButtonWithBottomLabel(
    label: String,
    @DrawableRes imageResource: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick.invoke()
            }
            .padding(8.dp)
    ) {
        Image(
            painterResource(imageResource), contentDescription = "icon",
            modifier = Modifier.size(56.dp)
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagePickerPreview() {
    TesttaskTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ImagePicker(
                imageUri = remember {
                    mutableStateOf(Uri.EMPTY)
                }
            )
            ImagePicker(
                imageUri = remember {
                    mutableStateOf(Uri.EMPTY)
                },
                isError = true,
                supportingText = "Photo is required"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IconButtonWithBottomLabelPreview() {
    TesttaskTheme {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ImageButtonWithBottomLabel(
                "Camera",
                imageResource = R.drawable.camera_detailed_icon,
                onClick = {}
            )
            ImageButtonWithBottomLabel(
                "Gallery",
                imageResource = R.drawable.galery_detailed_icon,
                onClick = {}
            )
        }
    }
}
