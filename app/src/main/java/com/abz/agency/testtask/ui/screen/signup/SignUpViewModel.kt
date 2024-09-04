package com.abz.agency.testtask.ui.screen.signup

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abz.agency.testtask.model.api.Position
import com.abz.agency.testtask.model.api.UserPost
import com.abz.agency.testtask.model.api.UsersApi
import com.abz.agency.testtask.model.data.UsersRepository
import com.abz.agency.testtask.ui.screen.UiStateDelegate
import com.abz.agency.testtask.ui.screen.UiStateDelegateImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.abz.agency.testtask.ui.screen.signup.SignUpViewModel.Event
import com.abz.agency.testtask.ui.screen.signup.SignUpViewModel.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.UnknownHostException

@HiltViewModel
class SignUpViewModel @Inject constructor(
    // We need context to read image file and also validate it
    private val application: Application,
    private val usersRepository: UsersRepository
) : AndroidViewModel(application), UiStateDelegate<UiState, Event> by UiStateDelegateImpl(UiState()) {
    companion object {
        const val TAG = "SignUpViewModel"

        private const val PHONE_FORMAT_PLACEHOLDER = "+38 (XXX) XXX - XX - XX"
    }

    init {
        loadPositions()
    }

    sealed interface Event {
        data object NavigateToNoInternet : Event
        data object UserRegisteredSuccessfully : Event
        data class ValidationFailed(val message: String) : Event
        data object NoOpenPositions: Event
    }

    data class UiState(
        val name: FieldState<String> = FieldState(""),
        val email: FieldState<String> = FieldState(""),
        val phone: FieldState<String> = FieldState(
            "", PHONE_FORMAT_PLACEHOLDER
        ),
        val positions: OptionButtonGroupState<Int> = OptionButtonGroupState(-1),
        val photo: FieldState<Uri> = FieldState(Uri.EMPTY),
        val isLoading: Boolean = false
    )

    data class FieldState<T>(
        val value: T,
        val supportingText: String = "",
        val isError: Boolean = false
    )

    data class OptionButtonState<T>(
        val value: T,
        val label: String
    )

    private fun positionToOptionButtonState(position: Position): OptionButtonState<Int> {
        return OptionButtonState(position.id, position.name)
    }

    private fun positionsToOptionButtonStates(positions: List<Position>):
            List<OptionButtonState<Int>> {
        return positions.map(this::positionToOptionButtonState)
    }

    data class OptionButtonGroupState<T>(
        val selectedValue: T,
        val buttonsState: List<OptionButtonState<T>> = emptyList()
    )

    private fun loadPositions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val positions =  usersRepository.getPositions()
                val firstPosition = positions.firstOrNull()

                if (firstPosition == null) {
                    sendEvent(Event.NoOpenPositions)
                    return@launch
                }

                val newPositionsOptionButtonStates = positionsToOptionButtonStates(positions)

                reduce { uiState ->
                    uiState.copy(
                        positions = OptionButtonGroupState(
                            firstPosition.id,
                            newPositionsOptionButtonStates
                        )
                    )
                }

            } catch (e: UnknownHostException) {
                Log.e(TAG, e.toString())
                sendEvent(Event.NavigateToNoInternet)
            } catch (e: UsersApi.PositionsRequestException) {
                Log.e(TAG, e.toString())
                sendEvent(Event.NoOpenPositions)
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        phone: String,
        positionId: Int,
        photo: Uri
    ) {
        var isValidationFailed = false

        val newNameState = when {
            name.isBlank() -> {
                isValidationFailed = true
                FieldState(name, ValidationError.FieldIsRequired.message, true)
            }
            !isNameValid(name) -> {
                isValidationFailed = true
                FieldState(name, ValidationError.WrongNameLength.message, true)
            }
            else -> FieldState(name)
        }

        val newEmailState = when {
            email.isBlank() -> {
                isValidationFailed = true
                FieldState(email, ValidationError.FieldIsRequired.message, true)
            }
            !isEmailValid(email) -> {
                isValidationFailed = true
                FieldState(email, ValidationError.WrongEmailFormat.message, true)
            }
            else -> FieldState(email)
        }

        val newPhoneState = when {
            phone.isBlank() -> {
                isValidationFailed = true
                FieldState(phone, ValidationError.FieldIsRequired.message, true)
            }
            !isPhoneValid(phone) -> {
                isValidationFailed = true
                FieldState(phone, ValidationError.WrongPhoneFormat.message, true)
            }
            else -> FieldState(phone, PHONE_FORMAT_PLACEHOLDER)
        }

        val newPhotoState = when {
            photo == Uri.EMPTY -> {
                isValidationFailed = true
                FieldState(photo, ValidationError.PhotoIsRequired.message, true)
            }
            !isPhotoValid(photo) -> {
                isValidationFailed = true
                FieldState(photo, ValidationError.InvalidPhoto.message, true)
            }
            else -> FieldState(photo)
        }

        reduce { uiState ->
            uiState.copy(
                name = newNameState,
                email = newEmailState,
                phone = newPhoneState,
                positions = uiState.positions.copy(
                    selectedValue = positionId
                ),
                photo = newPhotoState
            )
        }

        if (!isValidationFailed) {
            viewModelScope.launch(Dispatchers.IO) {
                val imageBytes = getImageByteArrayFromFile(photo)
                if (imageBytes == null) {
                    reduce { uiState ->
                        uiState.copy(
                            photo = FieldState(
                                photo,
                                ValidationError.PhotoIsRequired.message,
                                true
                            )
                        )
                    }
                    return@launch
                }

                reduce {
                    it.copy(isLoading = true)
                }

                try {
                    usersRepository.insertUser(
                        UserPost(
                            name,
                            email,
                            phone,
                            positionId,
                            imageBytes
                        )
                    )
                    sendEvent(Event.UserRegisteredSuccessfully)
                    // Users successfully registered
                    // Clear all fields except positions, set isLoading to false
                    reduce {
                        UiState(
                            // Set selected position to first, we for sure know it exists
                            positions = it.positions.copy(
                                selectedValue = it.positions.buttonsState[0].value
                            )
                        )
                    }
                } catch (e: UsersApi.UsersRequestSingleErrorException) {
                    sendEvent(Event.ValidationFailed(e.message ?: "Unknown error"))
                    reduce {
                        it.copy(isLoading = false)
                    }
                } catch (e: UsersApi.UsersRequestMultipleErrorsException) {
                    sendEvent(Event.ValidationFailed(e.message ?: "Unknown error"))
                    reduce {
                        it.copy(isLoading = false)
                    }
                } catch (e: UnknownHostException) {
                    sendEvent(Event.NavigateToNoInternet)
                    reduce {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun isNameValid(name: String): Boolean {
        return name.length in 2..60
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPhoneValid(phone: String): Boolean {
        val regex = "^\\+380\\d{9}$".toRegex()
        return regex.matches(phone)
    }

    private fun isPhotoValid(imageUri: Uri): Boolean {
        // Validate dimensions
        val (width, height) = getImageDimensions(imageUri) ?: return false
        if (width < 70 || height < 70) {
            return false
        }

        val fileSize = getFileSize(imageUri)
        // Validate file size (5MB = 5 * 1024 * 1024 bytes)
        return fileSize != -1L && fileSize <= 5 * 1024 * 1024
    }

    private enum class ValidationError(
        val message: String
    ) {
        FieldIsRequired("Required field"),
        WrongNameLength("Should be 2-60 characters"),
        WrongEmailFormat("Invalid email format"),
        WrongPhoneFormat("Invalid phone format"),
        PhotoIsRequired("Photo is required"),
        InvalidPhoto("Photo must be at least 70x70px, less then 5MB")
    }

    private fun getImageByteArrayFromFile(imageUri: Uri): ByteArray? {
        val inputStream: InputStream = try {
            application.contentResolver.openInputStream(imageUri)
                ?: return null
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.toString())
            return null
        }

        val buf = ByteArray(inputStream.available())

        while (inputStream.read(buf) != -1);

        inputStream.close()
        return buf
    }

    private fun getImageDimensions(imageUri: Uri): Pair<Int, Int>? {
        val inputStream = application.contentResolver.openInputStream(imageUri) ?: return null
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // Don't actually load the bitmap, only its options
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        return Pair(options.outWidth, options.outHeight)
    }

    private fun getFileSize(imageUri: Uri): Long {
        val cursor = application.contentResolver.query(
            imageUri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )
        return cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            it.getLong(sizeIndex)
        } ?: -1L
    }
}