package com.onramp.android.takehome

interface MainContract {

    interface View {

        fun setUpUIFirstTimeLogin()

        fun createSnackbar()


    }

    interface Presenter {

        fun start()

    }

    interface Model {

    }
}