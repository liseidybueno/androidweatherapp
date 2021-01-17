package com.onramp.android.takehome

import android.location.Location

interface MainContract {

    interface View {

        fun setUpUILoggedIn(user: String, units: String)

        fun setUpUINotLoggedIn()

        fun createSnackbar()

        fun checkFields(): Boolean

        fun startNewActivity(location: Location)

        fun getPermissions()

        fun getName(): String

        fun changeUiOnBtnClick(user: String, units: String)

        fun getUnits(): String

    }

    interface Presenter {

        fun start()

        fun startLoggedIn(user: String, units: String)

        fun onBtnClick()

        fun startWeatherActivity()

    }

    interface Model {

    }
}