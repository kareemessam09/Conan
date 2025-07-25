package com.example.conan

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ConanApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
