package com.onramp.android.takehome

class MainPresenter(
        view: MainContract.View

) : MainContract.Presenter{

    private var view: MainContract.View? = view

    override fun start(){

        view?.setUpUIFirstTimeLogin()

        view?.createSnackbar()

    }
}