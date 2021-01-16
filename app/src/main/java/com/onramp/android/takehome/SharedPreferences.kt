package com.onramp.android.takehome

import android.content.Context
import android.location.Location
import androidx.core.content.edit

internal object LocationSharedPref {

    const val FOREGROUND_ENABLED = "tracking_foreground_location"

    fun getLocationTrackingPref(context: Context): Boolean =
            context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    .getBoolean(FOREGROUND_ENABLED, false)

    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
            context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE).edit() {
                putBoolean(FOREGROUND_ENABLED, requestingLocationUpdates)
            }

}
