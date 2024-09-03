package com.abz.agency.testtask

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    companion object {
        const val PHONE_NUMBER_LENGTH = 13
    }
}
