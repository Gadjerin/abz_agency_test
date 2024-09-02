package com.abz.agency.testtask.ui.screen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Delegate which declares how all ViewModels notify related Views with their states and events.
 */
interface UiStateDelegate<UiState, Event> {

    /**
     * Source of UI changes, Views take their states from here.
     */
    val uiState: StateFlow<UiState>

    /**
     * Source of single events like toasts, dialogs and navigation.
     */
    val singleEvents: Flow<Event>

    /**
     * Current UiState value, shortcut to `uiState.value`.
     */
    val stateValue: UiState

    /**
     * Reduce is a function that take the current state and an action as arguments,
     * and changed a new state result. In other words, (state: ViewState) => newState.
     */
    fun reduce(action: (state: UiState) -> UiState)

    /**
     * Sends single event to View.
     */
    suspend fun sendEvent(event: Event)
    fun CoroutineScope.asyncSendEvent(event: Event)
}

class UiStateDelegateImpl<UiState, Event>(
    initialUiState: UiState,
    eventsChannelCapacity: Int = Channel.BUFFERED
) : UiStateDelegate<UiState, Event> {

    private val stateFlow = MutableStateFlow(initialUiState)
    private val singleEventsChannel = Channel<Event>(eventsChannelCapacity)

    override val stateValue: UiState = stateFlow.value
    override val uiState: StateFlow<UiState> = stateFlow.asStateFlow()

    override val singleEvents: Flow<Event> = singleEventsChannel.receiveAsFlow()

    override fun reduce(action: (state: UiState) -> UiState) {
        stateFlow.update(action)
    }

    override suspend fun sendEvent(event: Event) {
        singleEventsChannel.send(event)
    }

    override fun CoroutineScope.asyncSendEvent(event: Event) {
        launch {
            sendEvent(event)
        }
    }
}
