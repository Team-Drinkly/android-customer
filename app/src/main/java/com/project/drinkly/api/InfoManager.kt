package com.project.drinkly.api

import android.content.Context
import android.content.SharedPreferences

class InfoManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)

    fun saveUserNickname(nickName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("nickName", nickName)
        editor.apply()
    }

    fun getUserNickname(): String? {
        return sharedPreferences.getString("nickName", null)
    }

    fun saveUserId(userId: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong("userId", userId)
        editor.apply()
    }

    fun getUserId(): Long? {
        return sharedPreferences.getLong("userId", 0)
    }

    fun saveIsSubscribe(isSubscribe: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isSubscribe", isSubscribe)
        editor.apply()
    }

    fun getIsSubscribe(): Boolean? {
        return sharedPreferences.getBoolean("isSubscribe", false)
    }

    fun saveSubscribeLeftDays(leftDays: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("leftDays", leftDays)
        editor.apply()
    }

    fun getSubscribeLeftDays(): Int? {
        return sharedPreferences.getInt("leftDays", 0)
    }

    fun saveSubscribeDays(startDate: String, expiredDate: String) {
        val editor = sharedPreferences.edit()
        editor.putString("startDate", startDate)
        editor.putString("expiredDate", expiredDate)
        editor.apply()
    }

    fun getStartDate(): String? {
        return sharedPreferences.getString("startDate", "")
    }

    fun getExpiredDate(): String? {
        return sharedPreferences.getString("expiredDate", "")
    }
}