package com.nada.tech.utility

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

class ActivityLifeCycle : ActivityLifecycleCallbacks {
    var currentActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity = null
    }

    override fun onActivityStopped(activity: Activity) {
        // don't clear current activity because activity may get stopped after the new activity is resumed
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        // don't clear current activity because activity may get destroyed after the new activity is resumed
    }

    fun isCallActive(): Boolean {
//        if (currentActivity is JitsiMeetActivity) return true
        return false
    }
}