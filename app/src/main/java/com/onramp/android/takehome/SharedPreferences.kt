package com.onramp.android.takehome

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit

internal object UserSharedPref {

    val LOGGEDIN = "loggedin"

    fun getSharedPref(context: Context): Boolean =
            context.getSharedPreferences(
                    "userInfo", Context.MODE_PRIVATE
            ).getBoolean(LOGGEDIN, false)


    fun saveUserPref(context: Context, username: String) =
            context.getSharedPreferences(
                    "userInfo",
                    Context.MODE_PRIVATE).edit() {
                putString("username", username)
            }

}
