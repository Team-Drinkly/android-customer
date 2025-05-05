package com.project.drinkly.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun setFCMToken(token: String) {
        preferences.edit().putString("FCM_TOKEN", token).apply()
    }

    fun getFCMToken(): String? =
        preferences.getString("FCM_TOKEN", null)

    fun setNotificationPermissionChecked(value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean("notification_permission_checked", value)
        editor.apply()
    }

    fun isNotificationPermissionChecked(): Boolean {
        return preferences.getBoolean("notification_permission_checked", false)
    }
}