package com.abz.agency.testtask.ui.screen.nointernet

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abz.agency.testtask.ui.screen.UiStateDelegate
import com.abz.agency.testtask.ui.screen.UiStateDelegateImpl
import com.abz.agency.testtask.ui.screen.nointernet.InternetConnectionViewModel.Event

class InternetConnectionViewModel(private val application: Application) : AndroidViewModel(application),
    UiStateDelegate<Unit, Event> by UiStateDelegateImpl(Unit) {

    sealed interface Event {
        data object NavigateToUsers : Event
        data class ShowToast(val message: String): Event
    }

    fun checkInternetConnection() {
        if (isInternetAvailable()) {
            viewModelScope.asyncSendEvent(Event.NavigateToUsers)
        }
        else {
            viewModelScope.asyncSendEvent(Event.ShowToast("Network is still unavailable"))
        }
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}