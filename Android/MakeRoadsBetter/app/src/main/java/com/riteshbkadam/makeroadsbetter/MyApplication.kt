package com.riteshbkadam.makeroadsbetter

import android.app.Application
import android.preference.PreferenceManager
import org.osmdroid.config.Configuration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = applicationContext.packageName
    }
}