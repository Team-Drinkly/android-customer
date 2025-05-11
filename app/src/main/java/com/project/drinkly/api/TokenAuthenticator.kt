package com.project.drinkly.api

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import okhttp3.Response

class TokenAuthenticator(private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val newAccessToken = TokenUtil.syncRefreshToken(context) ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }
}
