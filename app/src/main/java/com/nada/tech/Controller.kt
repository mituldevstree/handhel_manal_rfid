package com.nada.tech

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.nada.tech.utility.ActivityLifeCycle
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Controller : Application(), LifecycleObserver {
    private var activityLifeCycle = ActivityLifeCycle()

    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(activityLifeCycle)
    }

    fun getActivityLifeCycle(): ActivityLifeCycle {
        return activityLifeCycle
    }

    fun getCurrentActivity(): Activity? {
        return activityLifeCycle.currentActivity
    }

    companion object {
        lateinit var instance: Controller
    }
}

