package com.crafttechlabs.android.fixlastmodifieddate

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * Created by sky on 3/1/18.
 *
 * Main Application for the app
 */
class FixLastModifiedDateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}