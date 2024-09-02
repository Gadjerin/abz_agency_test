package com.abz.agency.testtask.ui.screen.users

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
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

    private val _users = mutableListOf<UserGet>()

    data class UiState(
        // MutableList to accumulate users from every request
        val users: List<UserGet> = listOf(),
        val isLoading: Boolean = false,
        val hasMoreUsers: Boolean = true
    )

    private var isLoading = false
    private var currentUsersPage = 1

    private var shouldUpdateTotalUsers = true
    private var totalUsers = -1


    var firstVisibleItemIndex = 0
        private set
    var firstVisibleItemScrollOffset = 0
        private set

    /**
     * Needed to save scroll position even through navigation.
     */
    fun saveUsersListScrollPosition(state: LazyListState) {
        firstVisibleItemIndex = state.firstVisibleItemIndex
        firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset
    }

    init {
        loadMoreUsers()
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
                        _users.addAll(users)
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
