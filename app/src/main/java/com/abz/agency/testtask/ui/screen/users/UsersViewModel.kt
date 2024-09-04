package com.abz.agency.testtask.ui.screen.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abz.agency.testtask.model.api.UserGet
import com.abz.agency.testtask.model.api.UsersApi
import com.abz.agency.testtask.model.data.UsersRepository
import com.abz.agency.testtask.ui.screen.UiStateDelegate
import com.abz.agency.testtask.ui.screen.UiStateDelegateImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.abz.agency.testtask.ui.screen.users.UsersViewModel.UiState
import com.abz.agency.testtask.ui.screen.users.UsersViewModel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel(), UiStateDelegate<UiState, Event> by UiStateDelegateImpl(UiState()) {
    companion object {
        const val TAG = "UsersViewModel"
    }

    sealed interface Event {
        data object NavigateToNoInternet : Event
    }

    // MutableList to accumulate users from every request
    private val _users = mutableListOf<UserGet>()

    data class UiState(
        val users: List<UserGet> = listOf(),
        val isLoading: Boolean = false,
        val hasMoreUsers: Boolean = true
    )

    private var isLoading = false
    private var currentUsersPage = 1

    private var shouldUpdateTotalUsers = true
    private var totalUsers = -1

    /*
        This method is needed because if user is scrolling users list while another user POSTs new user
        then users count became larger and we could accidentally load and add already existing user because
        next page then will contain one user from previous page.

        Example (count = 3):
            Step 1 -
                Load page 1: {user1, user2, user3} // We loaded them and next load is gonna be from page 2
            Step 2 -
                Someone added userN, so now on the server are {userN, user1, user2, user3}
            Step 3 -
                Load page 2: {user3, user4, user5} // But we already had loaded user3
     */
    private fun MutableList<UserGet>.addUnique(newUser: UserGet) {
        if (none { it.id == newUser.id }) {
            this.add(newUser)
        }
    }

    fun loadMoreUsers() {
        if (!isLoading) {
            isLoading = true
            reduce { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                runCatching {
                    if (shouldUpdateTotalUsers) {
                        try {
                            // In case if exception totalUsers stays -1 which is expected behavior
                            val newTotalUsers = usersRepository.getUsersCount()

                            if (newTotalUsers > totalUsers) {
                                reduce { uiState ->
                                    uiState.copy(
                                        hasMoreUsers = true
                                    )
                                }
                            }

                            totalUsers = newTotalUsers
                            shouldUpdateTotalUsers = false
                        } catch (e: UsersApi.UsersRequestSingleErrorException) {
                            // No users case
                            Log.e(TAG, e.toString())
                            reduce { uiState ->
                                uiState.copy(
                                    isLoading = false,
                                    hasMoreUsers = false
                                )
                            }
                            // We don't set isFirstLoad false to check again if someone had added new users
                            // And then initialize totalUsers
                            isLoading = false
                            return@launch
                        }
                    }

                    // Check haven't we already loaded all users
                    if (totalUsers > _users.size) {
                        val users = usersRepository.getUsers(currentUsersPage)
                        totalUsers = usersRepository.getUsersCount() // For future requests
                        currentUsersPage++ // Move to next page only if request succeed
                        users.forEach {
                            _users.addUnique(it)
                        }
                        reduce { uiState ->
                            uiState.copy(
                                users = _users,
                                isLoading = false
                            )
                        }
                    }
                    else {
                        Log.d(TAG, "All users have been loaded")
                        reduce { uiState ->
                            uiState.copy(
                                isLoading = false,
                                hasMoreUsers = false
                            )
                        }
                        // then total users will be loaded again on next request
                        shouldUpdateTotalUsers = true
                    }

                    isLoading = false
                }.onFailure {
                    // Separate handling because could be thrown by both
                    // usersRepository.getUsersCount() and usersRepository.getUsers()
                    if (it is UnknownHostException) {
                        viewModelScope.asyncSendEvent(
                            Event.NavigateToNoInternet
                        )
                        reduce { uiState ->
                            uiState.copy(
                                isLoading = false
                            )
                        }
                        isLoading = false
                    }
                }
            }
        }
    }
}
